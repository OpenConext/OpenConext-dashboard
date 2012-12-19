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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

/**
 * Group20 has no members and we need both in the recommend app context
 * 
 */
@SuppressWarnings("serial")
public class GroupContext implements Serializable{

  private List<Group20Wrap> wraps = new ArrayList<Group20Wrap>();

  public void addGroup(Group20 group, List<Person> members) {
    List<PersonWrap> children = new ArrayList<GroupContext.PersonWrap>();
    for (Person p : members) {
      children.add(new PersonWrap(p.getEmailValue(), p.getDisplayName(), false));
    }
    wraps.add(new Group20Wrap(group.getTitle(), true, children));
  }
  
  public List<Group20Wrap>  getEntries() {
    return wraps;
  }

  public class Group20Wrap implements Serializable{
    public final String text;
    public final boolean group;
    public List<PersonWrap> children;
    public Group20Wrap(String text, boolean group, List<PersonWrap> children) {
      super();
      this.text = text;
      this.group = group;
      this.children = children;
    }

  }
  
  public class PersonWrap implements Serializable {
    public final String id;
    public final String text;
    public final boolean group;

    public PersonWrap(String id, String text, boolean group) {
      super();
      this.id = id;
      this.text = text;
      this.group = group;
    }
  }
  
}
