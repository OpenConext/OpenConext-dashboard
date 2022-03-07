package dashboard.control;

import dashboard.domain.*;
import dashboard.mail.MailBox;
import dashboard.manage.EntityType;
import dashboard.manage.Manage;
import dashboard.sab.Sab;
import dashboard.sab.SabPerson;
import dashboard.service.ActionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;


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
    public ResponseEntity connectionRequest(@RequestBody ServiceConnectionRequest serviceConnectionRequest,
                                            HttpServletRequest request) {
        LOG.debug("authenticating serviceProvider request from sp: " + serviceConnectionRequest.getSpEntityId());

        if (invalidUser(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // get data
        String idpEntityId = serviceConnectionRequest.getIdpEntityId();
        Collection<SabPerson> sabPersons = sabClient.getSabEmailsForOrganization(idpEntityId, "SURFconextverantwoordelijke");

        Optional<IdentityProvider> optionalIdp = manage.getIdentityProvider(idpEntityId, false);
        String spEntityId = serviceConnectionRequest.getSpEntityId();
        Optional<ServiceProvider> optionalSp = manage.getServiceProvider(spEntityId, EntityType.valueOf(serviceConnectionRequest.getTypeMetaData()), false);
        if (!optionalSp.isPresent() || !optionalIdp.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        IdentityProvider idp = optionalIdp.get();
        ServiceProvider sp = optionalSp.get();

        String idpName = idp.getName();
        String spName = sp.getName();

        List<ContactPerson> contactPersons = sabPersons.stream().map(ContactPerson::new).collect(Collectors.toList());
        List<ContactPerson> contactPersonsFromIdp = idp.getContactPersons();
        if (!CollectionUtils.isEmpty(contactPersonsFromIdp)) {
            contactPersons.addAll(contactPersonsFromIdp.stream()
                    .filter(cp -> cp.getContactPersonType().equals(ContactPersonType.administrative))
                    .collect(Collectors.toList()));
        }

        // create JIRA ticket and send emails
        Action action = Action.builder()
                .userEmail(serviceConnectionRequest.getOwnEmail())
                .userName(serviceConnectionRequest.getOwnName())
                .emailTo(contactPersons.stream().map(ContactPerson::getEmailAddress).collect(Collectors.joining(", ")))
                .typeMetaData(serviceConnectionRequest.getTypeMetaData())
                .idpId(idpEntityId)
                .spId(spEntityId)
                .type(Action.Type.LINKINVITE).build();

        action = actionsService.create(action, Collections.emptyList());

        InviteRequest inviteRequest = new InviteRequest(serviceConnectionRequest, String.valueOf(sp.getEid()), idpName, spName, contactPersons);
        mailbox.sendInviteMail(inviteRequest, action);

        return ResponseEntity.ok(Collections.singletonMap("jiraKey", action.getJiraKey()));
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
        return !user.equals(spApiUsername) || !password.equals(spApiPassword);
    }
}
