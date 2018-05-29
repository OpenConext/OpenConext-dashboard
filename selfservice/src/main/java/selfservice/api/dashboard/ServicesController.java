package selfservice.api.dashboard;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.CharMatcher;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import selfservice.domain.Action;
import selfservice.domain.Category;
import selfservice.domain.CoinUser;
import selfservice.domain.InstitutionIdentityProvider;
import selfservice.domain.Provider;
import selfservice.domain.Service;
import selfservice.service.ActionsService;
import selfservice.service.Csa;
import selfservice.manage.Manage;
import selfservice.util.SpringSecurity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;
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
  private Manage manage;

  @Autowired
  private ActionsService actionsService;

  @RequestMapping
  public RestResponse<List<Service>> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    return createRestResponse(csa.getServicesForIdp(idpEntityId));
  }

  @RequestMapping(value = "/connected")
  public RestResponse<List<Service>> connected(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    return createRestResponse(csa.getServicesForIdp(idpEntityId).stream()
        .filter(Service::isConnected)
        .collect(toList()));
  }

  @RequestMapping(value = "/idps")
  public RestResponse<List<InstitutionIdentityProvider>> getConnectedIdps(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, @RequestParam String spEntityId) {
    List<InstitutionIdentityProvider> idps = manage.getLinkedIdentityProviders(spEntityId).stream()
      .map(idp -> new InstitutionIdentityProvider(idp.getId(), idp.getName(Provider.Language.EN),
        idp.getName(Provider.Language.NL), idp.getInstitutionId()))
        .collect(toList());

    return createRestResponse(idps);
  }


  @RequestMapping(value = "/download")
  public ResponseEntity<Void> download(@RequestParam("idpEntityId") String idpEntityId, @RequestParam("ids") String idCommaSeperated, HttpServletResponse response) throws IOException {
    List<Service> services = csa.getServicesForIdp(idpEntityId);
    List<Long> ids = Arrays.asList(idCommaSeperated.split(",")).stream().map(s -> Long.valueOf(s.trim())).collect
      (toList());
    Stream<String[]> values = ids.stream()
        .map(id -> getServiceById(services, id))
        .flatMap(opt -> opt.map(Stream::of).orElse(Stream.empty()))
        .map(service -> new String[]{
          String.valueOf(service.getId()),
          stripBreakingWhitespace(service.getName()),
          service.getSpEntityId(),
          stripBreakingWhitespace(service.getDescription()),
          service.getAppUrl(),
          service.getWikiUrl(),
          service.getSupportMail(),
          String.valueOf(service.isConnected()),
          service.getLicenseStatus().name(),
          String.valueOf(service.isPublishedInEdugain()),
          String.valueOf(service.isExampleSingleTenant()),
          String.valueOf(service.isStrongAuthentication()),
          String.valueOf(!service.getArp().isNoArp()),
          service.getArp().getAttributes().keySet().stream().collect(joining(" - "))});

    Stream<String[]> headers = Stream.<String[]>of(new String[] {
        "id", "name", "entityID", "description", "app-url", "wiki-url", "support-mail",
        "connected", "licenseStatus",
        "publishedInEdugain", "singleTenant", "strongAuthentication",
    "arpEnabled", "arpAttributes"});

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
    return StringUtils.hasText(input) ? input.trim().replace("\n", "") : "";
  }

  private Optional<Service> getServiceById(List<Service> services, Long id) {
    return services.stream().filter(service -> service.getId() == id).findFirst();
  }

  @RequestMapping(value = "/id/{id}")
  public ResponseEntity<RestResponse<Service>> get(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, @PathVariable long id) {
    return csa.getServiceForIdp(idpEntityId, id)
        .map(this::removeExplicitlyUnusedArpLabels)
        .map(service -> ResponseEntity.ok(createRestResponse(service)))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  private Service removeExplicitlyUnusedArpLabels(Service service) {
    if (service.getArp() == null) {
      return service;
    }

    IGNORED_ARP_LABELS.forEach(label -> service.getArp().getAttributes().remove(label));

    return service;
  }

  @RequestMapping(value = "/id/{id}/connect", method = RequestMethod.POST)
  public ResponseEntity<RestResponse<Action>> connect(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                              @RequestParam(value = "comments", required = false) String comments,
                                              @RequestParam(value = "spEntityId", required = true) String spEntityId,
                                              @PathVariable String id) {

    return createAction(idpEntityId, comments, spEntityId, Action.Type.LINKREQUEST)
        .map(action -> ResponseEntity.ok(createRestResponse(action)))
        .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
  }

  @RequestMapping(value = "/id/{id}/disconnect", method = RequestMethod.POST)
  public ResponseEntity<RestResponse<Action>> disconnect(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                 @RequestParam(value = "comments", required = false) String comments,
                                                 @RequestParam(value = "spEntityId", required = true) String spEntityId,
                                                 @PathVariable String id) {

    return createAction(idpEntityId, comments, spEntityId, Action.Type.UNLINKREQUEST)
        .map(action -> ResponseEntity.ok(createRestResponse(action)))
        .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
  }

  private Optional<Action> createAction(String idpEntityId, String comments, String spEntityId, Action.Type jiraType) {
    CoinUser currentUser = SpringSecurity.getCurrentUser();
    if (currentUser.isSuperUser() || (!currentUser.isDashboardAdmin() && currentUser.isDashboardViewer())) {
      return Optional.empty();
    }

    if (isNullOrEmpty(currentUser.getIdp().getInstitutionId())) {
      return Optional.empty();
    }

    List<Service> services = csa.getServicesForIdp(idpEntityId);
    Optional<Service> optional = services.stream().filter(s -> s.getSpEntityId().equals(spEntityId)).findFirst();

    if (optional.isPresent()) {
      Service service = optional.get();
      Action action = Action.builder()
              .userEmail(currentUser.getEmail())
              .userName(currentUser.getUsername())
              .body(comments)
              .idpId(idpEntityId)
              .spId(spEntityId)
              .service(service)
              .type(jiraType).build();

      return Optional.of(actionsService.create(action, Collections.emptyList()));
    }

    return Optional.empty();

  }
}
