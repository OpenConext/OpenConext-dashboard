package selfservice.api.rest;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import selfservice.domain.*;
import selfservice.service.Csa;
import selfservice.util.SpringSecurity;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

@Controller
@RequestMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServicesController extends BaseController {

  private static Set<String> IGNORED_ARP_LABELS = ImmutableSet.of("urn:mace:dir:attribute-def:eduPersonTargetedID");

  @Resource
  private Csa csa;

  @RequestMapping
  public ResponseEntity<RestResponse> index(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    List<Service> services = csa.getServicesForIdp(idpEntityId);
    return new ResponseEntity<RestResponse>(createRestResponse(services), HttpStatus.OK);
  }

  @RequestMapping(value = "/idps")
  public ResponseEntity<RestResponse> getConnectedIdps(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                       @RequestParam String spEntityId) {
    List<InstitutionIdentityProvider> providers = csa.serviceUsedBy(spEntityId);
    return new ResponseEntity<RestResponse>(createRestResponse(providers), HttpStatus.OK);
  }

  @RequestMapping(value = "/download")
  public ResponseEntity<RestResponse> download(@RequestParam("idpEntityId") String idpEntityId, @RequestParam("id[]") List<Long> ids, HttpServletResponse response) {
    List<Service> services = csa.getServicesForIdp(idpEntityId);

    List<String[]> rows = new ArrayList<>();
    rows.add(new String[]{"id", "name", "description", "app-url", "wiki-url", "support-mail",
      "connected", "license", "licenseStatus", "categories",
      "spEntityId", "spName", "publishedInEdugain", "normenkaderPresent", "normenkaderUrl", "singleTenant"});

    for (Long id : ids) {
      Service service = getServiceById(services, id);
      rows.add(new String[]{String.valueOf(service.getId()), service.getName(), service.getDescription(), service.getAppUrl(), service.getWikiUrl(), service.getSupportMail(),
        String.valueOf(service.isConnected()), service.getLicense() != null ? service.getLicense().toString() : null, service.getLicenseStatus(), service.getCategories().stream().map(Category::getName).collect(joining()),
        service.getSpEntityId(), service.getSpName(), String.valueOf(service.isPublishedInEdugain()), String.valueOf(service.isNormenkaderPresent()), service.getNormenkaderUrl(), String.valueOf(service.isExampleSingleTenant())});
    }

    response.setHeader("Content-Disposition", format("attachment; filename=service-overview.csv"));

    try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
      writer.writeAll(rows);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new ResponseEntity<RestResponse>(HttpStatus.OK);
  }

  private Service getServiceById(List<Service> services, Long id) {
    for (Service service : services) {
      if (service.getId() == id) {
        return service;
      }
    }
    return null;
  }

  @RequestMapping(value = "/id/{id}")
  public ResponseEntity<RestResponse> get(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, @PathVariable long id) {
    Service service = csa.getServiceForIdp(idpEntityId, id);
    // remove arp-labels that are explicitly unused
    for (String label : IGNORED_ARP_LABELS) {
      if (service.getArp() != null) {
        service.getArp().getAttributes().remove(label);
      }
    }

    return new ResponseEntity<RestResponse>(createRestResponse(service), HttpStatus.OK);
  }

  @RequestMapping(value = "/id/{id}/connect", method = RequestMethod.POST)
  public ResponseEntity<RestResponse> connect(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                              @RequestParam(value = "comments", required = false) String comments,
                                              @RequestParam(value = "spEntityId", required = true) String spEntityId,
                                              @PathVariable String id) {
    if (!createAction(idpEntityId, comments, spEntityId, JiraTask.Type.LINKREQUEST))
      return new ResponseEntity<RestResponse>(HttpStatus.FORBIDDEN);

    return new ResponseEntity<RestResponse>(HttpStatus.OK);
  }

  @RequestMapping(value = "/id/{id}/disconnect", method = RequestMethod.POST)
  public ResponseEntity<RestResponse> disconnect(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                 @RequestParam(value = "comments", required = false) String comments,
                                                 @RequestParam(value = "spEntityId", required = true) String spEntityId,
                                                 @PathVariable String id) {
    if (!createAction(idpEntityId, comments, spEntityId, JiraTask.Type.UNLINKREQUEST))
      return new ResponseEntity<RestResponse>(HttpStatus.FORBIDDEN);

    return new ResponseEntity<RestResponse>(HttpStatus.OK);
  }

  private boolean createAction(String idpEntityId, String comments, String spEntityId, JiraTask.Type jiraType) {
    CoinUser currentUser = SpringSecurity.getCurrentUser();
    if (currentUser.isSuperUser() || currentUser.isDashboardViewer()) {
      return false;
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
    csa.createAction(action);
    return true;
  }
}
