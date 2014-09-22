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

public class EnsureAccessToIdp extends HandlerInterceptorAdapter {

  @Resource
  private Csa csa;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if(!request.getPathInfo().endsWith("/users/me")) {
      SpringSecurity.ensureAccess(csa, request.getHeader(HTTP_X_IDP_ENTITY_ID));
    }
    return true;
  }

  public void setCsa(Csa csa) {
    this.csa = csa;
  }
}
