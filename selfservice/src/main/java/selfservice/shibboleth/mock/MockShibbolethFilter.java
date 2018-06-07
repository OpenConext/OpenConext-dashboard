package selfservice.shibboleth.mock;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;
import static selfservice.shibboleth.ShibbolethHeader.Name_Id;
import static selfservice.shibboleth.ShibbolethHeader.Shib_Authenticating_Authority;
import static selfservice.shibboleth.ShibbolethHeader.Shib_DisplayName;
import static selfservice.shibboleth.ShibbolethHeader.Shib_SchacPersonalUniqueCode;
import static selfservice.shibboleth.ShibbolethHeader.Shib_Uid;
import static selfservice.shibboleth.ShibbolethPreAuthenticatedProcessingFilter.shibHeaders;

public class MockShibbolethFilter extends GenericFilterBean {

    public static final String idp = "http://mock-idp";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
        ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String userId = Optional.ofNullable(request.getParameter("mockUser"))
            .orElse((String) req.getSession().getAttribute("mockShibbolethUser"));

        if (userId == null) {
            String login = IOUtils.toString(new ClassPathResource("mockLogin.html").getInputStream());
            response.getWriter().write(login);
        } else {


            req.getSession(true).setAttribute("mockShibbolethUser", userId);
            SetHeader wrapper = new SetHeader(req);
            wrapper.setHeader(Name_Id.getValue(), userId);
            wrapper.setHeader(Shib_Uid.getValue(), userId);
            wrapper.setHeader(Shib_Authenticating_Authority.getValue(), idp);
            wrapper.setHeader(Shib_DisplayName.getValue(), "Jane Roe");
            wrapper.setHeader(Shib_SchacPersonalUniqueCode.getValue(), "schac_personal_unique_code");
            wrapper.setHeader(HTTP_X_IDP_ENTITY_ID, idp);

            wrapper.setHeader(shibHeaders.get("urn:mace:dir:attribute-def:eduPersonEntitlement").getValue(),
                "urn:mace:terena.org:tcs:personal-user;some-filtered-value");

            chain.doFilter(wrapper, response);
        }
    }

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

}
