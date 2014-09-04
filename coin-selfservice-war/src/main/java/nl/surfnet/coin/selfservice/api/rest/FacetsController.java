package nl.surfnet.coin.selfservice.api.rest;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/facets")
public class FacetsController {

  @Resource
  private Csa csa;

  @RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RestResponse<ListHolder<Category>>> index() {
    List<Category> categories = csa.getTaxonomy().getCategories();
    return new ResponseEntity<>(new RestResponse<ListHolder<Category>>(new ListHolder(categories)), HttpStatus.OK);
  }
}
