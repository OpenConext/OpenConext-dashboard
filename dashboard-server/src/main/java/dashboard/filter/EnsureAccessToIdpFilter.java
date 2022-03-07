package dashboard.filter;

import dashboard.domain.IdentityProvider;
import dashboard.manage.Manage;
import dashboard.util.SpringSecurity;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

import static dashboard.control.Constants.HTTP_X_IDP_ENTITY_ID;

public class EnsureAccessToIdpFilter extends GenericFilterBean {

    private final Manage manage;

    public EnsureAccessToIdpFilter(Manage manage) {
        this.manage = manage;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        if (shouldAccessToIdpBeChecked(req)) {
            String idpEntityId = Optional.ofNullable(req.getHeader(HTTP_X_IDP_ENTITY_ID)).orElse(request.getParameter("idpEntityId"));
            if (StringUtils.hasText(idpEntityId)) {
                IdentityProvider idp = manage.getIdentityProvider(idpEntityId, false).orElseThrow(() -> new SecurityException(idpEntityId + " does not exist"));
                SpringSecurity.ensureAccess(idp);
            }
        }
        chain.doFilter(request, response);
    }

    private boolean shouldAccessToIdpBeChecked(HttpServletRequest req) {
        String requestURI = req.getRequestURI();
        if (SpringSecurity.getCurrentUser().isGuest()) {
            return false;
        }
        return requestURI.startsWith("/dashboard/api") && !requestURI.contains("/users/me") && !requestURI.contains("/jsError");
    }
}
