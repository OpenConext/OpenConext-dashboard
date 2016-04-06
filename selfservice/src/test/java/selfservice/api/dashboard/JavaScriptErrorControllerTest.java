package selfservice.api.dashboard;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import selfservice.domain.Policy;
import selfservice.pdp.PdpService;
import selfservice.pdp.PolicyNameNotUniqueException;
import selfservice.util.CookieThenAcceptHeaderLocaleResolver;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class JavaScriptErrorControllerTest {

  @InjectMocks
  private JavaScriptErrorController controller;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    this.mockMvc = standaloneSetup(controller)
      .dispatchOptions(true).build();
  }

  @Test
  public void testReportError() throws Exception {
    mockMvc.perform(post("/dashboard/api/jsError").contentType(APPLICATION_JSON).content("{\"error\": \"whoops\"}"))
      .andExpect(status().isOk());
  }
}
