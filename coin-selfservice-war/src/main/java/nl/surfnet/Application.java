package nl.surfnet;


import nl.surfnet.coin.selfservice.api.rest.GsonHttpMessageConverter;
import nl.surfnet.coin.selfservice.service.Csa;
import nl.surfnet.coin.selfservice.service.VootClient;
import nl.surfnet.coin.selfservice.service.impl.CsaImpl;
import nl.surfnet.coin.selfservice.service.impl.CsaMock;
import nl.surfnet.coin.selfservice.service.impl.VootClientImpl;
import nl.surfnet.coin.selfservice.service.impl.VootClientMock;
import nl.surfnet.coin.selfservice.util.CookieThenAcceptHeaderLocaleResolver;
import nl.surfnet.sab.HttpClientTransport;
import nl.surfnet.sab.Sab;
import nl.surfnet.sab.SabClient;
import nl.surfnet.sab.SabClientMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.TraceRepositoryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.TraceWebFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;


@SpringBootApplication
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, FreeMarkerAutoConfiguration.class, TraceWebFilterAutoConfiguration.class, TraceRepositoryAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Application.class);
  }

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public LocaleResolver localeResolver() {
    final CookieThenAcceptHeaderLocaleResolver localeResolver = new CookieThenAcceptHeaderLocaleResolver();
    localeResolver.setCookieName("dashboardLang");
    localeResolver.setDefaultLocale(new Locale("nl"));
    localeResolver.setCookieMaxAge(315360000);
    return localeResolver;
  }

  @Bean
  @Autowired
  public WebMvcConfigurerAdapter webMvcConfigurerAdapter(@Value("${statsBaseUrl}") String statsBaseUrl,
                                                         @Value("${statsClientId}") String statsClientId,
                                                         @Value("${statsScope}") String statsScope,
                                                         @Value("${statsRedirectUri}") String statsRedirectUri
  ) {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addInterceptors(InterceptorRegistry registry) {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
        registry.addInterceptor(new HandlerInterceptorAdapter() {
          @Override
          public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
            // add this header as an indication to the JS-client that this is a regular, non-session-expired response.
            response.addHeader("sessionAlive", "success");
            return true;
          }
        });
      }

      @Override
      public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new GsonHttpMessageConverter(statsBaseUrl, statsClientId, statsScope, statsRedirectUri));
      }
    };
  }

  @Bean
  @Autowired
  @Profile("!dev")
  public Csa csaClient(
    @Value("${csa.base.url}") String csaBaseLocation,
    @Value("${csa.client.key}") String clientKey,
    @Value("${csa.client.secret}") String clientSecret,
    @Value("${csa.oauth2.accessTokenUrl}") String accessTokenUrl) {

    return new CsaImpl(accessTokenUrl, clientKey, clientSecret, "actions cross-idp-services stats", csaBaseLocation);
  }

  @Bean
  @Autowired
  @Profile("dev")
  public Csa csaMockClient() {
    return new CsaMock();
  }

  @Bean
  @Autowired
  @Profile("!dev")
  public Sab sab(HttpClientTransport httpClientTransport) {
    return new SabClient(httpClientTransport);
  }


  @Bean
  @Autowired
  @Profile("dev")
  public Sab sabMock() {
    return new SabClientMock();
  }

  @Bean
  @Autowired
  @Profile("!dev")
  public VootClient vootClient(Environment environment,
                               @Value("${voot.accessTokenUri}") String accessTokenUri,
                               @Value("${voot.clientId}") String clientId,
                               @Value("${voot.clientSecret}") String clientSecret,
                               @Value("${voot.scopes}") String spaceDelimitedScopes,
                               @Value("${voot.serviceUrl}") String serviceUrl) {
    return new VootClientImpl(accessTokenUri, clientId, clientSecret, spaceDelimitedScopes, serviceUrl);
  }

  @Bean
  @Autowired
  @Profile("dev")
  public VootClient mockVootClient(Environment environment) {
    return new VootClientMock();
  }


  @Bean
  public EmbeddedServletContainerCustomizer containerCustomizer() {
    return new ErrorCustomizer();
  }

  private static class ErrorCustomizer implements EmbeddedServletContainerCustomizer {
    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
      container.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/forbidden"));
    }
  }
}
