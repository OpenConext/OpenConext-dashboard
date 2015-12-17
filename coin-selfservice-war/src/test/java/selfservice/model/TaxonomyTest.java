package selfservice.model;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.Taxonomy;

public class TaxonomyTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testCyclicDependencyJsonDeserialization() throws Exception {
    Category category = new Category("test_category");
    category.setValues(singletonList(new CategoryValue("test_category_value")));

    Taxonomy taxonomy = new Taxonomy(singletonList(category));

    String jsonTaxonomy = objectMapper.writeValueAsString(taxonomy);

    assertThat(jsonTaxonomy, containsString("test_category_value"));
  }
}
