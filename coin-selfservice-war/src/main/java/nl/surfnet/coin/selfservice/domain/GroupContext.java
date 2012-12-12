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
package nl.surfnet.coin.selfservice.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Group20 has no members and we need both in the recommend app context
 * 
 */
public class GroupContext {

  private Map<Group20, List<Person>> data = new HashMap<Group20, List<Person>>();

  public void addGroup(Group20 group, List<Person> members) {
    data.put(group, members);
  }
  
  public List<Entry> getEntries() {
    List<Entry> entries = new ArrayList<GroupContext.Entry>();
    for (Group20 group : data.keySet()) {
      entries.add(new Entry(group.getId(), group.getTitle(), true));
      List<Person> members = data.get(group);
      for (Person person : members) {
        entries.add(new Entry(person.getEmailValue(), person.getDisplayName(), false));
      }
    }
    return entries;
  }

  public class Entry {
    public final String id;
    public final String text;
    public final boolean group;

    public Entry(String id, String text, boolean group) {
      super();
      this.id = id;
      this.text = text;
      this.group = group;
    }
  }
  
}
