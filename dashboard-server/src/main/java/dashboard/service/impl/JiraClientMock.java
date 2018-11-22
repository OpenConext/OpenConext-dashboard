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

import com.google.common.collect.ImmutableMap;
import dashboard.domain.JiraFilter;
import dashboard.domain.JiraResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dashboard.domain.Action;
import dashboard.domain.Change;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class JiraClientMock implements JiraClient {

    private static final Logger LOG = LoggerFactory.getLogger(JiraClientMock.class);

    private Map<String, Action> repository = new LinkedHashMap<>();

    private AtomicInteger counter = new AtomicInteger(0);

    private List<String> statuses = Arrays.asList("To Do", "In Progress", "Awaiting Input", "Resolved", "Closed");

    public JiraClientMock(String idp) {
        IntStream.rangeClosed(0, 25).forEach(i -> {
            List<Action.Type> types = Arrays.asList(Action.Type.values());
            Action action = Action.builder()
                    .spId("http://sp-" + i)
                    .idpId(idp)
                    .idpName("IDP")
                    .jiraKey(generateKey())
                    .requestDate(ZonedDateTime.now())
                    .status(statuses.get(new Random().nextInt(statuses.size())))
                    .type(types.get(new Random().nextInt(types.size())))
                    .build();
            repository.put(action.getJiraKey().get(), action);
        });

    }

    @Override
    public String create(final Action action, List<Change> changes) {
        String key = generateKey();

        repository.put(key, action.unbuild().jiraKey(key).body(action.getBody() == null ? "" : action.getBody())
                .build());

        LOG.debug("Added task (key '{}') to repository: {}", key, action);

        return key;
    }

    private String generateKey() {
        return "TASK-" + counter.incrementAndGet();
    }

    @Override
    public JiraResponse searchTasks(String idp, JiraFilter jiraFilter) {
        List<Action> actions = repository.values().stream().filter(action -> action.getIdpId().equals(idp))
                .collect(toList());
        return new JiraResponse(actions, actions.size(), jiraFilter.getStartAt(), jiraFilter.getMaxResults());
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
        Action action = repository.get(key);
        if (action != null) {
            Map<String, String> transitions = validTransitions(key);
            Optional<String> newStatusTxt = transitions.entrySet().stream().filter(entry -> entry.getValue().equals(transitionId)).map(entry -> entry.getKey()).findAny();
            newStatusTxt.ifPresent(status -> {
                repository.put(key, action.unbuild().status(status).build());
            });
        }
    }
}
