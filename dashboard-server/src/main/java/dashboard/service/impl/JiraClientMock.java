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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import dashboard.domain.Action;
import dashboard.domain.JiraFilter;
import dashboard.domain.JiraResponse;
import dashboard.mail.MailBox;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class JiraClientMock implements JiraClient {

    private final MailBox mailBox;
    private final ObjectMapper objectMapper;

    public JiraClientMock(MailBox mailBox, ObjectMapper objectMapper) {
        this.mailBox = mailBox;
        this.objectMapper = objectMapper;
    }


    @SneakyThrows
    @Override
    public String create(final Action action) {
        String key = String.valueOf(Math.round(Math.random() * 10000));
        String subject = String.format(
                "[Services (%s) request] %s connection from IdP '%s' to SP '%s' (Issue : %s)",
                getHost(), action.getType().name(), action.getIdpId(), action.getSpId(), key);
        String body = "A JIRA action / task is created. These are the details:\n";
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(action);
        mailBox.sendAdministrativeMail(body + json, subject);

        return key;
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            return "UNKNOWN";
        }
    }

    @Override
    public JiraResponse searchTasks(String idp, JiraFilter jiraFilter) {
        return new JiraResponse(Collections.emptyList(), 0, jiraFilter.getStartAt(), jiraFilter.getMaxResults());
    }

    @Override
    public Map<String, String> validTransitions(String key) {
        return ImmutableMap.of("To Do", "1", "In Progress", "2", "Awaiting Input", "3", "Resolved", "4", "Closed", "5");
    }

    @Override
    public void comment(String key, String comment) {
        //nope
    }

    @Override
    public void transition(String key, String transitionId, Optional<String> resolution, Optional<String> comment) {
        //nope
    }



    @Override
    public void updateOptionalMessage(String jiraKey, String optionalMessage) {
        //nope
    }
}
