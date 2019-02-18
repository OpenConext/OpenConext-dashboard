package dashboard.control;

import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import dashboard.domain.Action;
import dashboard.domain.Category;
import dashboard.domain.CategoryValue;
import dashboard.domain.CoinUser;
import dashboard.domain.InstitutionIdentityProvider;
import dashboard.domain.Provider;
import dashboard.domain.Service;
import dashboard.manage.EntityType;
import dashboard.manage.Manage;
import dashboard.service.ActionsService;
import dashboard.service.Services;
import dashboard.util.SpringSecurity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static dashboard.control.Constants.HTTP_X_IDP_ENTITY_ID;

@RestController
@RequestMapping(value = "/dashboard/api/services", produces = APPLICATION_JSON_VALUE)
public class ServicesController extends BaseController {

    @Autowired
    private Services services;

    @Autowired
    private Manage manage;

    @Autowired
    private ActionsService actionsService;

    @RequestMapping
    public RestResponse<Map<String, Object>> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, Locale locale)
        throws IOException {
        List<Service> servicesForIdp = services.getServicesForIdp(idpEntityId, locale);
        List<Category> categories = getCategories(servicesForIdp);
        Map<String, Object> result = new HashMap<>();
        result.put("apps", servicesForIdp);
        result.put("facets", categories);
        return createRestResponse(result);
    }

    private List<Category>  getCategories(List<Service> servicesForIdp) {
        Map<String, List<Category>> groupedCategories = servicesForIdp.stream().map(s -> s.getCategories()).flatMap
            (Collection::stream).collect(groupingBy(Category::getName));

        //ensure we make the values unique
        return groupedCategories.entrySet().stream().map(entry -> {
            List<CategoryValue> categoryValues = entry.getValue().stream().map(cat -> cat.getValues().stream().map
                (CategoryValue::getValue)).flatMap(Function.identity()).collect(toSet()).stream().map
                (CategoryValue::new)
                .collect(toList());
            return new Category(entry.getKey(), "type_of_service", categoryValues);
        }).map(res -> {
            return res;
        }).collect(toList());
    }

    @RequestMapping(value = "/connected")
    public RestResponse<List<Service>> connected(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, Locale
        locale) throws IOException {
        return createRestResponse(services.getServicesForIdp(idpEntityId, locale).stream()
            .filter(Service::isConnected)
            .collect(toList()));
    }

    @RequestMapping(value = "/idps")
    public RestResponse<List<InstitutionIdentityProvider>> getConnectedIdps(
        @RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
        @RequestParam String spEntityId) {
        List<InstitutionIdentityProvider> idps = manage.getLinkedIdentityProviders(spEntityId).stream()
            .map(idp -> new InstitutionIdentityProvider(idp.getId(), idp.getName(Provider.Language.EN),
                idp.getName(Provider.Language.NL), idp.getInstitutionId()))
            .collect(toList());

        return createRestResponse(idps);
    }


    @PostMapping(value = "/download")
    public List<String[]> download(@RequestBody Map<String, Object> body,
                                   Locale locale,
                                   HttpServletResponse response) throws IOException {
        String idpEntityId = (String) body.get("idp");
        List<Integer> ids = (List<Integer>) body.get("ids");
        List<Service> services = this.services.getServicesForIdp(idpEntityId, locale);
        Stream<String[]> values = ids.stream()
            .map(id -> getServiceById(services, id.longValue()))
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

        Stream<String[]> headers = Stream.<String[]>of(new String[]{
            "id", "name", "entityID", "description", "app-url", "wiki-url", "support-mail",
            "connected", "licenseStatus",
            "publishedInEdugain", "singleTenant", "strongAuthentication",
            "arpEnabled", "arpAttributes"});

        List<String[]> rows = Stream.concat(headers, values).collect(toList());
        return rows;
    }

    private String stripBreakingWhitespace(String input) {
        return StringUtils.hasText(input) ? input.trim().replace("\n", "") : "";
    }

    private Optional<Service> getServiceById(List<Service> services, Long id) {
        return services.stream().filter(service -> service.getId() == id).findFirst();
    }

    @RequestMapping(value = "/detail")
    public ResponseEntity<RestResponse<Service>> get(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                     @RequestParam Long spId,
                                                     @RequestParam String entityType,
                                                     Locale locale) throws IOException {
        Optional<Service> serviceByEntityId = services.getServiceById(idpEntityId, spId, EntityType
            .valueOf(entityType), locale);
        return serviceByEntityId
            .map(service -> ResponseEntity.ok(createRestResponse(service)))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/connect", method = RequestMethod.POST)
    public ResponseEntity<RestResponse<Action>> connect(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                        @RequestParam(value = "comments", required = false) String
                                                            comments,
                                                        @RequestParam(value = "spEntityId") String spEntityId,
                                                        Locale locale) throws IOException {

        return createAction(idpEntityId, comments, spEntityId, Action.Type.LINKREQUEST, locale)
            .map(action -> ResponseEntity.ok(createRestResponse(action)))
            .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @RequestMapping(value = "/disconnect", method = RequestMethod.POST)
    public ResponseEntity<RestResponse<Action>> disconnect(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                           @RequestParam(value = "comments", required = false) String
                                                               comments,
                                                           @RequestParam(value = "spEntityId") String spEntityId,
                                                           Locale locale) throws IOException {

        return createAction(idpEntityId, comments, spEntityId, Action.Type.UNLINKREQUEST, locale)
            .map(action -> ResponseEntity.ok(createRestResponse(action)))
            .orElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    private Optional<Action> createAction(String idpEntityId, String comments, String spEntityId, Action.Type
        jiraType, Locale locale) throws IOException {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.isSuperUser() || (!currentUser.isDashboardAdmin() && currentUser.isDashboardViewer())) {
            return Optional.empty();
        }

        if (isNullOrEmpty(currentUser.getIdp().getInstitutionId())) {
            return Optional.empty();
        }

        List<Service> services = this.services.getServicesForIdp(idpEntityId, locale);
        Optional<Service> optional = services.stream().filter(s -> s.getSpEntityId().equals(spEntityId)).findFirst();

        if (optional.isPresent()) {
            Service service = optional.get();
            Action action = Action.builder()
                .userEmail(currentUser.getEmail())
                .userName(currentUser.getFriendlyName())
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
