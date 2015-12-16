package selfservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@EnableWebSecurity
//@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${admin.distribution.channel.teamname}")
  private String adminDistributionTeam;


  @Override
  public void configure(WebSecurity web) throws Exception {
    web
      .ignoring()
      .antMatchers("/api/**", "/public/**", "/css/**", "/font/**", "/images/**", "/img/**", "/js/**", "/health")
    ;
  }

//  @Bean
//  @Profile("dev")
//  public FilterRegistrationBean mockShibbolethFilter() {
//    FilterRegistrationBean shibFilter = new FilterRegistrationBean();
//    shibFilter.setFilter(new MockShibbolethFilter());
//    shibFilter.addUrlPatterns("/shopadmin/*");
//    shibFilter.setOrder(1);
//    return shibFilter;
//  }
//
//  @Bean
//  public FilterRegistrationBean authorizationServerFilter(Environment environment,
//                                                          @Value("${oauth.checkToken.endpoint.url}") String oauthCheckTokenEndpointUrl,
//                                                          @Value("${oauth.checkToken.clientId}") String oauthCheckTokenClientId,
//                                                          @Value("${oauth.checkToken.secret}") String oauthCheckTokenSecret) {
//    final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//    filterRegistrationBean.addUrlPatterns("/api/*");
//
//    if (environment.acceptsProfiles(Application.DEV_PROFILE_NAME)) {
//      filterRegistrationBean.setFilter(new MockAuthorizationServerFilter());
//    } else {
//      final AuthorizationServerFilter authorizationServerFilter = new AuthorizationServerFilter(oauthCheckTokenEndpointUrl, oauthCheckTokenClientId, oauthCheckTokenSecret);
//      filterRegistrationBean.setFilter(authorizationServerFilter);
//    }
//
//    return filterRegistrationBean;
//  }
}
