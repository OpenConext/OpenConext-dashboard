package selfservice.api.dashboard;

import selfservice.service.Csa;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/dashboard/api/actions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActionController extends BaseController {

  @Resource
  private Csa csa;

  @RequestMapping
  public ResponseEntity<RestResponse> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    return new ResponseEntity<RestResponse>(this.createRestResponse(csa.getJiraActions(idpEntityId)), HttpStatus.OK);
  }

}
