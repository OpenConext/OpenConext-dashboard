package selfservice.util;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableList;

import org.springframework.web.servlet.i18n.CookieLocaleResolver;

public class CookieThenAcceptHeaderLocaleResolver extends CookieLocaleResolver {

  private static final List<String> SUPPORTED_LANGS = ImmutableList.of("en", "nl");

  @Override
  protected Locale determineDefaultLocale(HttpServletRequest request) {
    Locale acceptLocale = request.getLocale();

    return SUPPORTED_LANGS.contains(acceptLocale.getLanguage()) ? acceptLocale : super.determineDefaultLocale(request);
  }
}
