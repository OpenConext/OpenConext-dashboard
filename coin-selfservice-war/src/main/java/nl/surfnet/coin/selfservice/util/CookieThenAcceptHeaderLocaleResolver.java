package nl.surfnet.coin.selfservice.util;

import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CookieThenAcceptHeaderLocaleResolver extends CookieLocaleResolver {

  private static final List<String> SUPPORTED_LANGS = Arrays.asList("en", "nl");

  @Override
  protected Locale determineDefaultLocale(HttpServletRequest request) {
    Locale acceptLocale = request.getLocale();
    if (SUPPORTED_LANGS.contains(acceptLocale.getLanguage())) {
      return acceptLocale;
    } else {
      return super.determineDefaultLocale(request);
    }
  }
}
