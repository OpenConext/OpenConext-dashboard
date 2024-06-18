package dashboard.control;

import dashboard.domain.CoinUser;
import dashboard.domain.IdentityProvider;
import dashboard.util.SpringSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LoginController {

    public final static String IDP_ID_COOKIE_NAME = "IDP_ID_COOKIE_NAME";

    private final String mailBaseUrl;
    private final Map<Integer, String> loaLevels;
    private final boolean secureCookie;

    public LoginController(@Value("${mailBaseUrl}") String mailBaseUrl,
                           @Value("${loa_values_supported}") String loaLevels,
                           @Value("${server.servlet.session.cookie.secure}") boolean secureCookie) {
        this.mailBaseUrl = mailBaseUrl;
        this.loaLevels = Arrays.stream(loaLevels.replaceAll("\"", "").split(","))
                .map(String::trim)
                .collect(Collectors.toMap(level -> Integer.valueOf(level.substring(level.length() - 1)), level -> level));
        this.secureCookie = secureCookie;
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
        //Need to remember the switched IdP, we will check if the IdP is allowed on login again
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        Optional<IdentityProvider> switchedToIdp = currentUser.getSwitchedToIdp();
        switchedToIdp.ifPresentOrElse(idp -> {
            Cookie cookie = new Cookie(IDP_ID_COOKIE_NAME, idp.getId());
            cookie.setSecure(secureCookie);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }, () -> {
            Cookie cookie = new Cookie(IDP_ID_COOKIE_NAME, "");
            cookie.setSecure(secureCookie);
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        });
        SecurityContextHolder.clearContext();

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
