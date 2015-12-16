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
package selfservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Proxy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import selfservice.util.DomainObject;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@SuppressWarnings("serial")
@Entity
@Proxy(lazy = false)
public class MultilingualString extends DomainObject {

  @Transient
  @JsonIgnore
  public static final Locale defaultLocale = Locale.ENGLISH;

  /*
   * Add more locale abbreviations here to have more languages in the GUI to edit / add. The default 'en' should
   * no be added as this is always included being the default.
   */
  @Transient
  @JsonIgnore
  private static final List<String> availableLocales = Arrays.asList(new String[]{"nl"});

  @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "multilingualString")
  @MapKey(name = "locale")
  private Map<String, LocalizedString> localizedStrings = new HashMap<>();

  public Map<String, LocalizedString> getLocalizedStrings() {
    return localizedStrings;
  }

  @JsonIgnore
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
    if (localizedStrings.get(locale.toString()) == null) {
      this.localizedStrings.put(locale.toString(), new LocalizedString(locale.toString(), value, this));
    } else {
      localizedStrings.get(locale.toString()).setValue(value);
    }
  }


  public void setValue(String value) {
    this.addValue(defaultLocale, value);
  }

  public String getValue() {
    String locale = getLocale().toString();
    return localeString(locale);
  }

  public String getLocaleValue(String locale) {
    return localeString(locale);
  }

  private String localeString(String locale) {
    LocalizedString localizedString = this.localizedStrings.get(locale);
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
