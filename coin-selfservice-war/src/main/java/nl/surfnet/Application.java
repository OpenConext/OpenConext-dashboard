package nl.surfnet;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.CsaClient;
import nl.surfnet.coin.oauth.ClientCredentialsClient;
import nl.surfnet.coin.selfservice.api.rest.GsonHttpMessageConverter;
import nl.surfnet.coin.selfservice.service.VootClient;
import nl.surfnet.coin.selfservice.service.impl.CsaMock;
import nl.surfnet.coin.selfservice.service.impl.VootClientImpl;
import nl.surfnet.coin.selfservice.service.impl.VootClientMock;
import nl.surfnet.coin.selfservice.util.CookieThenAcceptHeaderLocaleResolver;
import nl.surfnet.sab.*;
import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;
import java.util.Locale;


@SpringBootApplication
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, FreeMarkerAutoConfiguration.class})
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
  public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addInterceptors(InterceptorRegistry registry) {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
      }
      @Override
      public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add( new GsonHttpMessageConverter());
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
    @Value("${csa.oauth2.authorization.url}") String oauthAuthorizationUrl) {

    ClientCredentialsClient oauthClient = new ClientCredentialsClient();
    oauthClient.setClientKey(clientKey);
    oauthClient.setClientSecret(clientSecret);
    oauthClient.setOauthAuthorizationUrl(oauthAuthorizationUrl);

    CsaClient csaClient = new CsaClient(csaBaseLocation);
    csaClient.setOauthClient(oauthClient);
    return csaClient;
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


  /**
   * Required because of https://github.com/spring-projects/spring-boot/issues/2825
   * As the issue says, probably can be removed as of Spring-Boot 1.3.0
   */
  @Bean
  public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
    return new EmbeddedServletContainerCustomizer() {

      @Override
      public void customize(ConfigurableEmbeddedServletContainer container) {
        if (container instanceof TomcatEmbeddedServletContainerFactory) {
          customizeTomcat((TomcatEmbeddedServletContainerFactory) container);
        }
      }

      private void customizeTomcat(TomcatEmbeddedServletContainerFactory tomcatFactory) {
        tomcatFactory.addContextCustomizers(context -> {
          Container jsp = context.findChild("jsp");
          if (jsp instanceof Wrapper) {
            ((Wrapper) jsp).addInitParameter("development", "false");
          }
        });
      }
    };
  }

}
