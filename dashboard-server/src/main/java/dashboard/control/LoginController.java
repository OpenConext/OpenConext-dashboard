package dashboard.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class LoginController {

    @RequestMapping(value = "/login")
    public void home(HttpServletResponse response) throws IOException {
        response.sendRedirect("/");
    }

}
