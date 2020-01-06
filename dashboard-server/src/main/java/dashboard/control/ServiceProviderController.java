package dashboard.control;

import dashboard.domain.Action;
import dashboard.domain.InviteRequest;
import dashboard.domain.Service;
import dashboard.mail.MailBox;
import dashboard.service.ActionsService;
import dashboard.service.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


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

    // authenticate using shib headers before calling this function
    @RequestMapping(value = "serviceConnectionRequest", method = RequestMethod.PUT)
    public void connectionRequest(
            @RequestParam(name="IdPentityID") String idpEntityId,
            @RequestParam(name="SPentityID") String entityId,
            @RequestParam(name="contactName") String contactName,
            @RequestParam(name="contactEmail") String contactEmail,
            @RequestParam(name="ownEmail") String ownEmail,
            @RequestParam(name="connectionRequest") InviteRequest inviteRequest,
            Locale locale) throws IOException, MessagingException {
        LOG.debug("Incoming connection request, params(IdPentityID: " + idpEntityId + " SPentityID: " + entityId + " contactName " + contactName + " contactEmail " + contactEmail + " ownEmail " + ownEmail + ")");
        // create JIRA ticket and send email
        List<Service> services = this.services.getServicesForIdp(idpEntityId, locale);
        Optional<Service> optional = services.stream().filter(s -> s.getSpEntityId().equals(entityId)).findFirst();

        if (optional.isPresent()) {
            Service service = optional.get();

            Action action = Action.builder()
                    .userEmail(ownEmail)
                    .userName(contactName)
                    .body("") // TODO XXX
                    .idpId(idpEntityId)
                    .spId(entityId)
                    .typeMetaData("")
                    .service(service)
                    .type(Action.Type.LINKREQUEST).build();
            actionsService.create(action, Collections.emptyList());

            mailbox.sendInviteMail(inviteRequest, action);
        }

    }
}
