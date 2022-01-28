package dashboard.control;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

@RestController
public class LoginController {

    @RequestMapping(value = "/startSSO")
    public void login(HttpServletResponse response, @RequestParam("redirect_url") String redirectUrl) throws IOException {
        redirectUrl = URLDecoder.decode(redirectUrl, Charset.defaultCharset().name());
        response.sendRedirect(redirectUrl);
    }

    @RequestMapping(value = "/login")
    public void start(HttpServletRequest request, HttpServletResponse response, @RequestParam("redirect_url") String redirectUrl)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        request.logout();
        response.sendRedirect("/startSSO?redirect_url=" + redirectUrl);
    }
}
