package dashboard.control;

import dashboard.domain.JiraFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import dashboard.domain.Action;
import dashboard.service.ActionsService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static dashboard.control.Constants.HTTP_X_IDP_ENTITY_ID;

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
                                                           "20") int maxResults) {
        Map<String, Object> result = actionsService.getActions(idpEntityId, startAt, maxResults);
        List<Action> issues = (List<Action>) result.get("issues");
        Map<String, Object> copyResult = new HashMap<>(result);
        copyResult.put("issues", issues);
        return createRestResponse(copyResult);
    }

    @PostMapping("/search")
    Map<String, Object> search(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, @RequestBody JiraFilter filter) {
        return Collections.emptyMap();
    }
}
