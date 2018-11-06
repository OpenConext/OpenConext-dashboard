package dashboard.control;

import dashboard.domain.JiraFilter;
import dashboard.domain.JiraResponse;
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

    @RequestMapping(method = RequestMethod.POST)
    public RestResponse<JiraResponse> search(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                             @RequestBody JiraFilter filter) {
        return createRestResponse(actionsService.searchTasks(idpEntityId, filter));
    }

}
