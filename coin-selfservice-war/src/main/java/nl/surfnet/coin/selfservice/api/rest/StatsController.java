package nl.surfnet.coin.selfservice.api.rest;

import com.google.gson.Gson;
import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.joda.time.LocalDate;
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
import java.util.*;

import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

@Controller
@RequestMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
public class StatsController extends BaseController {

  @Resource
  private Cruncher cruncher;

  @Resource
  private Csa csa;

  @RequestMapping(value = "/id/{id}")
  public ResponseEntity<RestResponse> get(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                          @RequestParam("start") @DateTimeFormat(pattern = "yyyyMMdd") Date start,
                                          @RequestParam("end") @DateTimeFormat(pattern = "yyyyMMdd") Date end,
                                          @PathVariable long id) {
    Service service = csa.getServiceForIdp(idpEntityId, id);
    String crazyJsonString = cruncher.getLoginsByIdpAndSp(start, end, idpEntityId, service.getSpEntityId());
    LoginData crazyJsonData = new Gson().fromJson(crazyJsonString, LoginData[].class)[0];
    Map<Long, Integer> graphData = new LinkedHashMap<>();
    int microsecondsInSecond = 1000;

    for (int i = 0; i < crazyJsonData.getTotal(); i++) {
      graphData.put((crazyJsonData.getPointStart() + i * crazyJsonData.getPointInterval()) / microsecondsInSecond, crazyJsonData.getData().get(i));
    }

    return new ResponseEntity(createRestResponse(graphData), HttpStatus.OK);
  }
}
