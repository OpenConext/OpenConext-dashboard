package nl.surfnet.coin.selfservice.api.rest;

import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
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

  @RequestMapping("/logout")
  public ResponseEntity<RestResponse> me(HttpServletRequest request, SessionStatus status) {
    status.setComplete();
    request.getSession().invalidate();
    SecurityContextHolder.getContext().setAuthentication(null);
    return new ResponseEntity(HttpStatus.OK);
  }
}
