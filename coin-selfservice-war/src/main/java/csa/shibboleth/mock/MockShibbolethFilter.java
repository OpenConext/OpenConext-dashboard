package csa.shibboleth.mock;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.filter.GenericFilterBean;

public class MockShibbolethFilter extends GenericFilterBean {

  private static class SetHeader extends HttpServletRequestWrapper {

    private final HashMap<String, String> headers;

    public SetHeader(HttpServletRequest request) {
      super(request);
      this.headers = new HashMap<>();
    }

    public void setHeader(String name, String value) {
      this.headers.put(name, value);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
      List<String> names = Collections.list(super.getHeaderNames());
      names.addAll(headers.keySet());
      return Collections.enumeration(names);
    }

    @Override
    public String getHeader(String name) {
      if (headers.containsKey(name)) {
        return headers.get(name);
      }
      return super.getHeader(name);
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String userId = request.getParameter("mockUser");//"admin";
    if (userId == null) {
      userId = (String) req.getSession().getAttribute("mockShibbolethUser");
    }
    if (userId == null) {
      IOUtils.copy(new ClassPathResource("mockLogin.html").getInputStream(), response.getOutputStream());
    } else {
      req.getSession(true).setAttribute("mockShibbolethUser", userId);
      SetHeader wrapper = new SetHeader(req);
      wrapper.setHeader("name-id", userId);
      wrapper.setHeader("Shib-uid", userId);
      String idp = "http://mock-idp";
      wrapper.setHeader("Shib-Authenticating-Authority", idp);
      wrapper.setHeader("Shib-displayName", "Ben Vonk");
      chain.doFilter(wrapper, response);
    }
  }

}
