package selfservice.model;

import java.util.Arrays;
import java.util.List;

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
    List<CategoryValue> values = Arrays.asList(new CategoryValue("test_category_value"));
    category.setValues(values);
    Taxonomy taxonomy = new Taxonomy(Arrays.asList(category));
    this.objectMapper.writeValueAsString(taxonomy);
    //ok, no exception anymore
  }
}
