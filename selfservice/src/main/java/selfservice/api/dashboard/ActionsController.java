package selfservice.api.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import selfservice.domain.Action;
import selfservice.service.ActionsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

@RestController
@RequestMapping(value = "/dashboard/api/actions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActionsController extends BaseController {

    @Autowired
    private ActionsService actionsService;

    @RequestMapping(method = RequestMethod.GET)
    public RestResponse<Map<String, Object>> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                   @RequestParam(value = "startAt", required = false, defaultValue =
                                                       "0")
                                                       int startAt,
                                                   @RequestParam(value = "maxResults", required = false, defaultValue =
                                                       "50") int maxResults) {
        Map<String, Object> result = actionsService.getActions(idpEntityId, startAt, maxResults);
        List<Action> issues = (List<Action>) result.get("issues");
        List<Action> enrichedActions = issues.stream().map(this::cleanAction).collect(toList());
        Map<String, Object> copyResult = new HashMap<>(result);
        copyResult.put("issues", enrichedActions);
        return createRestResponse(copyResult);
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
