package selfservice.api.dashboard;

import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import selfservice.domain.Action;
import selfservice.service.ActionsService;

@RestController
@RequestMapping(value = "/dashboard/api/actions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActionsController extends BaseController {

  @Autowired
  private ActionsService actionsService;

  @RequestMapping(method = RequestMethod.GET)
  public RestResponse index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    return createRestResponse(actionsService.getActions(idpEntityId));
  }

}
