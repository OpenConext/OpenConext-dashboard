package selfservice.api.dashboard;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/dashboard/api", produces = MediaType.TEXT_HTML_VALUE)
public class DashboardController {

  @RequestMapping(value = "/forbidden")
  public String forbidden() {
    return "forbidden";
  }

  @RequestMapping(value = "/home")
  public void home(@RequestParam(value = "redirectTo", required = false, defaultValue = "/") String redirectTo,
                   HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendRedirect(redirectTo);
  }

}
