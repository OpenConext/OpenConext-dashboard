package selfservice.api.dashboard;

import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

import java.util.List;
import java.util.stream.Collectors;

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
  public RestResponse<List<Action>> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    return createRestResponse(actionsService.getActions(idpEntityId).stream()
      .map(this::cleanAction).collect(Collectors.toList()));
  }

  private Action cleanAction(Action action) {
    //Only show what we need in the GUI
    return Action.builder()
      .jiraKey(action.getJiraKey().orElse(null))
      .requestDate(action.getRequestDate())
      .userName(action.getUserName())
      .type(action.getType())
      .spName(action.getSpName())
      .status(action.getStatus())
      .build();
  }

}
