package selfservice.filter;

import selfservice.domain.IdentityProvider;
import selfservice.service.IdentityProviderService;
import selfservice.util.SpringSecurity;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

import java.io.IOException;
import java.util.Optional;

public class EnsureAccessToIdpFilter extends GenericFilterBean {

  private IdentityProviderService idpService;

  public EnsureAccessToIdpFilter(IdentityProviderService idpService) {
    this.idpService = idpService;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String idpEntityId = Optional.ofNullable(req.getHeader(HTTP_X_IDP_ENTITY_ID)).orElse(request.getParameter("idpEntityId"));

    if (!req.getRequestURI().contains("/users/me")) {
      IdentityProvider idp = idpService.getIdentityProvider(idpEntityId).orElseThrow(() -> new SecurityException(idpEntityId + " does not exist"));
      SpringSecurity.ensureAccess(idp);
    }
    chain.doFilter(request, response);
  }
}
