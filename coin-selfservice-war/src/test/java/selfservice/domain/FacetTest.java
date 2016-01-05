package selfservice.domain;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

public class FacetTest {

  private ObjectMapper mapper = new ObjectMapper();

  @Test
  public void serializeAFacetWithFacetValues() throws JsonGenerationException, JsonMappingException, IOException {
    FacetValue cloud = FacetValue.builder().value("cloud").build();
    FacetValue hosted = FacetValue.builder().value("hosted").build();
    Facet facet = Facet.builder().name("category").addFacetValue(cloud).addFacetValue(hosted).build();

    Facet deserializedFacet = mapper.readValue(mapper.writeValueAsString(facet), Facet.class);

    assertThat(deserializedFacet.getFacetValues(), hasSize(2));
    assertThat(deserializedFacet.getFacetValues().first().getValue(), is("cloud"));
    assertThat(deserializedFacet.getFacetValues().first().getMultilingualString().getLocalizedStrings().get("en").getValue(), is("cloud"));
  }

}
