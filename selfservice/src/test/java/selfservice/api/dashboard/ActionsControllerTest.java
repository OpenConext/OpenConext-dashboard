package selfservice.api.dashboard;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

import java.time.ZonedDateTime;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.LocaleResolver;

import selfservice.domain.Action;
import selfservice.service.ActionsService;

@RunWith(MockitoJUnitRunner.class)
public class ActionsControllerTest {

  @InjectMocks
  private ActionsController controller;

  @Mock
  private ActionsService actionsServiceMock;
  @Mock
  private LocaleResolver localeResolverMock;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    mockMvc = standaloneSetup(controller).build();

    when(localeResolverMock.resolveLocale(any(HttpServletRequest.class))).thenReturn(Locale.ENGLISH);
  }

  @Test
  public void getActionsForAnIdp() throws Exception {
    Action action = Action.builder()
        .jiraKey("jiraKey").userName("userName").userEmail("userEmail")
        .type(Action.Type.LINKREQUEST).status("Open")
        .body("body").idpId("idpId").spId("spId").requestDate(ZonedDateTime.now()).build();

    when(actionsServiceMock.getActions("idpId")).thenReturn(ImmutableList.of(action));

    this.mockMvc.perform(get("/dashboard/api/actions")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HTTP_X_IDP_ENTITY_ID, "idpId"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.payload").isArray())
      .andExpect(jsonPath("$.payload[0].userEmail").value("userEmail"));
  }
}
