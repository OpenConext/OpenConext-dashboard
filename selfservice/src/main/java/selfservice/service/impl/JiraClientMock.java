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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selfservice.domain.Action;

public class JiraClientMock implements JiraClient {

  private static final Logger LOG = LoggerFactory.getLogger(JiraClientMock.class);

  private Map<String, Action> repository = new HashMap<>();

  private AtomicInteger counter = new AtomicInteger(0);

  @Override
  public String create(final Action action) {
    String key = generateKey();

    repository.put(key, action.unbuild().jiraKey(key).body(action.getBody() == null ? "" : action.getBody()).build());

    LOG.debug("Added task (key '{}') to repository: {}", key, action);

    return key;
  }

  private String generateKey() {
    return "TASK-" + counter.incrementAndGet();
  }

  @Override
  public List<Action> getTasks(String idp) {
    return repository.values().stream().filter(action -> action.getIdpId().equals(idp)).collect(toList());
  }

}
