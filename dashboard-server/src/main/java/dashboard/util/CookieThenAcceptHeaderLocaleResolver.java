package dashboard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;

public class CookieThenAcceptHeaderLocaleResolver extends CookieLocaleResolver {

    private final static Logger LOG = LoggerFactory.getLogger(CookieThenAcceptHeaderLocaleResolver.class);

    private final String cookieName;

    public CookieThenAcceptHeaderLocaleResolver() {
        this(DEFAULT_COOKIE_NAME);
    }

    public CookieThenAcceptHeaderLocaleResolver(String cookieName) {
        super(cookieName);
        this.cookieName = cookieName;
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        super.setLocaleContext(request, response, localeContext);
        Locale locale = (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
        if (locale != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    if (cookieName.equals(cookie.getName())) {
                        cookie.setValue(locale.toString());
                        LOG.debug("Setting cookie name '{}' domain '{}' path '{}' value '{}'",
                                cookie.getName(), cookie.getDomain(), cookie.getPath(), locale.toString());
                        response.addCookie(cookie);
                    }
                }
            }
        }
    }
}
