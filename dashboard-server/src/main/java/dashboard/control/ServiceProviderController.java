package dashboard.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import static dashboard.control.Constants.HTTP_X_IDP_ENTITY_ID;


@RestController
public class ServiceProviderController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceProviderController.class);

    @RequestMapping(value = "/dashboard/api/test", method = RequestMethod.PUT)
    public void connectionRequest(){
        LOG.debug("test " + "testing");
    }

//    @RequestMapping(value = "/serviceProvider/api/connectionRequest", method = RequestMethod.PUT)
//    public void connectionRequest(
//            @RequestParam(name="IdPentityID") String IdPentityID,
//            @RequestParam(name="SPentityID") String SPentityID,
//            @RequestParam(name="contactName") String contactName,
//            @RequestParam(name="contactEmail") String contactEmail,
//            @RequestParam(name="ownEmail") String ownEmail
//    ){
//        LOG.debug("Incoming connection request, params(IdPentityID: " + IdPentityID + " SPentityID: " + SPentityID + " contactName " + contactName + " contactEmail " + contactEmail + " ownEmail " + ownEmail + ")");
//    }
}
