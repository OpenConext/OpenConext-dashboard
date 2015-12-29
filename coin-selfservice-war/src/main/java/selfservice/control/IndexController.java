package selfservice.control;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import selfservice.api.dashboard.BaseController;

@Controller
@RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
public class IndexController extends BaseController {

  @RequestMapping("/")
  public void toShopAdmin(HttpServletResponse response) throws IOException {
    response.sendRedirect("/shopadmin/all-spslmng.shtml");
  }

  @RequestMapping(value = "/forbidden")
  public ModelAndView forbidden() {
    return new ModelAndView("forbidden");
  }

  @RequestMapping(value = "/home")
  public void home(@RequestParam(value = "redirectTo", required = false, defaultValue = "/") String redirectTo,
                   HttpServletRequest request,
                   HttpServletResponse response) throws IOException {
    response.sendRedirect(redirectTo);
  }

}
