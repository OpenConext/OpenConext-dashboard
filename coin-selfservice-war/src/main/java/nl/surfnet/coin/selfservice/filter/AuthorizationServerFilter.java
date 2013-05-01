package nl.surfnet.coin.selfservice.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class AuthorizationServerFilter extends org.surfnet.oaaas.auth.AuthorizationServerFilter {

  /* (non-Javadoc)
   * @see org.surfnet.oaaas.auth.AuthorizationServerFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    if (httpServletRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
      //due to CORS preflight checks, this is always ok
      chain.doFilter(servletRequest, servletResponse);
    } else {
      super.doFilter(servletRequest, servletResponse, chain);
    }
  }
}
