package selfservice.filter;

import org.springframework.web.filter.GenericFilterBean;
import selfservice.domain.IdentityProvider;
import selfservice.serviceregistry.ServiceRegistry;
import selfservice.util.SpringSecurity;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

public class EnsureAccessToIdpFilter extends GenericFilterBean {

  private static final String DASHBOARD_API_PREFIX = "/dashboard/api";

  private ServiceRegistry serviceRegistry;

  public EnsureAccessToIdpFilter(ServiceRegistry serviceRegistry) {
    this.serviceRegistry = serviceRegistry;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;

    if (shouldAccessToIdpBeChecked(req)) {
      String idpEntityId = Optional.ofNullable(req.getHeader(HTTP_X_IDP_ENTITY_ID)).orElse(request.getParameter("idpEntityId"));
      IdentityProvider idp = serviceRegistry.getIdentityProvider(idpEntityId).orElseThrow(() -> new SecurityException(idpEntityId + " does not exist"));
      SpringSecurity.ensureAccess(idp);
    }

    chain.doFilter(request, response);
  }

  private boolean shouldAccessToIdpBeChecked(HttpServletRequest req) {
    return req.getRequestURI().startsWith(DASHBOARD_API_PREFIX) && !req.getRequestURI().contains("/users/me");
  }
}
