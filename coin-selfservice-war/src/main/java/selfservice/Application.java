package selfservice;

import java.net.URI;
import java.util.Locale;

import javax.sql.DataSource;

import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
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
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import selfservice.dao.LmngIdentifierDao;
import selfservice.janus.Janus;
import selfservice.janus.JanusRestClient;
import selfservice.sab.HttpClientTransport;
import selfservice.sab.Sab;
import selfservice.sab.SabClient;
import selfservice.sab.SabClientMock;
import selfservice.service.CrmService;
import selfservice.service.Csa;
import selfservice.service.VootClient;
import selfservice.service.impl.CsaImpl;
import selfservice.service.impl.JiraClient;
import selfservice.service.impl.JiraClientImpl;
import selfservice.service.impl.JiraClientMock;
import selfservice.service.impl.LmngServiceImpl;
import selfservice.service.impl.LmngServiceMock;
import selfservice.service.impl.VootClientImpl;
import selfservice.service.impl.VootClientMock;
import selfservice.util.CookieThenAcceptHeaderLocaleResolver;
import selfservice.util.JanusRestClientMock;
import selfservice.util.LicenseContactPersonService;

@SpringBootApplication
@EnableJpaRepositories("selfservice.dao")
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, FreeMarkerAutoConfiguration.class, TraceWebFilterAutoConfiguration.class, TraceRepositoryAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {

  @Autowired
  private ResourceLoader resourceLoader;

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Application.class);
  }

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public LocaleResolver localeResolver() {
    CookieThenAcceptHeaderLocaleResolver localeResolver = new CookieThenAcceptHeaderLocaleResolver();
    localeResolver.setCookieName("dashboardLang");
    localeResolver.setDefaultLocale(new Locale("nl"));
    localeResolver.setCookieMaxAge(315360000);
    return localeResolver;
  }

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public Csa csaClient() {
    return new CsaImpl();
  }

  @Bean
  @Profile("!dev")
  public Sab sab(HttpClientTransport httpClientTransport) {
    return new SabClient(httpClientTransport);
  }

  @Bean
  @Profile("dev")
  public Sab sabMock() {
    return new SabClientMock();
  }

  @Bean
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
  @Profile("dev")
  public VootClient mockVootClient(Environment environment) {
    return new VootClientMock();
  }

  @Bean
  public EmbeddedServletContainerCustomizer containerCustomizer() {
    return new ErrorCustomizer();
  }

  @Bean
  @Profile("!dev")
  public Janus janus(@Value("${janus.uri}") String uri, @Value("${janus.user}") String user, @Value("${janus.secret}") String secret) throws Exception {
    return new JanusRestClient(new URI(uri), user, secret);
  }

  @Bean
  @Profile("dev")
  public Janus mockJanus() {
    return new JanusRestClientMock();
  }

  @Bean
  @Profile("!dev")
  public JiraClient jiraClient(@Value("${jiraBaseUrl}") String baseUrl,
                               @Value("${jiraUsername}") String username,
                               @Value("${jiraPassword}") String password,
                               @Value("${jiraProjectKey}") String projectKey) {
    return new JiraClientImpl(baseUrl, username, password, projectKey);
  }

  @Bean
  @Profile("dev")
  public JiraClient mockJiraClient() {
    return new JiraClientMock();
  }

  @Bean
  @Profile("!dev")
  public CrmService crmService(LmngIdentifierDao lmngIdentifierDao, @Value("${crmServiceClassEndpoint}") String endpoint) {
    return new LmngServiceImpl(lmngIdentifierDao, endpoint);
  }

  @Bean
  @Profile("dev")
  public CrmService mockcrmService() {
    return new LmngServiceMock();
  }

  @Bean
  public LicenseContactPersonService licenseContactPersonService(@Value("${licenseContactPerson.config.path}") final String contentFileLocation) {
    return new LicenseContactPersonService(resourceLoader.getResource(contentFileLocation));
  }

  @Bean
  public InternalResourceViewResolver viewResolver(@Value("${spring.mvc.view.prefix}") String prefix, @Value("${spring.mvc.view.suffix}") String suffix) {
    InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
    internalResourceViewResolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
    internalResourceViewResolver.setPrefix(prefix);
    internalResourceViewResolver.setSuffix(suffix);
    return internalResourceViewResolver;
  }

  /**
   * Required because of https://github.com/spring-projects/spring-boot/issues/2825
   * As the issue says, probably can be removed as of Spring-Boot 1.3.0
   */
  private static class ErrorCustomizer implements EmbeddedServletContainerCustomizer {
      @Override
      public void customize(ConfigurableEmbeddedServletContainer container) {
        container.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/forbidden"));

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
  }
}
