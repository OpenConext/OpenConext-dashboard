package selfservice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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

import selfservice.filter.EnsureAccessToIdpFilter;
import selfservice.filter.SabEntitlementsFilter;
import selfservice.filter.VootFilter;
import selfservice.sab.Sab;
import selfservice.service.IdentityProviderService;
import selfservice.service.VootClient;
import selfservice.shibboleth.ShibbolethPreAuthenticatedProcessingFilter;
import selfservice.shibboleth.ShibbolethUserDetailService;
import selfservice.shibboleth.mock.MockShibbolethFilter;

@Configuration
@EnableWebSecurity
public class ShibbolethSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ShibbolethSecurityConfig.class);

  @Autowired
  private VootClient vootClient;

  @Autowired
  private IdentityProviderService idpService;

  @Autowired
  private Sab sab;

  @Value("${dashboard.admin}")
  private String dashboardAdmin;

  @Value("${dashboard.viewer}")
  private String dashboardViewer;

  @Value("${dashboard.super.user}")
  private String dashboardSuperUser;

  @Value("${admin.distribution.channel.teamname}")
  private String adminDistributionTeam;

  @Value("${admin.surfconext.idp.sabRole}")
  private String adminSufConextIdpRole;

  @Value("${viewer.surfconext.idp.sabRole}")
  private String viewerSurfConextIdpRole;

  @Bean
  @Profile("dev")
  public FilterRegistrationBean mockShibbolethFilter() {
    FilterRegistrationBean shibFilter = new FilterRegistrationBean();
    shibFilter.setFilter(new MockShibbolethFilter());
    shibFilter.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
    return shibFilter;
  }

  /*
   * See http://stackoverflow.com/questions/22998731/httpsecurity-websecurity-and-authenticationmanagerbuilder
   * for a quick overview of the differences between the three configure overrides
   */
  @Override
  public void configure(WebSecurity web) throws Exception {
    web
      .ignoring()
      .antMatchers("/home", "/forbidden", "/public/**", "/css/**", "/font/**", "/images/**", "/img/**", "/js/**", "/health", "/info");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .logout()
        .invalidateHttpSession(true)
        .deleteCookies("statsToken") // remove stats cookie
        .logoutSuccessHandler(new DashboardLogoutSuccessHandler())
        .addLogoutHandler(new DashboardLogoutHandler()).and()
      .csrf().disable()
      .addFilterBefore(
        new ShibbolethPreAuthenticatedProcessingFilter(authenticationManagerBean(), idpService),
        AbstractPreAuthenticatedProcessingFilter.class
      )
      .addFilterAfter(
          new VootFilter(vootClient, dashboardAdmin, dashboardViewer, dashboardSuperUser, adminDistributionTeam),
          ShibbolethPreAuthenticatedProcessingFilter.class)
      .addFilterAfter(new SabEntitlementsFilter(sab, adminSufConextIdpRole, viewerSurfConextIdpRole), VootFilter.class)
      .addFilterAfter(new EnsureAccessToIdpFilter(idpService), SabEntitlementsFilter.class)
      .authorizeRequests()
      .antMatchers("/shopadmin/**").hasRole("DISTRIBUTION_CHANNEL_ADMIN")
      .antMatchers("/identity/**").hasRole("DASHBOARD_SUPER_USER")
      .antMatchers("/dashboard/api/**").hasAnyRole("DASHBOARD_ADMIN", "DASHBOARD_VIEWER", "DASHBOARD_SUPER_USER")
      .anyRequest().authenticated();
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
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
      LOG.debug("Logging out user {}", authentication);

      Cookie statsToken = new Cookie("statsToken", "");
      statsToken.setMaxAge(0); //deletes the cookie
      httpServletResponse.addCookie(statsToken);
      SecurityContextHolder.getContext().setAuthentication(null);
      httpServletResponse.setStatus(204); // 204 No content
    }
  }

  private static class DashboardLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
      response.setStatus(204); // 204 No content
    }
  }
}
