package nl.surfnet.coin.selfservice.api.rest;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.surfnet.cruncher.Cruncher;
import org.surfnet.cruncher.model.SpStatistic;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

@Controller
@RequestMapping(value = "/services", consumes = MediaType.APPLICATION_JSON_VALUE)
public class ServicesController {

  @Resource
  private Csa csa;

  @Resource
  private Cruncher cruncher;

  @RequestMapping
  public ResponseEntity<RestResponse<List<Service>>> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    List<Service> services = csa.getServicesForIdp(idpEntityId);
    List<SpStatistic> recentLoginsForUser = cruncher.getRecentLoginsForUser(SpringSecurity.getCurrentUser().getUid(), idpEntityId);
    recentLoginsForUser
      .stream()
      .forEach(stat -> getServiceBySpEntityId(services, stat.getSpEntityId())
        .ifPresent(service -> service.setLastLoginDate(new Date(stat.getEntryTime()))));

    return new ResponseEntity(new RestResponse(services), HttpStatus.OK);
  }

  private Optional<Service> getServiceBySpEntityId(List<Service> services, String spEntityId) {
    return services.stream().filter(s -> s.getSpEntityId().equalsIgnoreCase(spEntityId)).findFirst();
  }

}
