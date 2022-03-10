package dashboard.control;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class LoginController {


    private final String mailBaseUrl;
    private final Map<Integer, String> loaLevels;

    public LoginController(@Value("${mailBaseUrl}") String mailBaseUrl,
                           @Value("${loa_values_supported}") String loaLevels) {
        this.mailBaseUrl = mailBaseUrl;
        this.loaLevels = Arrays.stream(loaLevels.replaceAll("\"", "").split(","))
                .map(String::trim)
                .collect(Collectors.toMap(level -> Integer.valueOf(level.substring(level.length() - 1)), level -> level));
    }

    @RequestMapping(value = "/startSSO")
    public void login(HttpServletResponse response, @RequestParam("redirect_url") String redirectUrl) throws IOException {
        redirectUrl = URLDecoder.decode(redirectUrl, Charset.defaultCharset().name());
        if (!redirectUrl.toLowerCase().startsWith(mailBaseUrl.toLowerCase())) {
            throw new IllegalArgumentException("Open redirect attempt");
        }
        response.sendRedirect(redirectUrl);
    }

    @RequestMapping(value = "/login")
    public void start(HttpServletRequest request,
                      HttpServletResponse response,
                      @RequestParam("redirect_url") String redirectUrl,
                      @RequestParam(value = "loa", required = false) Integer loa)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        request.logout();
        //We could do this client side, but one extra redirect is not a problem
        String target = "/startSSO?redirect_url=" + redirectUrl;
        String shibbolethLogin = String.format("/Shibboleth.sso/Login?target=%s%s",
                URLEncoder.encode(target, Charset.defaultCharset()),
                convertLoa(loa));
        response.sendRedirect(shibbolethLogin);
    }

    private String convertLoa(Integer loa) {
        if (loa == null) {
            return "";
        }
        String loaLevel = loaLevels.get(loa);
        if (!StringUtils.hasText(loaLevel)) {
            throw new IllegalArgumentException("Not a valid loa level: " + loa);
        }
        return String.format("&authnContextClassRef=%s", URLEncoder.encode(loaLevel, Charset.defaultCharset()));

    }
}
