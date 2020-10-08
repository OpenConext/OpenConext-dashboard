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

import dashboard.domain.Action;
import dashboard.domain.Change;
import dashboard.domain.ContactPerson;
import dashboard.domain.ContactPersonType;
import dashboard.domain.IdentityProvider;
import dashboard.domain.JiraFilter;
import dashboard.domain.JiraResponse;
import dashboard.domain.Provider;
import dashboard.domain.ServiceProvider;
import dashboard.mail.MailBox;
import dashboard.manage.EntityType;
import dashboard.manage.Manage;
import dashboard.sab.Sab;
import dashboard.sab.SabPerson;
import dashboard.service.ActionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ActionsServiceImpl implements ActionsService {
    private static final Logger LOG = LoggerFactory.getLogger(ActionsServiceImpl.class);

    private static final Pattern namePattern = Pattern.compile("^Applicant name: (.*)$", Pattern.MULTILINE);
    private static final Pattern emailPattern = Pattern.compile("^Applicant email: (.*)$", Pattern.MULTILINE);

    @Autowired
    private JiraClient jiraClient;

    @Autowired
    private Manage manage;

    @Autowired
    private MailBox mailBox;

    @Autowired
    private Sab sabClient;

    @Value("${administration.email.enabled}")
    private boolean sendAdministrationEmail;

    @Override
    @SuppressWarnings("unchecked")
    public JiraResponse searchTasks(String idp, JiraFilter jiraFilter) {
        JiraResponse jiraResponse = jiraClient.searchTasks(idp, jiraFilter);
        List<Action> issues = jiraResponse.getIssues();

        Map<String, ServiceProvider> serviceProviders = serviceProviders(issues, EntityType.saml20_sp);

        Map<String, ServiceProvider> relyingParties = serviceProviders(issues, EntityType.oidc10_rp);

        Map<String, ServiceProvider> singleTenants = serviceProviders(issues, EntityType.single_tenant_template);

        serviceProviders.putAll(relyingParties);
        serviceProviders.putAll(singleTenants);

        Map<String, IdentityProvider> identityProviders = issues.stream()
                .map(Action::getIdpId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet())
                .stream()
                .map(idpId -> manage.getIdentityProvider(idpId, true))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Provider::getId, Function.identity()));

        List<Action> enrichedActions = issues.stream()
                .map(this::addUser)
                .map(action -> action.unbuild()
                        .spName(providerName(serviceProviders.get(action.getSpId())))
                        .spEid(providerEid(serviceProviders.get(action.getSpId())))
                        .idpName(providerName(identityProviders.get(action.getIdpId())))
                        .build())
                .collect(toList());
        jiraResponse.setIssues(enrichedActions);
        return jiraResponse;
    }

    private Map<String, ServiceProvider> serviceProviders(List<Action> issues, EntityType entityType) {
        Set<String> entityIds = issues.stream()
                .filter(action -> StringUtils.isEmpty(action.getTypeMetaData()) || action.getTypeMetaData().equals(entityType.name()))
                .map(Action::getSpId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        return entityIds
                .stream()
                .map(spId -> manage.getServiceProvider(spId, entityType, true))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Provider::getId, Function.identity()));
    }

    private String providerName(Provider provider) {
        return provider == null ? "Information unavailable" : provider.getName();
    }

    private Long providerEid(Provider provider) {
        return provider == null ? null : provider.getEid();
    }

    private Action addNames(Action action) {
        String typeMetaData = action.getTypeMetaData();
        EntityType entityType = StringUtils.hasText(typeMetaData) ? EntityType.valueOf(typeMetaData) : EntityType.saml20_sp;
        Optional<ServiceProvider> serviceProvider = manage.getServiceProvider(action.getSpId(), entityType, true);
        Optional<IdentityProvider> identityProvider = manage.getIdentityProvider(action.getIdpId(), true);

        return action
                .unbuild()
                .idpName(identityProvider.map(IdentityProvider::getName).orElse("Information unavailable"))
                .spName(serviceProvider.map(ServiceProvider::getName).orElse("Information unavailable"))
                .build();
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

        if (action.getType().equals(Action.Type.LINKINVITE)) {
            String transitionId = jiraClient.validTransitions(jiraKey).get(JiraClient.START_PROGRESS);
            jiraClient.transition(jiraKey, transitionId, Optional.empty(), Optional.empty());

            transitionId = jiraClient.validTransitions(jiraKey).get(JiraClient.INPUT_NEEDED);
            jiraClient.transition(jiraKey, transitionId, Optional.empty(), Optional.of("Waiting for approval of SCV."));

        }
        return savedAction;
    }

    @Override
    public Action connectWithoutInteraction(Action action) {
        Action savedAction = addNames(action);

        String resp = manage.connectWithoutInteraction(savedAction.getIdpId(), savedAction.getSpId(), savedAction.getTypeMetaData());

        savedAction = savedAction.unbuild().rejected(!resp.equals("success")).build();
        if (!savedAction.isRejected()) {
            List<String> idpEmails = sabClient.getSabEmailsForOrganization(action.getIdpId(), "SURFconextverantwoordelijke")
                    .stream().map(SabPerson::getEmail).collect(toList());
            LOG.info("Sending emails 'automatic connection made' to IdP contact persons {}", idpEmails);
            if (!CollectionUtils.isEmpty(idpEmails)) {
                mailBox.sendDashboardConnectWithoutInteractionEmail(idpEmails, savedAction.getIdpName(), savedAction.getSpName(), "idp", action.getBody(), action.getEmailContactPerson());
            }
            Optional<ServiceProvider> serviceProvider = manage.getServiceProvider(action.getSpId(), EntityType.valueOf(action.getTypeMetaData()), true);
            List<String> spEmails = spEmails(serviceProvider);
            LOG.info("{} emails 'automatic connection made' to SP contact persons {}", action.shouldSendEmail() ? "Sending " : "Not sending ", spEmails);
            if (!CollectionUtils.isEmpty(spEmails) && action.shouldSendEmail()) {
                mailBox.sendDashboardConnectWithoutInteractionEmail(spEmails, savedAction.getIdpName(), savedAction.getSpName(), "sp", action.getBody(), action.getEmailContactPerson());
            }
        }

        return savedAction;
    }

    protected List<String> spEmails(Optional<ServiceProvider> serviceProvider) {
        return serviceProvider.map(sp -> {
            List<ContactPerson> contactPersons = sp.getContactPersons();
            List<ContactPersonType> contactTypes = Arrays.asList(
                    ContactPersonType.administrative, ContactPersonType.support, ContactPersonType.technical);
            // can't return from each
            for (ContactPersonType contactPersonType : contactTypes) {
                if (contactPersons.stream().anyMatch(contactPerson -> contactPersonType.equals(contactPerson.getContactPersonType()))) {
                    return contactPersons.stream()
                            .filter(cp -> contactPersonType.equals(cp.getContactPersonType()))
                            .map(ContactPerson::getEmailAddress).
                                    collect(toList());
                }
            }
            return null;
        }).filter(l -> l.stream().filter(Objects::nonNull).count() != 0).orElse(new ArrayList<>());
    }

    @Override
    public void rejectInviteRequest(String jiraKey, String comment) {
        //By request of SURF we don't close the ticket as the feedback might be lost
        approveInviteRequest(jiraKey, comment, false);
    }

    @Override
    public void approveInviteRequest(String jiraKey, String comment, boolean transitionToResolved) {
        Map<String, String> validTransitions = jiraClient.validTransitions(jiraKey);
        String transitionId = transitionToResolved ? validTransitions.get(JiraClient.RESOLVED) : validTransitions.get(JiraClient.ANSWER_AUTOMATICALLY);
        jiraClient.transition(jiraKey, transitionId, Optional.empty(), Optional.empty());
        // There is no comment option in the Answer Automatically screen, so we need to do this after the transition
        jiraClient.comment(jiraKey, comment);
        if (transitionToResolved) {
            validTransitions = jiraClient.validTransitions(jiraKey);
            transitionId = validTransitions.get(JiraClient.RESOLVED);
            jiraClient.transition(jiraKey, transitionId, Optional.empty(), Optional.empty());
        }
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
