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
import java.util.List;

/**
 * Menu
 */
public class Menu {

  private List<MenuItem> menuItems = new ArrayList<MenuItem>();

  public List<MenuItem> getMenuItems() {
    return menuItems;
  }

  public void addMenuItem(MenuItem menuItem) {
    //clumsy, but we want to preserve the order and not have duplicates
    if (!menuItems.contains(menuItem)) {
      menuItems.add(menuItem);
    }
  }
}
