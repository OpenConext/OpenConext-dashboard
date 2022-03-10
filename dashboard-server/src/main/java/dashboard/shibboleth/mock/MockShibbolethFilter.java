package dashboard.shibboleth.mock;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import static dashboard.control.Constants.HTTP_X_IDP_ENTITY_ID;
import static dashboard.shibboleth.ShibbolethHeader.*;

@SuppressWarnings("unchecked")
public class MockShibbolethFilter extends GenericFilterBean {

    public static final String idp = "https://idp.surfnet.nl";//"https://localhost.surf.id"; //"https://idp.surf.nl"
    public String role = "admin";//"admin";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();
        if (requestURI.endsWith("startSSO")) {
            role = "admin";
        }
        if ("none".equals(role)) {
            chain.doFilter(request, response);

        } else {
            SetHeader wrapper = new SetHeader(request);
            wrapper.setHeader(Name_Id.getValue(), role);
            wrapper.setHeader(Shib_Uid.getValue(), role);
            wrapper.setHeader(Shib_Authenticating_Authority.getValue(), idp);
            wrapper.setHeader(Shib_Email.getValue(), "jane.roe@example.org");
            wrapper.setHeader(Shib_EduPersonPN.getValue(), "Some eduPersonPrincipalName");
            wrapper.setHeader(Shib_DisplayName.getValue(), "Jane Roe");
            wrapper.setHeader(Shib_GivenName.getValue(), "Jane");
            wrapper.setHeader(Shib_SurName.getValue(), "Roe");
            wrapper.setHeader(Shib_SchacPersonalUniqueCode.getValue(), "schac_personal_unique_code");
            wrapper.setHeader(Shib_EduPersonAffiliation.getValue(), "some affiliation");
            wrapper.setHeader(Shib_EduPersonEntitlement.getValue(),
                    "urn:mace:terena.org:tcs:personal-user;some-filtered-value");
            wrapper.setHeader(Shib_EduPersonScopedAffiliation.getValue(),
                    "urn:mace:terena.org:tcs:eduPersonScopedAffiliation");
            wrapper.setHeader(Shib_SURFEckid.getValue(), "some surf eckid value");
            wrapper.setHeader(HTTP_X_IDP_ENTITY_ID, idp);
            wrapper.setHeader(Shib_AuthnContext_Class.getValue(), "urn:oasis:names:tc:SAML:2.0:ac:classes:Password");

            switch (role) {
                case "super":
                    wrapper.setHeader(Shib_MemberOf.getValue(), "dashboard.super.user");
                    break;
                case "admin":
                    wrapper.setHeader(Shib_MemberOf.getValue(), "dashboard.admin");
                    break;
                case "viewer":
                    wrapper.setHeader(Shib_MemberOf.getValue(), "dashboard.viewer");
                    break;
                default:
                    //nothing
            }
            if (requestURI.endsWith("Shibboleth.sso/Login")) {
                String authnContextClassRef = request.getParameter("authnContextClassRef");
                wrapper.setHeader(Shib_AuthnContext_Class.getValue(), URLDecoder.decode(authnContextClassRef, Charset.defaultCharset()));
            }
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

        @Override
        public Enumeration getHeaders(String name) {
            if (headers.containsKey(name)) {
                return Collections.enumeration(Collections.singletonList(headers.get(name)));
            }
            return super.getHeaders(name);
        }
    }

}
