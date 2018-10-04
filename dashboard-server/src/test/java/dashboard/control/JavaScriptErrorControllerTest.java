package dashboard.control;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.time.Year;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MockMvc;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class JavaScriptErrorControllerTest {

  private JavaScriptErrorController controller = new JavaScriptErrorController();

  private ListAppender<ILoggingEvent> listAppender;
  private MockMvc mockMvc;

  @Before
  public void setup() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    listAppender = new ListAppender<>();
    listAppender.setContext(loggerContext);
    listAppender.start();

    Logger logger = (Logger) LoggerFactory.getLogger(JavaScriptErrorController.class);
    logger.addAppender(listAppender);

    this.mockMvc = standaloneSetup(controller)
      .dispatchOptions(true).build();
  }

  @Test
  public void testReportError() throws Exception {
    mockMvc.perform(post("/dashboard/api/jsError").contentType(APPLICATION_JSON).content("{\"error\": \"whoops\"}"))
      .andExpect(status().isOk());

    List<ILoggingEvent> logs = listAppender.list;

    assertThat(logs, hasSize(1));

    ILoggingEvent error = logs.get(0);
    assertThat(error.getFormattedMessage(), containsString("whoops"));
    assertThat(error.getFormattedMessage(), containsString("dateTime"));
    assertThat(error.getFormattedMessage(), containsString("" + Year.now().getValue()));
  }
}
