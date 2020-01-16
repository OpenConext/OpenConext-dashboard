package dashboard.control;

import dashboard.domain.Action;
import dashboard.domain.InviteRequest;
import dashboard.domain.Service;
import dashboard.mail.MailBox;
import dashboard.manage.EntityType;
import dashboard.sab.Sab;
import dashboard.sab.SabPerson;
import dashboard.service.ActionsService;
import dashboard.service.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;


@RequestMapping(value = "/serviceProvider/api/")
@RestController
public class ServiceProviderController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceProviderController.class);

    @Autowired
    private Services services;

    @Autowired
    private ActionsService actionsService;

    @Autowired
    private MailBox mailbox;

    @Autowired
    private Sab sabClient;

    @RequestMapping(value = "serviceConnectionRequest", method = RequestMethod.PUT)
    public void connectionRequest(
            @RequestParam InviteRequest inviteRequest,
            @RequestParam String contactName,
            @RequestParam String contactEmail,
            @RequestParam String ownEmail,
            @Value("${spDashboard.username}") String spUsername,
            @Value("${spDashboard.password}") String spPassword,
            HttpServletRequest request
    ) throws IOException, MessagingException {
        LOG.debug("authenticating serviceProvider request from getSpEntityId: " + inviteRequest.getSpEntityId());

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            return;
        }

        byte[] base64Token = header.substring(6).getBytes(Charset.defaultCharset());
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            return;
        }


        String token = new String(decoded, Charset.defaultCharset());
        try {
            String user = token.split(":")[0];
            String password = token.split(":")[1];
            if (!user.equals(spUsername) || !password.equals(spPassword)) {
                return;
            }
        } catch (Exception e) {
            LOG.debug(String.valueOf(e));
        }

        // authentication done

        LOG.debug("Incoming connection request, params(" +
                "IdpEntityId: " + inviteRequest.getIdpEntityId() +
                " SpEntityId: " + inviteRequest.getSpEntityId() +
                " contactName " + contactName +
                " contactEmail " + contactEmail +
                " ownEmail " + ownEmail + ")");

        String idpEntityId = inviteRequest.getIdpEntityId();

        String emailTo = sabClient.getPersonsInRoleForOrganization(idpEntityId, "SURFconextverantwoordelijke")
                .stream()
                .map(SabPerson::getEmail)
                .collect(Collectors.joining(", "));

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

    }
}
