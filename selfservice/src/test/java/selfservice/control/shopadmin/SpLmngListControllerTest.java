package selfservice.control.shopadmin;

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
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.service.impl.CompoundServiceProviderService;
import selfservice.serviceregistry.Manage;
import selfservice.shibboleth.ShibbolethPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class SpLmngListControllerTest {

  @InjectMocks
  private SpLmngListController subject = new SpLmngListController();

  @Mock private LocaleResolver localeResolverMock;
  @Mock private Manage manageMock;
  @Mock private CompoundServiceProviderDao compoundServiceProviderDaoMock;
  @Mock private CompoundServiceProviderService compoundSPServiceMock;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    when(localeResolverMock.resolveLocale(any(HttpServletRequest.class))).thenReturn(Locale.ENGLISH);

    mockMvc = standaloneSetup(subject)
        .addFilter(new ShibbolethPreAuthenticatedProcessingFilter(auth -> auth, manageMock))
        .build();
  }

  @Test
  public void listAllSpsLmngShouldSetModelAttributes() throws Exception {
    when(manageMock.getIdentityProvider("idpId")).thenReturn(Optional.of(new IdentityProvider()));
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

    when(manageMock.getIdentityProvider("idpId")).thenReturn(Optional.of(new IdentityProvider()));
    when(manageMock.getAllServiceProviders()).thenReturn(ImmutableList.of(sp1));
    when(compoundSPServiceMock.getCSPByServiceProvider(sp1)).thenReturn(csp1);

    when(compoundServiceProviderDaoMock.findAll()).thenReturn(newArrayList(csp1, orphan));

    mockMvc.perform(get("/shopadmin/all-spslmng")
        .header("name-id", "nameId")
        .header("Shib-Authenticating-Authority", "idpId"))
      .andExpect(status().isOk())
      .andExpect(model().attribute("bindings", hasSize(1)))
      .andExpect(model().attribute("orphans", hasSize(1)))
      .andExpect(model().attributeExists("licenseStatuses"));
  }

}
