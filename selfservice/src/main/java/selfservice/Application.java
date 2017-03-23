package selfservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.TraceRepositoryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.TraceWebFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.LocaleResolver;
import selfservice.dao.LmngIdentifierDao;
import selfservice.pdp.PdpService;
import selfservice.pdp.PdpServiceImpl;
import selfservice.pdp.PdpServiceMock;
import selfservice.sab.HttpClientTransport;
import selfservice.sab.Sab;
import selfservice.sab.SabClient;
import selfservice.sab.SabClientMock;
import selfservice.service.CrmService;
import selfservice.service.Csa;
import selfservice.service.VootClient;
import selfservice.service.impl.*;
import selfservice.serviceregistry.ClassPathResourceServiceRegistry;
import selfservice.serviceregistry.ServiceRegistry;
import selfservice.serviceregistry.UrlResourceServiceRegistry;
import selfservice.util.CookieThenAcceptHeaderLocaleResolver;
import selfservice.util.LicenseContactPersonService;

import java.io.IOException;
import java.util.Locale;

@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class, FreeMarkerAutoConfiguration.class,
    TraceWebFilterAutoConfiguration.class, TraceRepositoryAutoConfiguration.class,
    MetricFilterAutoConfiguration.class
 })
@EnableJpaRepositories("selfservice.dao")
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
  @Profile("!dev")
  public ServiceRegistry urlResourceServiceRegistry(
    @Value("${metadata.username}") String username,
    @Value("${metadata.password}") String password,
    @Value("${metadata.idpRemotePath}") String idpRemotePath,
    @Value("${metadata.spRemotePath}") String spRemotePath,
    @Value("${period.metadata.refresh.minutes}") int period,
    @Value("${singleTenants.config.path}") String singleTenantsConfigPath) throws IOException {
    Resource resource = resourceLoader.getResource(singleTenantsConfigPath);

    return new UrlResourceServiceRegistry(username, password, idpRemotePath, spRemotePath, period, resource);
  }

  @Bean
  @Profile("dev")
  public ServiceRegistry classPathServiceRegistry(@Value("${singleTenants.config.path}") String singleTenantsConfigPath) throws Exception {
    Resource resource = resourceLoader.getResource(singleTenantsConfigPath);
    return new ClassPathResourceServiceRegistry(true, resource);
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
  public CrmService mockCrmService() {
    return new LmngServiceMock();
  }

  @Bean
  public LicenseContactPersonService licenseContactPersonService(@Value("${licenseContactPerson.config.path}") final String contentFileLocation) {
    return new LicenseContactPersonService(resourceLoader.getResource(contentFileLocation));
  }

  @Bean
  @Profile("!dev")
  public PdpService pdpService(@Value("${pdp.server}") String server, @Value("${pdp.username}") String username, @Value("${pdp.password}") String password) {
    return new PdpServiceImpl(server, username, password);
  }

  @Bean
  @Profile("dev")
  public PdpService mockPdpService() {
    return new PdpServiceMock();
  }

}
