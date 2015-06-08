package nl.surfnet.coin.selfservice.filter;


import nl.surfnet.coin.selfservice.service.Csa;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

public class EnsureAccessToIdpFilter extends GenericFilterBean {

  private Csa csa;

  public EnsureAccessToIdpFilter(Csa csa) {
    this.csa = csa;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String idpEntityId = req.getHeader(HTTP_X_IDP_ENTITY_ID);
    if (idpEntityId == null) {
      idpEntityId = request.getParameter("idpEntityId");
    }

    if (!req.getRequestURI().contains("/users/me")) {
      SpringSecurity.ensureAccess(csa, idpEntityId);
    }
    chain.doFilter(request, response);

  }
}
