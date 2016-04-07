package selfservice.api.dashboard;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;

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
