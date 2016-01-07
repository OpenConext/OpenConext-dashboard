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

package selfservice.domain.csa;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu item
 */
public class MenuItem {

  private String label;
  private String url;
  private boolean selected;
  private List<MenuItem> menuItems = new ArrayList<MenuItem>();

  public MenuItem(String label, String url) {
    this(label, url, false);
  }

  public MenuItem(String label, String url, boolean selected) {
    this.label = label;
    this.url = url;
    this.selected = selected;
  }

  /**
   * @return label of the menu item, can be an i18n key
   */
  public String getLabel() {
    return label;
  }

  /**
   * @return URL the menu item links to
   */
  public String getUrl() {
    return url;
  }

  /**
   * @return flag to define if this is the selected menu item
   */
  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  /**
   * @return List of chile {@link MenuItem}'s
   */
  public List<MenuItem> getMenuItems() {
    return menuItems;
  }

  public void addMenuItem(MenuItem menuItem) {
    this.menuItems.add(menuItem);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((label == null) ? 0 : label.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MenuItem other = (MenuItem) obj;
    if (label == null) {
      if (other.label != null)
        return false;
    } else if (!label.equals(other.label))
      return false;
    return true;
  }

}
