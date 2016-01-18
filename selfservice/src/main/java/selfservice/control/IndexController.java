package selfservice.control;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
public class IndexController{

  @RequestMapping("/")
  public void toShopAdmin(HttpServletResponse response) throws IOException {
    response.sendRedirect("/shopadmin/all-spslmng.shtml");
  }

}
