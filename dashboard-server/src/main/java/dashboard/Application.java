package dashboard;

import dashboard.shibboleth.mock.MockShibbolethFilter;
import org.springframework.beans.factory.annotation.Autowired;
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
import dashboard.manage.ClassPathResourceManage;
import dashboard.manage.Manage;
import dashboard.manage.UrlResourceManage;
import dashboard.pdp.PdpService;
import dashboard.pdp.PdpServiceImpl;
import dashboard.pdp.PdpServiceMock;
import dashboard.sab.HttpClientTransport;
import dashboard.sab.Sab;
import dashboard.sab.SabClient;
import dashboard.sab.SabClientMock;
import dashboard.service.Services;
import dashboard.service.impl.JiraClient;
import dashboard.service.impl.JiraClientImpl;
import dashboard.service.impl.JiraClientMock;
import dashboard.service.impl.ServicesImpl;
import dashboard.stats.Stats;
import dashboard.stats.StatsImpl;
import dashboard.stats.StatsMock;
import dashboard.util.CookieThenAcceptHeaderLocaleResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, FreeMarkerAutoConfiguration.class,
    TraceWebFilterAutoConfiguration.class, TraceRepositoryAutoConfiguration.class,
    MetricFilterAutoConfiguration.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Services services(Manage manage, @Value("${guestidp.entityids}") String guestIdps) {
        return new ServicesImpl(manage, Arrays.stream(guestIdps.split(",")).map(String::trim).collect(Collectors.toList()) );
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
    public JiraClient jiraClient(@Value("${dashboard.feature.jira}") boolean jiraEnabled,
                                 @Value("${jiraBaseUrl}") String baseUrl,
                                 @Value("${jiraUsername}") String username,
                                 @Value("${jiraPassword}") String password,
                                 @Value("${jiraProjectKey}") String projectKey,
                                 @Value("${jiraDueDateWeeks}") int dueDateWeeks) throws IOException {
        return jiraEnabled ? new JiraClientImpl(baseUrl, username, password, projectKey, dueDateWeeks) :
                new JiraClientMock(MockShibbolethFilter.idp);
    }

    @Bean
    public PdpService pdpService(@Value("${dashboard.feature.pdp}") boolean pdpEnabled,
                                 @Value("${pdp.server}") String server,
                                 @Value("${pdp.username}") String username,
                                 @Value("${pdp.password}") String password) {
        return pdpEnabled ? new PdpServiceImpl(server, username, password) : new PdpServiceMock();
    }

}
