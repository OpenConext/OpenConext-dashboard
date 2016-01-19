package selfservice.control.shopadmin;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableList;

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
import selfservice.service.IdentityProviderService;
import selfservice.service.ServiceProviderService;
import selfservice.service.impl.CompoundSPService;
import selfservice.shibboleth.ShibbolethPreAuthenticatedProcessingFilter;

@RunWith(MockitoJUnitRunner.class)
public class SpLnmgListControllerTest {

  @InjectMocks
  private SpLnmgListController subject = new SpLnmgListController();

  @Mock private LmngIdentifierDao lmngIdentifierDaoMock;
  @Mock private LocaleResolver localeResolverMock;
  @Mock private ServiceProviderService providerServiceMock;
  @Mock private CompoundServiceProviderDao compoundServiceProviderDaoMock;
  @Mock private IdentityProviderService identityProviderServiceMock;
  @Mock private CompoundSPService compoundSPServiceMock;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    when(localeResolverMock.resolveLocale(any(HttpServletRequest.class))).thenReturn(Locale.ENGLISH);

    mockMvc = standaloneSetup(subject)
        .addFilter(new ShibbolethPreAuthenticatedProcessingFilter(auth -> auth, identityProviderServiceMock))
        .build();
  }

  @Test
  public void listAllSpsLmngShouldSetModelAttributes() throws Exception {
    when(identityProviderServiceMock.getIdentityProvider("idpId")).thenReturn(Optional.of(new IdentityProvider()));

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
    ServiceProvider sp1 = new ServiceProvider("sp1");
    CompoundServiceProvider csp1 = new CompoundServiceProvider();
    csp1.setServiceProvider(sp1);
    CompoundServiceProvider orphan = new CompoundServiceProvider();
    orphan.setServiceProvider(new ServiceProvider("sp3"));

    when(identityProviderServiceMock.getIdentityProvider("idpId")).thenReturn(Optional.of(new IdentityProvider()));
    when(providerServiceMock.getAllServiceProviders(false)).thenReturn(ImmutableList.of(sp1));
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
}
