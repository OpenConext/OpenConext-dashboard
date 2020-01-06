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
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
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

    // authenticate using shib headers before calling this function
    @RequestMapping(value = "serviceConnectionRequest", method = RequestMethod.PUT)
    public void connectionRequest(
            @RequestBody InviteRequest inviteRequest,
            @RequestParam Locale locale,
            @RequestParam String contactName,
            @RequestParam String contactEmail,
            @RequestParam String ownEmail
    ) throws IOException, MessagingException {
        LOG.debug("Incoming connection request, params(" +
                "IdPentityID: " + inviteRequest.getIdpEntityId() +
                " SPentityID: " + inviteRequest.getSpEntityId() +
                " contactName " + contactName +
                " contactEmail " + contactEmail +
                " ownEmail " + ownEmail + ")");

        String idpEntityId = inviteRequest.getIdpEntityId();
        String spEntityId = inviteRequest.getSpEntityId();

        Optional<Service> service = services.getServiceByEntityId(idpEntityId, spEntityId, EntityType.saml20_sp, locale);

        if (service.isPresent()) { // check if service is already connected. If it is, ignore the request?
            return;
        }

        String emailTo = sabClient.getPersonsInRoleForOrganization(idpEntityId, "SURFconextverantwoordelijke")
                .stream()
                .map(SabPerson::getEmail)
                .collect(Collectors.joining(", "));

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
