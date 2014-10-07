package nl.surfnet.coin.selfservice.api.rest;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.GraphData;
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

  @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RestResponse> get(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                          @RequestParam("start") @DateTimeFormat(pattern = "yyyyMMdd") Date start,
                                          @RequestParam("end") @DateTimeFormat(pattern = "yyyyMMdd") Date end) {
    LocalDate startDate = new LocalDate(start);
    LocalDate endDate = new LocalDate(end);

    List<GraphData> graphData = getGraphDataFromCruncher(startDate, endDate, idpEntityId);

    return new ResponseEntity(createRestResponse(graphData), HttpStatus.OK);
  }

  @RequestMapping(value = "/download", produces = "text/csv")
  public ResponseEntity<RestResponse> download(@RequestParam("idpEntityId") String idpEntityId,
                                               @RequestParam("start") @DateTimeFormat(pattern = "yyyyMMdd") Date start,
                                               @RequestParam("end") @DateTimeFormat(pattern = "yyyyMMdd") Date end,
                                               HttpServletResponse response) {
    LocalDate startDate = new LocalDate(start);
    LocalDate endDate = new LocalDate(end);

    List<GraphData> graphData = getGraphDataFromCruncher(startDate, endDate, idpEntityId);

    return respondWithCsv(response, graphData, format("%s-%s.csv", startDate.toString(DATE_PATTERN), endDate.toString(DATE_PATTERN)));
  }

  @RequestMapping(value = "/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RestResponse> get(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                          @RequestParam("start") @DateTimeFormat(pattern = "yyyyMMdd") Date start,
                                          @RequestParam("end") @DateTimeFormat(pattern = "yyyyMMdd") Date end,
                                          @PathVariable long id) {
    LocalDate startDate = new LocalDate(start);
    LocalDate endDate = new LocalDate(end);

    Service service = csa.getServiceForIdp(idpEntityId, id);
    List<GraphData> graphData = getGraphDataFromCruncher(startDate, endDate, idpEntityId, service.getSpEntityId());

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
    List<GraphData> graphData = getGraphDataFromCruncher(startDate, endDate, idpEntityId, service.getSpEntityId());

    return respondWithCsv(response, graphData, format("%s-%s.csv", startDate.toString(DATE_PATTERN), endDate.toString(DATE_PATTERN)));
  }

  private ResponseEntity respondWithCsv(HttpServletResponse response, List<GraphData> graphData, String filename) {
    response.setHeader("Content-Disposition", format("attachment; filename=%s", filename));

    try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
      if (!graphData.isEmpty()) {
        writer.writeNext(graphData.get(0).getCsvHeaders());

        writer.writeAll(Lists.transform(graphData, new Function<GraphData, String[]>() {
          @Override
          public String[] apply(GraphData graphData) {
            return graphData.getCsvRow();
          }
        }));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new ResponseEntity(HttpStatus.OK);
  }

  private List<GraphData> getGraphDataFromCruncher(LocalDate start, LocalDate end, String idpEntityId) {
    return getGraphDataFromCruncher(start, end, idpEntityId, null);
  }

  private List<GraphData> getGraphDataFromCruncher(LocalDate start, LocalDate end, String idpEntityId, String spEntityId) {
    LOG.debug("Getting cruncher data for idp {} and sp {} - from {} to {}", idpEntityId, spEntityId, start.toString("yyyyMMdd"), end.toString("yyyyMMdd"));

    String crazyJsonString;
    if (spEntityId == null) {
      crazyJsonString = cruncher.getLoginsByIdp(start.toDate(), end.toDate(), idpEntityId);
    } else {
      crazyJsonString = cruncher.getLoginsByIdpAndSp(start.toDate(), end.toDate(), idpEntityId, spEntityId);
    }

    LOG.debug("JSON data from cruncher: {}", crazyJsonString);
    LoginData[] loginData = new Gson().fromJson(crazyJsonString, LoginData[].class);

    return Lists.transform(Arrays.asList(loginData), new Function<LoginData, GraphData>() {
      @Override
      public GraphData apply(LoginData loginData) {
        return GraphData.forLoginData(loginData);
      }
    });
  }
}
