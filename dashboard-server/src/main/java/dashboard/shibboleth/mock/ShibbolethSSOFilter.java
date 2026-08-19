package dashboard.shibboleth.mock;

import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ShibbolethSSOFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        if (requestURI.endsWith("Shibboleth.sso/Login")) {
            String target = request.getParameter("target");
            String redirectUrl = UriComponentsBuilder.fromUriString(target).build().getQueryParams().getFirst("redirect_url");
            response.sendRedirect(redirectUrl);
        } else {
            chain.doFilter(servletRequest, response);
        }

    }
}
