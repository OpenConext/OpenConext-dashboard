package selfservice.api.dashboard;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import selfservice.domain.*;
import selfservice.service.Csa;
import selfservice.serviceregistry.ServiceRegistry;
import selfservice.util.SpringSecurity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

@RestController
@RequestMapping(value = "/dashboard/api/services", produces = APPLICATION_JSON_VALUE)
public class ServicesController extends BaseController {

  private static Set<String> IGNORED_ARP_LABELS = ImmutableSet.of("urn:mace:dir:attribute-def:eduPersonTargetedID");

  @Autowired
  private Csa csa;

  @Autowired
  private ServiceRegistry serviceRegistry;

  @RequestMapping
  public RestResponse<List<Service>> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    return createRestResponse(csa.getServicesForIdp(idpEntityId));
  }

  @RequestMapping(value = "/connected")
  public RestResponse<List<Service>> connected(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    return createRestResponse(csa.getServicesForIdp(idpEntityId).stream().filter(Service::isConnected).collect(toList()));
  }

  @RequestMapping(value = "/idps")
  public RestResponse<List<InstitutionIdentityProvider>> getConnectedIdps(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, @RequestParam String spEntityId) {
    List<InstitutionIdentityProvider> idps = serviceRegistry.getLinkedIdentityProviders(spEntityId).stream()
        .map(idp -> new InstitutionIdentityProvider(idp.getId(), idp.getName(), idp.getInstitutionId()))
        .collect(toList());

    return createRestResponse(idps);
  }


  @RequestMapping(value = "/download")
  public ResponseEntity<Void> download(@RequestParam("idpEntityId") String idpEntityId, @RequestParam("id[]") List<Long> ids, HttpServletResponse response) {
    List<Service> services = csa.getServicesForIdp(idpEntityId);

    Stream<String[]> values = ids.stream()
        .map(id -> getServiceById(services, id))
        .flatMap(opt -> opt.map(Stream::of).orElse(Stream.empty()))
        .map(service ->
          new String[] {
            String.valueOf(service.getId()),
            stripBreakingWhitespace(service.getName()),
            stripBreakingWhitespace(service.getDescription()),
            service.getAppUrl(),
            service.getWikiUrl(),
            service.getSupportMail(),
            String.valueOf(service.isConnected()),
            service.getLicense() != null ? service.getLicense().toString() : null,
            service.getLicenseStatus().name(),
            service.getCategories().stream().map(Category::getName).collect(joining()),
            service.getSpEntityId(),
            service.getSpName(),
            String.valueOf(service.isPublishedInEdugain()),
            String.valueOf(service.isNormenkaderPresent()),
            service.getNormenkaderUrl(),
            String.valueOf(service.isExampleSingleTenant()) });

    Stream<String[]> headers = Stream.<String[]>of(new String[] {
        "id", "name", "description", "app-url", "wiki-url", "support-mail",
        "connected", "license", "licenseStatus", "categories", "spEntityId",
        "spName", "publishedInEdugain", "normenkaderPresent", "normenkaderUrl", "singleTenant" });

    List<String[]> rows = Stream.concat(headers, values).collect(toList());

    response.setHeader("Content-Disposition", format("attachment; filename=service-overview.csv"));

    try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
      writer.writeAll(rows);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }

    return ResponseEntity.ok().build();
  }

  private String stripBreakingWhitespace(String input) {
    return Optional.ofNullable(input).map(CharMatcher.BREAKING_WHITESPACE::removeFrom).orElse(null);
  }

  private Optional<Service> getServiceById(List<Service> services, Long id) {
    return services.stream().filter(service -> service.getId() == id).findFirst();
  }

  @RequestMapping(value = "/id/{id}")
  public ResponseEntity<RestResponse<Service>> get(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, @PathVariable long id) {
    return csa.getServiceForIdp(idpEntityId, id).map(service -> {
      // remove arp-labels that are explicitly unused
      for (String label : IGNORED_ARP_LABELS) {
        if (service.getArp() != null) {
          service.getArp().getAttributes().remove(label);
        }
      }

      return ResponseEntity.ok(createRestResponse(service));
    }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequestMapping(value = "/id/{id}/connect", method = RequestMethod.POST)
  public ResponseEntity<RestResponse<Action>> connect(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                              @RequestParam(value = "comments", required = false) String comments,
                                              @RequestParam(value = "spEntityId", required = true) String spEntityId,
                                              @PathVariable String id) {

    return createAction(idpEntityId, comments, spEntityId, JiraTask.Type.LINKREQUEST)
        .map(action -> ResponseEntity.ok(createRestResponse(action)))
        .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
  }

  @RequestMapping(value = "/id/{id}/disconnect", method = RequestMethod.POST)
  public ResponseEntity<RestResponse<Action>> disconnect(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                 @RequestParam(value = "comments", required = false) String comments,
                                                 @RequestParam(value = "spEntityId", required = true) String spEntityId,
                                                 @PathVariable String id) {

    return createAction(idpEntityId, comments, spEntityId, JiraTask.Type.UNLINKREQUEST)
        .map(action -> ResponseEntity.ok(createRestResponse(action)))
        .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
  }

  private Optional<Action> createAction(String idpEntityId, String comments, String spEntityId, JiraTask.Type jiraType) {
    CoinUser currentUser = SpringSecurity.getCurrentUser();
    if (currentUser.isSuperUser() || currentUser.isDashboardViewer()) {
      return Optional.empty();
    }

    if (Strings.isNullOrEmpty(currentUser.getIdp().getInstitutionId())) {
      return Optional.empty();
    }

    Action action = new Action();
    action.setUserId(currentUser.getUid());
    action.setUserEmail(currentUser.getEmail());
    action.setUserName(currentUser.getDisplayName());
    action.setType(jiraType);
    action.setBody(comments);
    action.setIdpId(idpEntityId);
    action.setSpId(spEntityId);
    action.setInstitutionId(currentUser.getIdp().getInstitutionId());

    return Optional.of(csa.createAction(action));
  }
}
