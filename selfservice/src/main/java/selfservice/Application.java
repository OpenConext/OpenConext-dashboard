package selfservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.TraceRepositoryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.TraceWebFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import selfservice.manage.ClassPathResourceManage;
import selfservice.manage.Manage;
import selfservice.manage.UrlResourceManage;
import selfservice.pdp.PdpService;
import selfservice.pdp.PdpServiceImpl;
import selfservice.pdp.PdpServiceMock;
import selfservice.sab.HttpClientTransport;
import selfservice.sab.Sab;
import selfservice.sab.SabClient;
import selfservice.sab.SabClientMock;
import selfservice.service.Services;
import selfservice.service.VootClient;
import selfservice.service.impl.JiraClient;
import selfservice.service.impl.JiraClientImpl;
import selfservice.service.impl.JiraClientMock;
import selfservice.service.impl.ServicesImpl;
import selfservice.service.impl.VootClientImpl;
import selfservice.service.impl.VootClientMock;
import selfservice.util.CookieThenAcceptHeaderLocaleResolver;

import java.util.Locale;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, FreeMarkerAutoConfiguration.class,
    TraceWebFilterAutoConfiguration.class, TraceRepositoryAutoConfiguration.class,
    MetricFilterAutoConfiguration.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Services services() {
        return new ServicesImpl();
    }

    @Bean
    public Sab sab(HttpClientTransport httpClientTransport,
                   @Value("${dashboard.feature.sab}") boolean sabEnabled) {
        return sabEnabled ? new SabClient(httpClientTransport) : new SabClientMock();
    }

    @Bean
    public VootClient vootClient(@Value("${dashboard.feature.voot}") boolean vootEnabled,
                                 @Value("${voot.accessTokenUri}") String accessTokenUri,
                                 @Value("${voot.clientId}") String clientId,
                                 @Value("${voot.clientSecret}") String clientSecret,
                                 @Value("${voot.scopes}") String spaceDelimitedScopes,
                                 @Value("${voot.serviceUrl}") String serviceUrl) {
        return vootEnabled ? new VootClientImpl(accessTokenUri, clientId, clientSecret, spaceDelimitedScopes,
            serviceUrl) : new VootClientMock();
    }

    @Bean
    public Manage urlResourceServiceRegistry(@Value("${dashboard.feature.manage}") boolean manageEnabled,
                                             @Value("${manage.username}") String username,
                                             @Value("${manage.password}") String password,
                                             @Value("${manage.manageBaseUrl}") String manageBaseUrl) {
        return manageEnabled ? new UrlResourceManage(username, password, manageBaseUrl) : new ClassPathResourceManage();
    }

    @Bean
    public JiraClient jiraClient(@Value("${dashboard.feature.jira}") boolean jiraEnabled,
                                 @Value("${jiraBaseUrl}") String baseUrl,
                                 @Value("${jiraUsername}") String username,
                                 @Value("${jiraPassword}") String password,
                                 @Value("${jiraProjectKey}") String projectKey) {
        return jiraEnabled ? new JiraClientImpl(baseUrl, username, password, projectKey) :new JiraClientMock();
    }

    @Bean
    public PdpService pdpService(@Value("${dashboard.feature.manage}") boolean pdpEnabled,
                                 @Value("${pdp.server}") String server,
                                 @Value("${pdp.username}") String username,
                                 @Value("${pdp.password}") String password) {
        return pdpEnabled ? new PdpServiceImpl(server, username, password) : new PdpServiceMock();
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieThenAcceptHeaderLocaleResolver localeResolver = new CookieThenAcceptHeaderLocaleResolver();
        localeResolver.setCookieName("dashboardLang");
        localeResolver.setDefaultLocale(new Locale("nl"));
        localeResolver.setCookieMaxAge(315360000);
        return localeResolver;
    }

}
