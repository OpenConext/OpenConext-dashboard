/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dashboard.service.impl;

import dashboard.domain.*;
import dashboard.mail.MailBox;
import dashboard.manage.EntityType;
import dashboard.manage.Manage;
import dashboard.service.ActionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ActionsServiceImpl implements ActionsService {

    private static final Pattern namePattern = Pattern.compile("^Applicant name: (.*)$", Pattern.MULTILINE);
    private static final Pattern emailPattern = Pattern.compile("^Applicant email: (.*)$", Pattern.MULTILINE);

    @Autowired
    private JiraClient jiraClient;

    @Autowired
    private Manage manage;

    @Autowired
    private MailBox mailBox;

    @Value("${administration.email.enabled}")
    private boolean sendAdministrationEmail;

    @Override
    @SuppressWarnings("unchecked")
    public JiraResponse searchTasks(String idp, JiraFilter jiraFilter) {
        JiraResponse jiraResponse = jiraClient.searchTasks(idp, jiraFilter);
        List<Action> issues = jiraResponse.getIssues();

        Map<String, String> serviceProviders = issues.stream()
                .map(Action::getSpId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet())
                .stream()
                .map(spId -> manage.getServiceProvider(spId, EntityType.saml20_sp, true))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Provider::getId, Provider::getName));

        Map<String, String> identityProviders = issues.stream()
                .map(Action::getIdpId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet())
                .stream()
                .map(idpId -> manage.getIdentityProvider(idpId, true))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Provider::getId, Provider::getName));

        List<Action> enrichedActions = issues.stream()
                .map(this::addUser)
                .map(action -> action.unbuild()
                        .spName(serviceProviders.getOrDefault(action.getSpId(), "Information unavailable"))
                        .idpName(identityProviders.getOrDefault(action.getIdpId(), "Information unavailable"))
                        .build())
                .collect(toList());
        jiraResponse.setIssues(enrichedActions);
        return jiraResponse;
    }

    private Action addNames(Action action) {
        Optional<ServiceProvider> serviceProvider = manage.getServiceProvider(action.getSpId(), EntityType.saml20_sp, true);
        Optional<IdentityProvider> identityProvider = manage.getIdentityProvider(action.getIdpId(), true);

        return action.unbuild()
                .idpName(identityProvider.map(IdentityProvider::getName).orElse("Information unavailable"))
                .spName(serviceProvider.map(ServiceProvider::getName).orElse("Information unavailable")).build();
    }

    private Action addUser(Action action) {
        String body = action.getBody();

        Optional<String> userEmail = findUserEmail(body);
        Optional<String> userName = findUserName(body);

        return action.unbuild()
                .userEmail(userEmail.orElse("unknown"))
                .userName(userName.orElse("unknown")).build();
    }

    private Optional<String> findUserEmail(String body) {
        return matchingGroup(emailPattern, body);
    }

    private Optional<String> findUserName(String body) {
        return matchingGroup(namePattern, body);
    }

    private Optional<String> matchingGroup(Pattern pattern, String input) {
        if (StringUtils.isEmpty(input)) {
            return Optional.empty();
        }
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Optional.ofNullable(matcher.group(1));
        }

        return Optional.empty();
    }

    @Override
    public Action create(Action action, List<Change> changes) {
        String jiraKey = jiraClient.create(action, changes);
        Action savedAction = addNames(action).unbuild().jiraKey(jiraKey).build();

        sendAdministrationEmail(savedAction);

        return savedAction;
    }

    private void sendAdministrationEmail(Action action) {
        if (!sendAdministrationEmail) {
            return;
        }
        String subject = String.format(
                "[Services (%s) request] %s connection from IdP '%s' to SP '%s' (Issue : %s)",
                getHost(), action.getType().name(), action.getIdpId(), action.getSpId(), action.getJiraKey().orElse("???"));
        try {
            mailBox.sendAdministrativeMail(action.toString(), subject);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            return "UNKNOWN";
        }
    }
}
