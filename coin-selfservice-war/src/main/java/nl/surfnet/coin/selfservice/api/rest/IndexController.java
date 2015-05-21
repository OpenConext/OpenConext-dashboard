package nl.surfnet.coin.selfservice.api.rest;


import nl.surfnet.coin.csa.Csa;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

@Controller
@RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
public class IndexController extends BaseController {

  @RequestMapping(value="/forbidden")
  public ModelAndView forbidden() {
    return new ModelAndView("forbidden");
  }

  @RequestMapping(value="/home")
  public void home(HttpServletResponse response) throws IOException {
    response.sendRedirect("/");
  }
}
