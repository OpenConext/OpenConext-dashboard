package nl.surfnet.coin.selfservice.api.rest;

import com.google.common.collect.ImmutableSet;
import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Action;
import nl.surfnet.coin.csa.model.JiraTask;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.command.LinkRequest;
import nl.surfnet.coin.selfservice.command.Question;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.surfnet.cruncher.Cruncher;
import org.surfnet.cruncher.model.SpStatistic;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

@Controller
@RequestMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServicesController extends BaseController {

  private static Set<String> IGNORED_ARP_LABELS = ImmutableSet.of("urn:mace:dir:attribute-def:eduPersonTargetedID");

  @Resource
  private Csa csa;

  @Resource
  private Cruncher cruncher;

  @RequestMapping
  public ResponseEntity<RestResponse> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    List<Service> services = csa.getServicesForIdp(idpEntityId);
    List<SpStatistic> recentLoginsForUser = cruncher.getRecentLoginsForUser(SpringSecurity.getCurrentUser().getUid(), idpEntityId);
    for (SpStatistic spStatistic: recentLoginsForUser) {
      Service serviceBySpEntityId = getServiceBySpEntityId(services, spStatistic.getSpEntityId());
      if(serviceBySpEntityId != null) {
        serviceBySpEntityId.setLastLoginDate(new Date(spStatistic.getEntryTime()));
      }
    }

    return new ResponseEntity(createRestResponse(services), HttpStatus.OK);
  }

  private Service getServiceBySpEntityId(List<Service> services, String spEntityId) {
    for (Service service: services) {
      if(service.getSpEntityId().equalsIgnoreCase((spEntityId))) {
        return service;
      }
    }
    return null;
  }

  @RequestMapping(value = "/id/{id}")
  public ResponseEntity<RestResponse> get(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, @PathVariable long id) {
    Service service = csa.getServiceForIdp(idpEntityId, id);
    // remove arp-labels that are explicitly unused
    for(String label: IGNORED_ARP_LABELS) {
      if (service.getArp() != null) {
        service.getArp().getAttributes().remove(label);
      }
    }

    return new ResponseEntity(createRestResponse(service), HttpStatus.OK);
  }

  @RequestMapping(value = "/id/{id}/connect", method = RequestMethod.POST)
  public ResponseEntity<RestResponse> connect(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, @RequestParam(value = "comments", required = false) String comments, @PathVariable String id) {
    CoinUser currentUser = SpringSecurity.getCurrentUser();
    if (!currentUser.isDashboardAdmin()) {
      return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    Action action = new Action(
      currentUser.getUid(),
      currentUser.getEmail(),
      currentUser.getUsername(),
      JiraTask.Type.LINKREQUEST,
      comments,
      idpEntityId,
      id,
      currentUser.getInstitutionId()
    );

    csa.createAction(action);

    return new ResponseEntity(HttpStatus.OK);
  }
}
