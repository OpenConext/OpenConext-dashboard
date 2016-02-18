package selfservice.api.dashboard;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import selfservice.domain.Category;
import selfservice.service.Csa;

@RestController
@RequestMapping("/dashboard/api/facets")
public class FacetsController extends BaseController {

  @Autowired
  private Csa csa;

  @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public RestResponse<List<Category>> index(HttpServletRequest request) {
    List<Category> categories = csa.getTaxonomy().getCategories();
    return createRestResponse(categories);
  }
}
