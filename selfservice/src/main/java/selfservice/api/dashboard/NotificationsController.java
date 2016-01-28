package selfservice.api.dashboard;

import selfservice.domain.NotificationMessage;
import selfservice.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/dashboard/api/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationsController extends BaseController {

  @Resource
  private NotificationService notificationService;

  @RequestMapping
  public ResponseEntity<RestResponse<NotificationMessage>> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    NotificationMessage notificationMessage = notificationService.getNotifications(idpEntityId);
    return new ResponseEntity<>(createRestResponse(notificationMessage), HttpStatus.OK);
  }
}
