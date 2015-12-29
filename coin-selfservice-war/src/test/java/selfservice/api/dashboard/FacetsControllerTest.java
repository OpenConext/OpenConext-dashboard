package selfservice.api.dashboard;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.google.common.collect.ImmutableList;

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
import selfservice.domain.Taxonomy;
import selfservice.service.Csa;
import selfservice.util.CookieThenAcceptHeaderLocaleResolver;

@RunWith(MockitoJUnitRunner.class)
public class FacetsControllerTest {
  @InjectMocks
  private FacetsController controller;

  @Mock
  private Csa csaMock;

  private MockMvc mockMvc;

  private Taxonomy taxonomy = new Taxonomy(ImmutableList.of(new Category("foo")));

  @Before
  public void setup() {
    controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();

    mockMvc = standaloneSetup(controller)
      .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
  }

  @Test
  public void thatFacetsAreRetrievedFromCsa() throws Exception {
    when(csaMock.getTaxonomy()).thenReturn(taxonomy);

    mockMvc.perform(
      get("/dashboard/api/facets").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.payload").isArray())
      .andExpect(jsonPath("$.payload[0].name").value("foo"))
    ;
  }
}
