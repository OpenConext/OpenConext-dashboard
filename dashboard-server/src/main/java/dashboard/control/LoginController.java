package dashboard.control;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class LoginController {

    @RequestMapping(value = "/startSSO")
    public void login(HttpServletResponse response) throws IOException {
        response.sendRedirect("/");
    }

    @RequestMapping(value = "/login")
    public void start(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        request.logout();
        response.sendRedirect("/startSSO");
    }
}
