/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.selfservice.domain;

import nl.surfnet.coin.shared.domain.DomainObject;
import org.hibernate.annotations.Proxy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@SuppressWarnings("serial")
@Entity
@Proxy(lazy = false)
public class MultilingualString extends DomainObject {

  @Transient
  public static final Locale defaultLocale = Locale.ENGLISH;

  /*
   * Add more locale abbreviations here to have more languages in the GUI to edit / add. The default 'en' should
   * no be added as this is always included being the default.
   */
  @Transient
  private static final List<String> availableLocales = Arrays.asList(new String[]{"nl"});

  @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "multilingualString")
  @MapKey(name = "locale")
  private Map<String, LocalizedString> localizedStrings = new HashMap<String, LocalizedString>();

  public Map<String, LocalizedString> getLocalizedStrings() {
    return localizedStrings;
  }

  public List<LocalizedString> getAllowedLocalizedStrings() {
    List<LocalizedString> result = new ArrayList<LocalizedString>();
    for (String locale : availableLocales) {
      if (localizedStrings.containsKey(locale)) {
        result.add(localizedStrings.get(locale));
      } else {
        result.add(new LocalizedString(locale, "", this));
      }
    }
    return result;
  }

  public void setLocalizedStrings(Map<String, LocalizedString> localizedStrings) {
    this.localizedStrings = localizedStrings;
  }

  public void addValue(Locale locale, String value) {
    String localeString = locale.toString();
    this.localizedStrings.put(localeString, new LocalizedString(localeString, value, this));
  }

  public void setValue(String value) {
    this.addValue(defaultLocale, value);
  }

  public String getValue() {
    Locale locale = getLocale();
    LocalizedString localizedString = this.localizedStrings.get(locale.toString());
    if (localizedString == null) {
      localizedString = this.localizedStrings.get(defaultLocale.toString());
    }
    if (localizedString == null) {
      throw new IllegalArgumentException("No LocalizedString configured for Locale " + locale.toString() + " and not for the default Locale");
    }
    return localizedString.getValue();
  }

  /*
   * Note: this is a deliberate design choice. We want to be able to transparently call getValue without passing in HttpServletRequest and / or Locale.
   *
   */
  private Locale getLocale() {
    Locale locale = null;
    ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (sra != null) {
      HttpServletRequest request = sra.getRequest();
      if (request != null) {
        locale = RequestContextUtils.getLocale(request);
      }
    }
    return locale != null ? locale : defaultLocale;

  }
}
