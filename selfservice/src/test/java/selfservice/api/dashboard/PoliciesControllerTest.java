package selfservice.api.dashboard;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;

import selfservice.pdp.PdpService;
import selfservice.util.CookieThenAcceptHeaderLocaleResolver;

@RunWith(MockitoJUnitRunner.class)
public class PoliciesControllerTest {

  @InjectMocks
  private PoliciesController controller;

  @Mock
  private PdpService pdpServiceMock;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();

    this.mockMvc = standaloneSetup(controller)
      .dispatchOptions(true).build();
  }

  @Test
  public void whenPdpIsAvailablePoliciesAllowsGet() throws Exception {
    when(pdpServiceMock.isAvailable()).thenReturn(true);
    controller.policiesEnabled = true;

    mockMvc.perform(options("/dashboard/api/policies"))
      .andExpect(header().string("Allow", containsString("GET")))
      .andExpect(status().isOk());
  }

  @Test
  public void whenPdpIsNotAvailablePoliciesDoesNotAllowAnything() throws Exception {
    when(pdpServiceMock.isAvailable()).thenReturn(false);
    controller.policiesEnabled = true;

    mockMvc.perform(options("/dashboard/api/policies"))
      .andExpect(header().string("Allow", ""))
      .andExpect(status().isOk());
  }
}
