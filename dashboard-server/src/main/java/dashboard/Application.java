package dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import dashboard.manage.ClassPathResourceManage;
import dashboard.manage.Manage;
import dashboard.manage.UrlResourceManage;
import dashboard.pdp.*;
import dashboard.sab.HttpClientTransport;
import dashboard.sab.Sab;
import dashboard.sab.SabClient;
import dashboard.sab.SabClientMock;
import dashboard.service.Services;
import dashboard.service.impl.*;
import dashboard.shibboleth.mock.MockShibbolethFilter;
import dashboard.stats.Stats;
import dashboard.stats.StatsImpl;
import dashboard.stats.StatsMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, FreeMarkerAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Services services(Manage manage, @Value("${guestidp.entityids}") String guestIdps) {
        return new ServicesImpl(manage, Arrays.stream(guestIdps.split(",")).map(String::trim).collect(Collectors.toList()));
    }

    @Bean
    public Sab sab(HttpClientTransport httpClientTransport,
                   @Value("${dashboard.feature.sab}") boolean sabEnabled) {
        return sabEnabled ? new SabClient(httpClientTransport) : new SabClientMock();
    }

    @Bean
    public Manage manage(@Value("${dashboard.feature.manage}") boolean manageEnabled,
                         @Value("${manage.username}") String username,
                         @Value("${manage.password}") String password,
                         @Value("${manage.manageBaseUrl}") String manageBaseUrl) {
        return manageEnabled ? new UrlResourceManage(username, password, manageBaseUrl) : new ClassPathResourceManage();
    }

    @Bean
    @Autowired
    public Stats stats(@Value("${dashboard.feature.statistics}") boolean statsEnabled,
                       @Value("${statsUser}") String user,
                       @Value("${statsPassword}") String password,
                       @Value("${statsBaseUrl}") String baseUrl,
                       Manage manage) {
        return statsEnabled ? new StatsImpl(user, password, baseUrl) : new StatsMock(manage);
    }

    @Bean
    public JiraClient jiraClient(ObjectMapper objectMapper,
                                 @Value("${dashboard.feature.jira}") boolean jiraEnabled,
                                 @Value("${jiraBaseUrl}") String baseUrl,
                                 @Value("${jiraUsername}") String username,
                                 @Value("${jiraPassword}") String password,
                                 @Value("${jiraProjectKey}") String projectKey,
                                 @Value("${jiraApikey}") String jiraApikey,
                                 @Value("${jiraUseApiKey}") boolean jiraUseApiKey,
                                 @Value("${jiraEnvironment}") Environment environment,
                                 @Value("${jiraDueDateWeeks}") int dueDateWeeks) throws IOException {
        return jiraEnabled ? new JiraClientImpl(
                objectMapper,
                baseUrl,
                username,
                password,
                jiraApikey,
                jiraUseApiKey,
                projectKey,
                dueDateWeeks,
                environment) : new JiraClientMock(MockShibbolethFilter.idp);
    }

    @Bean
    public PdpService pdpService(ObjectMapper objectMapper,
                                 @Value("${dashboard.feature.pdp}") PolicyDataSource policyDataSource,
                                 @Value("${pdp.server}") String pdpBaseUrl,
                                 @Value("${pdp.username}") String pdpUsername,
                                 @Value("${pdp.password}") String pdpPassword,
                                 @Value("${manage.manageBaseUrl}") String manageBaseUrl,
                                 @Value("${manage.username}") String manageUsername,
                                 @Value("${manage.password}") String managePassword) {
        switch (policyDataSource) {
            case PDP:
                return new PdpServiceImpl(objectMapper, pdpBaseUrl, pdpUsername, pdpPassword);
            case MOCK:
                return new PdpServiceMock();
            case MANAGE:
                return new PdpManage(objectMapper, manageBaseUrl, manageUsername, managePassword);
        }
        throw new IllegalArgumentException();
    }

}
