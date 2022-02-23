package dashboard.shibboleth.mock;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import static dashboard.control.Constants.HTTP_X_IDP_ENTITY_ID;
import static dashboard.shibboleth.ShibbolethHeader.*;

public class ShibbolethSSOFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        if (requestURI.endsWith("Shibboleth.sso/Login")) {
            String authnContextClassRef = request.getParameter("authnContextClassRef");
            response.sendRedirect();
        } else {
            chain.doFilter(servletRequest, response);
        }

    }
}
