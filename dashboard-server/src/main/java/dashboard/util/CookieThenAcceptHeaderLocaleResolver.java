package dashboard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class CookieThenAcceptHeaderLocaleResolver extends CookieLocaleResolver {

    private final static Logger LOG = LoggerFactory.getLogger(CookieThenAcceptHeaderLocaleResolver.class);

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        super.setLocaleContext(request, response, localeContext);
        Locale locale = (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
        if (locale != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    if (getCookieName().equals(cookie.getName())) {
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
