package dashboard.control;

import  dashboard.domain.Action;
import dashboard.domain.ContactPerson;
import dashboard.domain.InviteRequest;
import dashboard.mail.MailBox;
import dashboard.manage.Manage;
import dashboard.sab.Sab;
import dashboard.service.ActionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;


@RequestMapping(value = "/spDashboard/api/")
@RestController
public class SpDashboardController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(SpDashboardController.class);

    private ActionsService actionsService;
    private MailBox mailbox;
    private Sab sabClient;
    private String spApiUsername;
    private String spApiPassword;
    private Manage manage;

    public SpDashboardController(ActionsService actionsService, MailBox mailbox, Sab sabClient, Manage manage,
                                 @Value("${spDashboard.username}") String spApiUsername,
                                 @Value("${spDashboard.password}") String spApiPassword) {
        this.actionsService = actionsService;
        this.mailbox = mailbox;
        this.sabClient = sabClient;
        this.spApiUsername = spApiUsername;
        this.spApiPassword = spApiPassword;
        this.manage = manage;
    }

    @RequestMapping(value = "serviceConnectionRequest", method = RequestMethod.PUT)
    public ResponseEntity connectionRequest(
            @RequestBody ServiceConnectionRequest serviceConnectionRequest,
            HttpServletRequest request
    ) throws IOException, MessagingException {
        LOG.debug("authenticating serviceProvider request from getSpEntityId: " + inviteRequest.getSpEntityId());
        if (invalidUser(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // authentication done

        String idpEntityId = serviceConnectionRequest.getIdpEntityId();
        String emailTo = sabClient.getSabEmailsForOrganization(idpEntityId, "SURFconextverantwoordelijke")
                .stream()
                .collect(Collectors.joining(", "));
        String spEntityId = serviceConnectionRequest.getSpEntityId();

        LOG.debug("Send email to sabPeople: " + emailTo);

        // create JIRA ticket and send emails
        Action action = Action.builder()
                .userEmail(serviceConnectionRequest.getOwnEmail())
                .userName(serviceConnectionRequest.getOwnName())
                .emailTo(emailTo)
                .typeMetaData(serviceConnectionRequest.getTypeMetaData())
                .idpId(idpEntityId)
                .spId(spEntityId)
                .type(Action.Type.LINKINVITE).build();

        actionsService.create(action, Collections.emptyList());

        List<ContactPerson> contactPersons = null;//

        InviteRequest inviteRequest = new InviteRequest(serviceConnectionRequest, contactPersons);

        mailbox.sendInviteMail(inviteRequest, action);

        return ResponseEntity.ok().build();
    }

    private boolean invalidUser(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            return true;
        }

        byte[] base64Token = header.substring(6).getBytes(Charset.defaultCharset());
        byte[] decoded = Base64.getDecoder().decode(base64Token);

        String token = new String(decoded, Charset.defaultCharset());
        String user = token.split(":")[0];
        String password = token.split(":")[1];
        if (!user.equals(spApiUsername) || !password.equals(spApiPassword)) {
            return true;
        }
        return false;
    }
}
