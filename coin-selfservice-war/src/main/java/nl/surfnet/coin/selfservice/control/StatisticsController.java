/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.selfservice.control;

import au.com.bytecode.opencsv.CSVWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import org.apache.commons.lang.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.surfnet.cruncher.Cruncher;
import org.surfnet.cruncher.model.LoginData;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

import static java.lang.String.format;
import static java.util.Calendar.YEAR;

/**
 * Controller for statistics
 */
@Controller
@RequestMapping(value = "/stats/*")
public class StatisticsController extends BaseController {

  private static final String DATE_PATTERN = "dd-MM-yyyy";
  private static final Logger LOG = LoggerFactory.getLogger(StatisticsController.class);
  /**
   * Key for the selectedSp in the model
   */
  private static final String SELECTED_SP = "selectedSp";

  @Resource
  private Cruncher cruncher;

  @RequestMapping("/stats.shtml")
  public String stats(ModelMap model,
                      @RequestParam(value = "spEntityId", required = false) final String selectedSp, HttpServletRequest request) {
    InstitutionIdentityProvider selectedIdp = getSelectedIdp(request);
    model.put(SELECTED_IDP, selectedIdp);
    model.put(SELECTED_SP, selectedSp);

    // default return all statistics for the last two years
    Calendar twoYearsBack = Calendar.getInstance();
    twoYearsBack.roll(YEAR, -2);
    try {
      if (StringUtils.isNotBlank(selectedSp)) {
        model.put("login_stats", cruncher.getLoginsByIdpAndSp(twoYearsBack.getTime(), new Date(), selectedIdp.getId(), selectedSp));
      } else {
        model.put("login_stats", cruncher.getLoginsByIdp(twoYearsBack.getTime(), new Date(), selectedIdp.getId()));
      }
    } catch (RuntimeException e) {
      LOG.warn("exception while contacting cruncher", e);
      return "stats/nostats";
    }
    return "stats/statistics";
  }

  @RequestMapping(value = "/stats.csv", method = RequestMethod.GET)
  public void csvStats(@RequestParam(value = "startDateAsMillis") long startDateAsMillis,
                       @RequestParam(value = "intervalType") String intervalType,
                       @RequestParam(value = "spEntityId", required = false) final String spEntityId,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
    InstitutionIdentityProvider selectedIdp = getSelectedIdp(request);
    LocalDate startDate = new LocalDate(startDateAsMillis);
    LocalDate endDate = getEndDate(intervalType, startDate);
    List<LoginData> loginData = getLoginData(spEntityId, selectedIdp, startDate, endDate);
    List<String[]> result = createCSVRows(loginData);
    response.setContentType("Content-Type: application/csv");
    response.setHeader("Content-Disposition", format("attachment; filename=%s-%s.csv", startDate.toString(DATE_PATTERN), intervalType));
    try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
      writer.writeAll(result);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  private List<String[]> createCSVRows(List<LoginData> loginData) {
    List<String[]> csvRows = Lists.transform(loginData, new Function<LoginData, String[]>() {
      @Override
      public String[] apply(LoginData input) {
        List<String> result = new ArrayList<>();
        result.add(input.getIdpEntityId());
        result.add(input.getIdpname());
        result.add(input.getSpEntityId());
        result.add(toLocalDate(input.getPointStart()).toString(DATE_PATTERN));
        result.add(toLocalDate(input.getPointEnd()).toString(DATE_PATTERN));
        result.addAll(Lists.transform(input.getData(), new Function<Integer, String>() {
          public String apply(Integer input) {
            return input.toString();
          }
        }));
        return result.toArray(new String[result.size()]);
      }
    });
    HeaderRow headerRow = new HeaderRow(loginData);
    List<String[]> result = new ArrayList<>(csvRows);
    result.add(0, headerRow.toCsv());
    return result;
  }

  private List<LoginData> getLoginData(String spEntityId, InstitutionIdentityProvider selectedIdp, LocalDate startDate, LocalDate endDate) throws IOException {
    String statistics;
    if (spEntityId == null) {
      statistics = cruncher.getLoginsByIdp(startDate.toDate(), endDate.toDate(), selectedIdp.getId());
    } else {
      statistics = cruncher.getLoginsByIdpAndSp(startDate.toDate(), endDate.toDate(), selectedIdp.getId(), spEntityId);
    }
    return Arrays.asList(new ObjectMapper().readValue(statistics, LoginData[].class));
  }

  private LocalDate getEndDate(String intervalType, LocalDate startDate) {
    LocalDate endDate;
    switch (intervalType) {
      case "week":
        endDate = startDate.dayOfWeek().withMaximumValue();
        break;
      case "month":
        endDate = startDate.dayOfMonth().withMaximumValue();
        break;
      case "year":
        endDate = startDate.dayOfYear().withMaximumValue();
        break;
      default:
        throw new IllegalArgumentException(intervalType);
    }
    return endDate;
  }

  private static LocalDate toLocalDate(long pointStart) {
    return LocalDate.fromDateFields(new Date(pointStart));
  }

  private final static class HeaderRow {

    private final List<LoginData> loginDatas;

    public HeaderRow(List<LoginData> loginDatas) {
      this.loginDatas = loginDatas;
    }

    public String[] toCsv() {
      List<String> headerRow = new ArrayList<>();
      headerRow.addAll(Arrays.asList("idpEntityId", "idpName", "spEntityId", "startDate", "endDate"));
      if(!loginDatas.isEmpty()) {

        LocalDate startDate = toLocalDate(loginDatas.get(0).getPointStart());
        LocalDate endDate = toLocalDate(loginDatas.get(0).getPointEnd());
        Days daysBetween = Days.daysBetween(startDate, endDate);
        headerRow.add(startDate.toString(DATE_PATTERN));
        for (int i = 0; i < daysBetween.getDays(); i++) {
          headerRow.add(startDate.plusDays(i + 1).toString(DATE_PATTERN));
        }
      }

      return headerRow.toArray(new String[headerRow.size()]);
    }

  }
}
