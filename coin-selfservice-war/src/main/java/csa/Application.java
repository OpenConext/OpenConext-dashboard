package csa;

import java.net.URI;
import java.util.Locale;

import javax.sql.DataSource;

import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import csa.api.cache.ServicesCache;
import csa.dao.LmngIdentifierDao;
import csa.janus.Janus;
import csa.janus.JanusRestClient;
import csa.service.CrmService;
import csa.service.EmailService;
import csa.service.VootClient;
import csa.service.impl.CompoundSPService;
import csa.service.impl.EmailServiceImpl;
import csa.service.impl.JiraClient;
import csa.service.impl.JiraClientImpl;
import csa.service.impl.JiraClientMock;
import csa.service.impl.LmngServiceImpl;
import csa.service.impl.LmngServiceMock;
import csa.service.impl.ServicesServiceImpl;
import csa.service.impl.VootClientImpl;
import csa.service.impl.VootClientMock;
import csa.util.JanusRestClientMock;
import csa.util.LicenseContactPersonService;
import csa.util.mail.Emailer;
import csa.util.mail.EmailerImpl;
import csa.util.mail.MockEmailerImpl;
import net.sf.ehcache.CacheManager;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableCaching
public class Application extends SpringBootServletInitializer {

  public static final String DEV_PROFILE_NAME = "dev";

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
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public EhCacheCacheManager cacheManager(CacheManager ehCacheCacheManager) {
    return new EhCacheCacheManager(ehCacheCacheManager);
  }

  @Bean
  public CacheManager ehCacheCacheManager(@Value("${csa.cache.ehcache.config:classpath:/ehcache.xml}") Resource location) {
    if (location.exists()) {
      return EhCacheManagerUtils.buildCacheManager(location);
    }

    return EhCacheManagerUtils.buildCacheManager();
  }

  @Bean
  public VootClient vootClient(Environment environment,
                               @Value("${voot.accessTokenUri}") String accessTokenUri,
                               @Value("${voot.clientId}") String clientId,
                               @Value("${voot.clientSecret}") String clientSecret,
                               @Value("${voot.scopes}") String scopes,
                               @Value("${voot.serviceUrl}") String serviceUrl) {

    return environment.acceptsProfiles(DEV_PROFILE_NAME) ? new VootClientMock() : new VootClientImpl(accessTokenUri, clientId, clientSecret, scopes, serviceUrl);
  }

  @Bean
  public LocaleResolver localeResolver() {
    CookieLocaleResolver localeResolver = new CookieLocaleResolver();
    localeResolver.setDefaultLocale(new Locale("en"));
    return localeResolver;
  }

  @Bean
  public Janus janus(Environment environment, @Value("${janus.uri}") String uri, @Value("${janus.user}") String user, @Value("${janus.secret}") String secret) throws Exception {
    return environment.acceptsProfiles(DEV_PROFILE_NAME) ? new JanusRestClientMock() : new JanusRestClient(new URI(uri), user, secret);
  }

  @Bean
  public JiraClient jiraClient(Environment environment,
                               @Value("${jiraBaseUrl}") String baseUrl,
                               @Value("${jiraUsername}") String username,
                               @Value("${jiraPassword}") String password,
                               @Value("${jiraProjectKey}") String projectKey) throws Exception {
    return environment.acceptsProfiles(DEV_PROFILE_NAME) ? new JiraClientMock() : new JiraClientImpl(baseUrl, username, password, projectKey);
  }

  @Bean
  public CrmService crmService(Environment environment, LmngIdentifierDao lmngIdentifierDao, @Value("${crmServiceClassEndpoint}") String endpoint) {
    return environment.acceptsProfiles(DEV_PROFILE_NAME) ? new LmngServiceMock() : new LmngServiceImpl(lmngIdentifierDao, endpoint);
  }

  @Bean
  public EmailService emailService(Environment environment, JavaMailSender mailSender,
                                   @Value("${coin-administrative-email}") String administrativeEmail) {
    Emailer emailer = environment.acceptsProfiles(DEV_PROFILE_NAME) ? new MockEmailerImpl() : new EmailerImpl(mailSender);
    return new EmailServiceImpl(administrativeEmail, emailer);
  }

  @Bean
  public Emailer emailer(Environment environment, JavaMailSender mailSender) {
    return environment.acceptsProfiles(DEV_PROFILE_NAME) ? new MockEmailerImpl() : new EmailerImpl(mailSender);
  }

  @Bean
  public ServicesCache servicesCache(CompoundSPService compoundSPService, CrmService crmService,
                                     @Value("${cache.default.initialDelay}") long initialDelay,
                                     @Value("${cache.default.delay}") long delay,
                                     @Value("${cacheMillisecondsCallDelay}") long callDelay,
                                     @Value("${static.baseurl}") String staticBaseUrl,
                                     @Value("${lmngDeepLinkBaseUrl}") String lmngDeepLinkBaseUrl,
                                     @Value("${public.api.lmng.guids}") String[] guids) {
    return new ServicesCache(new ServicesServiceImpl(compoundSPService, crmService, staticBaseUrl, lmngDeepLinkBaseUrl, guids), initialDelay, delay, callDelay);
  }

  @Bean
  public LicenseContactPersonService licenseContactPersonService(@Value("${licenseContactPerson.config.path}") final String contentFileLocation) {
    return new LicenseContactPersonService(resourceLoader.getResource(contentFileLocation));
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

  @Bean
  public InternalResourceViewResolver viewResolver(@Value("${spring.view.prefix}") String prefix, @Value("${spring.view.suffix}") String suffix) {
    InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
    internalResourceViewResolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
    internalResourceViewResolver.setPrefix(prefix);
    internalResourceViewResolver.setSuffix(suffix);
    return internalResourceViewResolver;
  }
}
