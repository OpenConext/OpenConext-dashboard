package nl.surfnet.coin.selfservice.api.rest;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.gson.Gson;
import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.surfnet.cruncher.Cruncher;
import org.surfnet.cruncher.model.LoginData;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

import static java.lang.String.format;
import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

@Controller
@RequestMapping(value = "/stats")
public class StatsController extends BaseController {
  private static final String DATE_PATTERN = "dd-MM-yyyy";
  private static final Logger LOG = LoggerFactory.getLogger(StatsController.class);

  @Resource
  private Cruncher cruncher;

  @Resource
  private Csa csa;

  @RequestMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RestResponse> get(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                          @RequestParam("start") @DateTimeFormat(pattern = "yyyyMMdd") Date start,
                                          @RequestParam("end") @DateTimeFormat(pattern = "yyyyMMdd") Date end,
                                          @PathVariable long id) {
    LocalDate startDate = new LocalDate(start);
    LocalDate endDate = new LocalDate(end);

    Service service = csa.getServiceForIdp(idpEntityId, id);
    LoginData loginData = getLoginDataFromCruncher(startDate, endDate, idpEntityId, service.getSpEntityId());

    Map<Long, Integer> graphData = new LinkedHashMap<>();
    int microsecondsInSecond = 1000;

    for (int i = 0; i < loginData.getTotal(); i++) {
      graphData.put((loginData.getPointStart() + i * loginData.getPointInterval()) / microsecondsInSecond, loginData.getData().get(i));
    }

    return new ResponseEntity(createRestResponse(graphData), HttpStatus.OK);
  }

  @RequestMapping(value = "/id/{id}/download", produces = "text/csv")
  public ResponseEntity<RestResponse> download(@RequestParam("idpEntityId") String idpEntityId,
                                               @RequestParam("start") @DateTimeFormat(pattern = "yyyyMMdd") Date start,
                                               @RequestParam("end") @DateTimeFormat(pattern = "yyyyMMdd") Date end,
                                               @PathVariable long id,
                                               HttpServletResponse response) {
    LocalDate startDate = new LocalDate(start);
    LocalDate endDate = new LocalDate(end);

    Service service = csa.getServiceForIdp(idpEntityId, id);
    LoginData loginData = getLoginDataFromCruncher(startDate, endDate, idpEntityId, service.getSpEntityId());

    List<String> headerRow = new ArrayList<>();
    List<String> valueRow = new ArrayList<>();
    headerRow.addAll(Arrays.asList("idpEntityId", "idpName", "spEntityId", "startDate", "endDate"));
    valueRow.addAll(Arrays.asList(idpEntityId, service.getName(), service.getSpEntityId(), startDate.toString(DATE_PATTERN), endDate.toString(DATE_PATTERN)));

    for (int i = 0; i < loginData.getTotal(); i++) {
      headerRow.add(new LocalDate(loginData.getPointStart() + i * loginData.getPointInterval()).toString(DATE_PATTERN));
      valueRow.add(loginData.getData().get(i).toString());
    }

    response.setHeader("Content-Disposition", format("attachment; filename=%s-%s.csv", startDate.toString(DATE_PATTERN), endDate.toString(DATE_PATTERN)));

    try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
      writer.writeAll(Arrays.asList(headerRow.toArray(new String[headerRow.size()]), valueRow.toArray(new String[valueRow.size()])));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new ResponseEntity(HttpStatus.OK);
  }

  private LoginData getLoginDataFromCruncher(LocalDate start, LocalDate end, String idpEntityId, String spEntityId) {
    LOG.debug("Getting cruncher data for idp {} and sp {} - from {} to {}", idpEntityId, spEntityId, start.toString("yyyyMMdd"), end.toString("yyyyMMdd"));

    String crazyJsonString = cruncher.getLoginsByIdpAndSp(start.toDate(), end.toDate(), idpEntityId, spEntityId);
    LOG.debug("JSON data from cruncher: {}", crazyJsonString);
    return new Gson().fromJson(crazyJsonString, LoginData[].class)[0];
  }
}
