package dashboard.control;

import dashboard.domain.JiraFilter;
import dashboard.domain.JiraResponse;
import dashboard.service.ActionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static dashboard.control.Constants.HTTP_X_IDP_ENTITY_ID;

@RestController
@RequestMapping(value = "/dashboard/api/actions", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActionsController extends BaseController {

    @Autowired
    private ActionsService actionsService;

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @RequestMapping(method = RequestMethod.POST)
    public RestResponse<JiraResponse> search(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                             @RequestBody JiraFilter filter) {
        return createRestResponse(actionsService.searchTasks(idpEntityId, filter));
    }

}
