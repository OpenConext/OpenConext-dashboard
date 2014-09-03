package nl.surfnet.coin.selfservice.interceptor;

import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static nl.surfnet.coin.selfservice.control.rest.Constants.HTTP_X_IDP_ENTITY_ID;

public class EnsureCurrentIdpSet extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    SpringSecurity.setCurrentIdp(request.getHeader(HTTP_X_IDP_ENTITY_ID));
    return true;
  }
}
