package selfservice.api.csa;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import selfservice.api.cache.CrmCache;
import selfservice.api.cache.ProviderCache;
import selfservice.api.cache.ServicesCache;
import selfservice.domain.Service;
import selfservice.domain.csa.CheckTokenResponse;
import selfservice.domain.csa.IdentityProvider;
import selfservice.filter.AuthorizationServerFilter;
import selfservice.interceptor.AuthorityScopeInterceptor;

@RunWith(MockitoJUnitRunner.class)
public class CsaServicesControllerTest {

  private String mockIdp = "http://mock-idp";

  @InjectMocks
  private CsaServicesController subject = new CsaServicesController() {};

  @Mock
  private ServicesCache servicesCache;

  @Mock
  private ProviderCache providerCache;

  @Mock
  private CrmCache crmCache;

  private MockHttpServletRequest request;

  private String mockSp = "http://mock-sp";

  private String institutionId = "institutionId";

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    request.setAttribute(AuthorizationServerFilter.CHECK_TOKEN_RESPONSE, new CheckTokenResponse(mockIdp, Arrays.asList(AuthorityScopeInterceptor.OAUTH_CLIENT_SCOPE_CROSS_IDP_SERVICES)));

    when(providerCache.getIdentityProvider(mockIdp)).thenReturn(new IdentityProvider(mockIdp, institutionId, "mock-idp"));
    when(providerCache.getServiceProviderIdentifiers(mockIdp)).thenReturn(Arrays.asList(mockSp));
  }

  @Test
  public void test_get_protected_services_by_idp_with_idp_only_and_no_institution_id() throws Exception {
    when(servicesCache.getAllServices("en")).thenReturn(Arrays.asList(getService(true, null)));

    List<Service> services = subject.getProtectedServicesByIdp("en", mockIdp, request);
    assertEquals(0, services.size());
  }

  public void test_get_protected_services_by_idp_with_idp_only_and_valid_institution_id() throws Exception {
    when(servicesCache.getAllServices("en")).thenReturn(Arrays.asList(getService(true, institutionId)));

    List<Service> services = subject.getProtectedServicesByIdp("en", mockIdp, request);
    assertEquals(1, services.size());
  }

  private Service getService(boolean idpVisibleOnly, String institutionId) {
    Service service = new Service(1L, "sp", null, null, false, null, mockSp);
    service.setInstitutionId(institutionId);
    service.setIdpVisibleOnly(idpVisibleOnly);
    return service;
  }
}
