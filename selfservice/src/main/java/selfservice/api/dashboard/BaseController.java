package selfservice.api.dashboard;

import java.util.Locale;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

@Controller
public abstract class BaseController {

  @Resource(name = "localeResolver")
  protected LocaleResolver localeResolver;

  @ModelAttribute(value = "locale")
  public Locale getLocale(HttpServletRequest request) {
    return localeResolver.resolveLocale(request);
  }

  public <T> RestResponse<T> createRestResponse(T payload) {
    ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = sra.getRequest();

    return RestResponse.of(this.getLocale(request), payload);
  }

}
