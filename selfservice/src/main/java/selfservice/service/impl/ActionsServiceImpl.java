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
package selfservice.service.impl;

import static java.util.stream.Collectors.toList;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import selfservice.domain.Action;
import selfservice.domain.Change;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;
import selfservice.service.ActionsService;
import selfservice.service.EmailService;
import selfservice.serviceregistry.ServiceRegistry;

@Service
public class ActionsServiceImpl implements ActionsService {

  private static final Pattern namePattern = Pattern.compile("^Applicant name: (.*)$", Pattern.MULTILINE);
  private static final Pattern emailPattern = Pattern.compile("^Applicant email: (.*)$", Pattern.MULTILINE);


  @Autowired
  private JiraClient jiraClient;

  @Autowired
  private EmailService emailService;

  @Autowired
  private ServiceRegistry serviceRegistry;

  @Value("${administration.email.enabled}")
  private boolean sendAdministrationEmail;

  @Override
  public List<Action> getActions(String identityProvider) {
    List<Action> tasks = jiraClient.getTasks(identityProvider);

    return tasks.stream().map(this::addNames).map(this::addUser).collect(toList());
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

  private Action addNames(Action action) {
    Optional<ServiceProvider> serviceProvider = serviceRegistry.getServiceProvider(action.getSpId());
    Optional<IdentityProvider> identityProvider = serviceRegistry.getIdentityProvider(action.getIdpId());

    return action.unbuild()
        .idpName(identityProvider.map(IdentityProvider::getName).orElse("Unknown idp"))
        .spName(serviceProvider.map(ServiceProvider::getName).orElse("Unknown sp")).build();
  }

  private void sendAdministrationEmail(Action action) {
    if (!sendAdministrationEmail) {
      return;
    }

    String subject = String.format(
        "[Csa (%s) request] %s connection from IdP '%s' to SP '%s' (Issue : %s)",
        getHost(), action.getType().name(), action.getIdpId(), action.getSpId(), action.getJiraKey().orElse("???"));

    StringBuilder body = new StringBuilder();
    body.append("SP EntityID: " + action.getSpId() + "\n");
    body.append("SP Name: " + action.getSpName() + "\n");

    body.append("IdP EntityID: " + action.getIdpId() + "\n");
    body.append("IdP Name: " + action.getIdpName() + "\n");

    body.append("Request: " + action.getType().name() + "\n");
    body.append("Applicant name: " + action.getUserName() + "\n");
    body.append("Applicant email: " + action.getUserEmail() + " \n");
    body.append("Mail applicant: mailto:" + action.getUserEmail() + "?CC=surfconext-beheer@surfnet.nl&SUBJECT=[" + action.getJiraKey().orElse("???") + "]%20" + action.getType().name() + "%20to%20" + action.getSpName() + "&BODY=Beste%20" + action.getUserName() + " \n");

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:MM");
    body.append("Time: " + sdf.format(new Date()) + "\n");
    body.append("Remark from User:\n");
    body.append(action.getBody());
    emailService.sendMail(action.getUserEmail(), subject.toString(), body.toString());
  }

  private String getHost() {
    try {
      return InetAddress.getLocalHost().toString();
    } catch (UnknownHostException e) {
      return "UNKNOWN";
    }
  }
}
