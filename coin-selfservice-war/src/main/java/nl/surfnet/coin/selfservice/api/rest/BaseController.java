package nl.surfnet.coin.selfservice.api.rest;

import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class BaseController {

  @Resource(name = "localeResolver")
  protected LocaleResolver localeResolver;

  @ModelAttribute(value = "locale")
  public Locale getLocale(HttpServletRequest request) {
    return localeResolver.resolveLocale(request);
  }

  public RestResponse createRestResponse(Object payload) {
    ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = sra.getRequest();

    return new RestResponse(this.getLocale(request), payload);
  }
}
