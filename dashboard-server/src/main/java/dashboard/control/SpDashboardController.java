package dashboard.control;

import dashboard.domain.Action;
import dashboard.domain.InviteRequest;
import dashboard.mail.MailBox;
import dashboard.sab.Sab;
import dashboard.service.ActionsService;
import dashboard.service.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;


@RequestMapping(value = "/spDashboard/api/")
@RestController
public class SpDashboardController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(SpDashboardController.class);

    private ActionsService actionsService;
    private MailBox mailbox;
    private Sab sabClient;
    private String spUsername;
    private String spPassword;

    public SpDashboardController(ActionsService actionsService, MailBox mailbox, Sab sabClient,
                                 @Value("${spDashboard.username}") String spUsername,
                                 @Value("${spDashboard.password}") String spPassword) {
        this.actionsService = actionsService;
        this.mailbox = mailbox;
        this.sabClient = sabClient;
        this.spUsername = spUsername;
        this.spPassword = spPassword;
    }

    @RequestMapping(value = "serviceConnectionRequest", method = RequestMethod.PUT)
    public ResponseEntity connectionRequest(
            @RequestParam InviteRequest inviteRequest,
            @RequestParam String contactName,
            @RequestParam String contactEmail,
            @RequestParam String ownEmail,
            HttpServletRequest request
    ) throws IOException, MessagingException {

        LOG.debug("authenticating serviceProvider request from getSpEntityId: " + inviteRequest.getSpEntityId());

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        byte[] base64Token = header.substring(6).getBytes(Charset.defaultCharset());
        byte[] decoded = Base64.getDecoder().decode(base64Token);

        String token = new String(decoded, Charset.defaultCharset());
        String user = token.split(":")[0];
        String password = token.split(":")[1];
        if (!user.equals(spUsername) || !password.equals(spPassword)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // authentication done
        LOG.debug("Incoming connection request, params(" +
                "IdpEntityId: " + inviteRequest.getIdpEntityId() +
                " SpEntityId: " + inviteRequest.getSpEntityId() +
                " contactName " + contactName +
                " contactEmail " + contactEmail +
                " ownEmail " + ownEmail + ")");

        String idpEntityId = inviteRequest.getIdpEntityId();

        String emailTo = sabClient.getSabEmailsForOrganization(idpEntityId, "SURFconextverantwoordelijke");

        LOG.debug("Send email to sabPeople: " + emailTo);

        // create JIRA ticket and send emails
        Action action = Action.builder()
                .userEmail(contactEmail)
                .userName(contactName)
                .emailTo(emailTo)
                .typeMetaData(inviteRequest.getTypeMetaData())
                .idpId(inviteRequest.getIdpEntityId())
                .spId(inviteRequest.getSpEntityId())
                .type(Action.Type.LINKINVITE).build();
        actionsService.create(action, Collections.emptyList());
        mailbox.sendInviteMail(inviteRequest, action);

        return ResponseEntity.ok().build();
    }
}
