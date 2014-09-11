package nl.surfnet.coin.selfservice.api.rest;

import com.google.common.collect.ImmutableSet;
import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.surfnet.cruncher.Cruncher;
import org.surfnet.cruncher.model.SpStatistic;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
  public ResponseEntity<RestResponse> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, HttpServletRequest request) {
    List<Service> services = csa.getServicesForIdp(idpEntityId);
    List<SpStatistic> recentLoginsForUser = cruncher.getRecentLoginsForUser(SpringSecurity.getCurrentUser().getUid(), idpEntityId);
    recentLoginsForUser
      .stream()
      .forEach(stat -> getServiceBySpEntityId(services, stat.getSpEntityId())
        .ifPresent(service -> service.setLastLoginDate(new Date(stat.getEntryTime()))));

    return new ResponseEntity(new RestResponse(this.getLocale(request), services), HttpStatus.OK);
  }

  private Optional<Service> getServiceBySpEntityId(List<Service> services, String spEntityId) {
    return services.stream().filter(s -> s.getSpEntityId().equalsIgnoreCase(spEntityId)).findFirst();
  }

  @RequestMapping(value = "/id/{id}")
  public ResponseEntity<RestResponse> get(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, @PathVariable long id) {
    Service service = csa.getServiceForIdp(idpEntityId, id);
    // remove arp-labels that are explicitly unused
    IGNORED_ARP_LABELS.stream().filter(ignoredLabel -> service.getArp() != null).forEach(ignoredLabel -> {
      service.getArp().getAttributes().remove(ignoredLabel);
    });

    return new ResponseEntity<>(createRestResponse(service), HttpStatus.OK);
  }

}
