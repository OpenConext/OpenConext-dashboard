package selfservice.api.dashboard;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Service;
import selfservice.filter.SpringSecurityUtil;
import selfservice.service.Services;
import selfservice.util.CookieThenAcceptHeaderLocaleResolver;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@RunWith(MockitoJUnitRunner.class)
public class FacetsControllerTest {

  @InjectMocks
  private FacetsController controller;

  @Mock
  private Services servicesMock;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();
    mockMvc = standaloneSetup(controller)
      .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
  }

  @Test
  public void thatFacetsAreRetrievedFromCsa() throws Exception {
    List<Service> services = Stream.of(service("Business processing",  "Data Storage"),
      service("Digital Content",  "Data Storage"),
      service("Education Services",  "Data Storage")).collect(toList());
    CoinUser coinUser = new CoinUser();
    coinUser.setIdp(new IdentityProvider("http://mock-idp", "institutioin_id", "name", 1L));
    SpringSecurityUtil.setAuthentication(coinUser);
    when(servicesMock.getServicesForIdp("http://mock-idp", Locale.ENGLISH)).thenReturn(services);

    mockMvc.perform(
      get("/dashboard/api/facets").contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.payload").isArray())
      .andExpect(jsonPath("$.payload[0].values", hasSize(4)));
  }

  private Service service(String... categoryValues) {
    Service service = new Service();
    service.setCategories(Collections.singletonList(new Category("Type of Service", Stream.of(categoryValues).map(CategoryValue::new).collect(toList()))));
    return service;
  }
}
