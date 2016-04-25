package selfservice.control.shopadmin;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.LocaleResolver;

import selfservice.dao.CompoundServiceProviderDao;
import selfservice.dao.LmngIdentifierDao;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.service.CrmService;
import selfservice.service.ExportService;
import selfservice.service.impl.CompoundServiceProviderService;
import selfservice.serviceregistry.ServiceRegistry;
import selfservice.shibboleth.ShibbolethPreAuthenticatedProcessingFilter;

@RunWith(MockitoJUnitRunner.class)
public class SpLmngListControllerTest {

  @InjectMocks
  private SpLmngListController subject = new SpLmngListController();

  @Mock private LmngIdentifierDao lmngIdentifierDaoMock;
  @Mock private LocaleResolver localeResolverMock;
  @Mock private ServiceRegistry serviceRegistryMock;
  @Mock private CompoundServiceProviderDao compoundServiceProviderDaoMock;
  @Mock private CompoundServiceProviderService compoundSPServiceMock;
  @Mock private CrmService crmServiceMock;
  @Mock private ExportService exportServiceMock;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    when(localeResolverMock.resolveLocale(any(HttpServletRequest.class))).thenReturn(Locale.ENGLISH);

    mockMvc = standaloneSetup(subject)
        .addFilter(new ShibbolethPreAuthenticatedProcessingFilter(auth -> auth, serviceRegistryMock))
        .build();
  }

  @Test
  public void listAllSpsLmngShouldSetModelAttributes() throws Exception {
    when(serviceRegistryMock.getIdentityProvider("idpId")).thenReturn(Optional.of(new IdentityProvider()));
    when(compoundServiceProviderDaoMock.findAll()).thenReturn(ImmutableList.of());

    mockMvc.perform(get("/shopadmin/all-spslmng")
        .header("name-id", "nameId")
        .header("Shib-Authenticating-Authority", "idpId"))
      .andExpect(status().isOk())
      .andExpect(model().attributeExists("bindings"))
      .andExpect(model().attributeExists("orphans"))
      .andExpect(model().attributeExists("licenseStatuses"));
  }

  @Test
  public void listAllSpsLmngShouldHaveAOrpahn() throws Exception {
    ServiceProvider sp1 = new ServiceProvider(ImmutableMap.of("entityid", "sp1"));
    CompoundServiceProvider csp1 = new CompoundServiceProvider();
    csp1.setServiceProvider(sp1);
    CompoundServiceProvider orphan = new CompoundServiceProvider();
    orphan.setServiceProvider(new ServiceProvider(ImmutableMap.of("entityid", "sp3")));

    when(serviceRegistryMock.getIdentityProvider("idpId")).thenReturn(Optional.of(new IdentityProvider()));
    when(serviceRegistryMock.getAllServiceProviders()).thenReturn(ImmutableList.of(sp1));
    when(compoundSPServiceMock.getCSPByServiceProvider(sp1)).thenReturn(csp1);
    when(lmngIdentifierDaoMock.getLmngIdForServiceProviderId("sp1")).thenReturn("lmng1");
    when(lmngIdentifierDaoMock.getLmngIdForServiceProviderId("sp2")).thenReturn("lmng2");

    when(compoundServiceProviderDaoMock.findAll()).thenReturn(newArrayList(csp1, orphan));

    mockMvc.perform(get("/shopadmin/all-spslmng")
        .header("name-id", "nameId")
        .header("Shib-Authenticating-Authority", "idpId"))
      .andExpect(status().isOk())
      .andExpect(model().attribute("bindings", hasSize(1)))
      .andExpect(model().attribute("orphans", hasSize(1)))
      .andExpect(model().attributeExists("licenseStatuses"));
  }

  @Test
  public void saveLmngServiceShouldClearLmngId() throws Exception {
    when(compoundServiceProviderDaoMock.findAll()).thenReturn(ImmutableList.of());

    mockMvc.perform(post("/shopadmin/save-splmng")
        .param("spIdentifier", "spId")
        .param("index", "2"))
      .andExpect(status().isOk());

    verify(lmngIdentifierDaoMock).saveOrUpdateLmngIdForServiceProviderId("spId", null);
  }

  @Test
  public void saveLmngServiceShouldSetLmngId() throws Exception {
    String validGuid = "{00000000-0000-0000-0000-000000000000}";
    when(serviceRegistryMock.getIdentityProvider("idpId")).thenReturn(Optional.of(new IdentityProvider()));
    when(compoundServiceProviderDaoMock.findAll()).thenReturn(ImmutableList.of());
    when(crmServiceMock.getServiceName(validGuid)).thenReturn("serviceName");

    mockMvc.perform(post("/shopadmin/save-splmng")
        .param("spIdentifier", "spId")
        .param("lmngIdentifier", validGuid)
        .param("index", "2")
        .header("name-id", "nameId")
        .header("Shib-Authenticating-Authority", "idpId"))
      .andExpect(status().isOk())
      .andExpect(model().attribute("infoMessage", "serviceName"))
      .andExpect(model().attribute("messageIndex", 2));

    verify(lmngIdentifierDaoMock).saveOrUpdateLmngIdForServiceProviderId("spId", validGuid);
  }

  @Test
  public void exportToCsvShouldSetContentType() throws Exception {
    when(compoundServiceProviderDaoMock.findAll()).thenReturn(ImmutableList.of());
    when(exportServiceMock.exportServiceBindingsCsv(ImmutableList.of(), "http://localhost")).thenReturn("csvcontent");

    mockMvc.perform(get("/shopadmin/export.csv"))
      .andExpect(status().isOk())
      .andExpect(content().contentType("text/csv"))
      .andExpect(content().bytes("csvcontent".getBytes()));
  }
}
