package nl.surfnet.coin.selfservice.interceptor;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

public class EnsureCurrentIdpSet extends HandlerInterceptorAdapter {

  @Resource
  private Csa csa;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    // TODO not needed anymore? Remove.
//    SpringSecurity.setCurrentIdp(csa, request.getHeader(HTTP_X_IDP_ENTITY_ID));
    return true;
  }
}
