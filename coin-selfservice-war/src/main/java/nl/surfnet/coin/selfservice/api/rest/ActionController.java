package nl.surfnet.coin.selfservice.api.rest;


import nl.surfnet.coin.selfservice.service.Csa;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

@Controller
@RequestMapping(value = "/actions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActionController extends BaseController {

  @Resource
  private Csa csa;

  @RequestMapping
  public ResponseEntity<RestResponse> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    return new ResponseEntity(this.createRestResponse(csa.getJiraActions(idpEntityId)), HttpStatus.OK);
  }

}
