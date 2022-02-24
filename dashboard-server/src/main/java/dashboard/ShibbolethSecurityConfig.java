package dashboard;

import dashboard.filter.EnsureAccessToIdpFilter;
import dashboard.manage.Manage;
import dashboard.sab.Sab;
import dashboard.service.impl.JiraClient;
import dashboard.shibboleth.ShibbolethPreAuthenticatedProcessingFilter;
import dashboard.shibboleth.ShibbolethUserDetailService;
import dashboard.shibboleth.mock.MockShibbolethFilter;
import dashboard.shibboleth.mock.ShibbolethSSOFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ShibbolethSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ShibbolethSecurityConfig.class);

    @Autowired
    private Manage manage;

    @Autowired
    private Sab sab;

    @Autowired
    private JiraClient jiraClient;

    @Value("${dashboard.admin}")
    private String dashboardAdmin;

    @Value("${dashboard.viewer}")
    private String dashboardViewer;

    @Value("${dashboard.super.user}")
    private String dashboardSuperUser;

    @Value("${admin.surfconext.idp.sabRole}")
    private String adminSufConextIdpRole;

    @Value("${viewer.surfconext.idp.sabRole}")
    private String viewerSurfConextIdpRole;

    @Value("${dashboard.feature.shibboleth}")
    private boolean shibbolethEnabled;

    @Value("${dashboard.feature.consent}")
    private boolean isManageConsentEnabled;

    @Value("${dashboard.feature.oidc}")
    private boolean isOidcEnabled;

    @Value("${dashboard.hide_tabs}")
    private String hideTabs;

    @Value("${supported_language_codes}")
    private String supportedLanguages;

    @Value("${organization}")
    private String organization;

    @Value("${default_loa_level}")
    private String defaultLoa;

    @Value("${loa_values_supported}")
    private String loaLevels;

    /*
     * See http://stackoverflow.com/questions/22998731/httpsecurity-websecurity-and-authenticationmanagerbuilder
     * for a quick overview of the differences between the three configure overrides
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(
                        "/public/**",
                        "/css/**",
                        "/font/**",
                        "/images/**",
                        "/img/**",
                        "/js/**",
                        "/health",
                        "/info",
                        "/serviceProvider/api/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<String> loaLevels = Arrays.stream(this.loaLevels.replaceAll("\"", "").split(",")).map(String::trim).collect(Collectors.toList());
        http
                .logout()
                .logoutUrl("/dashboard/api/logout")
                .invalidateHttpSession(true)
                .deleteCookies("statsToken") // remove stats cookie
                .logoutSuccessHandler(new DashboardLogoutSuccessHandler())
                .addLogoutHandler(new DashboardLogoutHandler()).and()
                .csrf().disable()
                .addFilterBefore(
                        new ShibbolethPreAuthenticatedProcessingFilter(authenticationManagerBean(), manage, sab, jiraClient,
                                dashboardAdmin, dashboardViewer, dashboardSuperUser, adminSufConextIdpRole,
                                viewerSurfConextIdpRole, isManageConsentEnabled, isOidcEnabled, hideTabs, supportedLanguages, organization,
                                defaultLoa, loaLevels),
                        AbstractPreAuthenticatedProcessingFilter.class
                )
                .addFilterAfter(new EnsureAccessToIdpFilter(manage), ShibbolethPreAuthenticatedProcessingFilter.class)
                .authorizeRequests()
                .antMatchers("/identity/**").hasRole("DASHBOARD_SUPER_USER")
                .antMatchers("/dashboard/api/stats/**")
                .hasAnyRole("DASHBOARD_ADMIN", "DASHBOARD_VIEWER", "DASHBOARD_MEMBER", "DASHBOARD_SUPER_USER")
                .antMatchers("/dashboard/api/**")
                .hasAnyRole("DASHBOARD_ADMIN", "DASHBOARD_VIEWER", "DASHBOARD_MEMBER", "DASHBOARD_SUPER_USER", "DASHBOARD_GUEST")
                .anyRequest().authenticated();
            if (!shibbolethEnabled) {
                http.addFilterBefore(new MockShibbolethFilter(), ShibbolethPreAuthenticatedProcessingFilter.class);
                http.addFilterAfter(new ShibbolethSSOFilter(), ShibbolethPreAuthenticatedProcessingFilter.class);
            }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        LOG.info("Configuring AuthenticationManager with a PreAuthenticatedAuthenticationProvider");
        PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(new ShibbolethUserDetailService());
        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    private static class DashboardLogoutHandler implements LogoutHandler {

        private static final Logger LOG = LoggerFactory.getLogger(DashboardLogoutHandler.class);

        @Override
        public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                           Authentication authentication) {
            LOG.debug("Logging out user {}", authentication);

            Cookie statsToken = new Cookie("statsToken", "");
            statsToken.setMaxAge(0); //deletes the cookie
            httpServletResponse.addCookie(statsToken);
            SecurityContextHolder.getContext().setAuthentication(null);
            httpServletResponse.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }

    private static class DashboardLogoutSuccessHandler implements LogoutSuccessHandler {

        @Override
        public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }
}
