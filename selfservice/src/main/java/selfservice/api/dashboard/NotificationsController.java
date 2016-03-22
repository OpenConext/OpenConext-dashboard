package selfservice.api.dashboard;

import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import selfservice.domain.NotificationMessage;
import selfservice.service.NotificationService;

@RestController
@RequestMapping(value = "/dashboard/api/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationsController extends BaseController {

  @Resource
  private NotificationService notificationService;

  @RequestMapping
  public RestResponse<NotificationMessage> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    return createRestResponse(notificationService.getNotifications(idpEntityId));
  }
}
