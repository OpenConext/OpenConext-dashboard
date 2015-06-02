package nl.surfnet;


import nl.surfnet.coin.selfservice.filter.EnsureAccessToIdpFilter;
import nl.surfnet.coin.selfservice.filter.VootFilter;
import nl.surfnet.coin.selfservice.service.Csa;
import nl.surfnet.coin.selfservice.service.VootClient;
import nl.surfnet.coin.selfservice.shibboleth.ShibbolethPreAuthenticatedProcessingFilter;
import nl.surfnet.coin.selfservice.shibboleth.ShibbolethUserDetailService;
import nl.surfnet.coin.selfservice.shibboleth.mock.MockShibbolethFilter;
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
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class ShibbolethSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ShibbolethSecurityConfig.class);

  @Autowired
  private Csa csa;

  @Autowired
  private VootClient vootClient;

  @Value("${dashboard.admin}")
  private String dashboardAdmin;

  @Value("${dashboard.viewer}")
  private String dashboardViewer;

  @Value("${dashboard.super.user}")
  private String dashboardSuperUser;

  @Bean
  @Profile("dev")
  public FilterRegistrationBean mockShibbolethFilter() {
    FilterRegistrationBean shibFilter = new FilterRegistrationBean();
    shibFilter.setFilter(new MockShibbolethFilter());
    shibFilter.addUrlPatterns("/*");
    shibFilter.setOrder(1);
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
      .antMatchers("/home", "/forbidden", "/css/**", "/font/**", "/images/**", "/js/**", "/health")
    ;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf().disable()
      .addFilterBefore(
        new ShibbolethPreAuthenticatedProcessingFilter(authenticationManagerBean(), csa),
        AbstractPreAuthenticatedProcessingFilter.class
      )
      .addFilterAfter(new VootFilter(vootClient, dashboardAdmin, dashboardViewer, dashboardSuperUser), ShibbolethPreAuthenticatedProcessingFilter.class)
      .addFilterAfter(new EnsureAccessToIdpFilter(csa), VootFilter.class)
      .authorizeRequests()
      .antMatchers("/identity/**").hasRole("DASHBOARD_SUPER_USER")
      .antMatchers("/**").hasAnyRole("DASHBOARD_ADMIN", "DASHBOARD_VIEWER", "DASHBOARD_SUPER_USER")
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
}
