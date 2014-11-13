package nl.surfnet.coin.selfservice.api.rest;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Category;
import nl.surfnet.coin.csa.model.Taxonomy;
import nl.surfnet.coin.selfservice.util.CookieThenAcceptHeaderLocaleResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class FacetsControllerIntegrationTest {
  private MockMvc mockMvc;

  @InjectMocks
  private FacetsController controller;

  @Mock
  private Csa csa;
  private Taxonomy taxonomy;

  @Before
  public void setup() {
    controller = new FacetsController();
    controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();

    MockitoAnnotations.initMocks(this);

    this.mockMvc = standaloneSetup(controller)
      .setMessageConverters(new MappingJacksonHttpMessageConverter()).build();
    taxonomy = new Taxonomy();
    taxonomy.setCategories(Arrays.asList(new Category("foo")));
  }

  @Test
  public void thatFacetsAreRetrievedFromCsa() throws Exception {

    when(csa.getTaxonomy()).thenReturn(taxonomy);

    this.mockMvc.perform(
      get("/facets").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.payload").isArray())
      .andExpect(jsonPath("$.payload[0].name").value("foo"))
    ;
  }
}
