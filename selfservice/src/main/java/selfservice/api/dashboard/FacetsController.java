package selfservice.api.dashboard;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.Service;
import selfservice.service.Csa;
import selfservice.util.SpringSecurity;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/dashboard/api/facets")
public class FacetsController extends BaseController {

  @Autowired
  private Csa csa;

  @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public RestResponse<List<Category>> index() {
    List<Service> servicesForIdp = csa.getServicesForIdp(SpringSecurity.getCurrentUser().getIdp().getId());
    Map<String, List<Category>> groupedCategories = servicesForIdp.stream().map(s -> s.getCategories()).flatMap
      (Collection::stream).collect(groupingBy(Category::getName));

    //ensure we make the values unique
    List<Category> categories = groupedCategories.entrySet().stream().map(entry -> {
      List<CategoryValue> categoryValues = entry.getValue().stream().map(cat -> cat.getValues().stream().map
        (CategoryValue::getValue)).flatMap(Function.identity()).collect(toSet()).stream().map(CategoryValue::new)
        .collect(toList());
      return new Category(entry.getKey(), categoryValues);
    }).collect(toList());

    return createRestResponse(categories);
  }


}
