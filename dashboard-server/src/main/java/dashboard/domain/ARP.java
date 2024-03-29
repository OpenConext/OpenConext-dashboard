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
package dashboard.domain;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Attribute Release Policy
 */
public class ARP implements Serializable {

    private static final long serialVersionUID = 0L;

    private String name;
    private String description;
    private Map<String, List<String>> attributes = new LinkedHashMap<>();
    private boolean noArp;
    private boolean noAttrArp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public boolean isNoArp() {
        return noArp;
    }

    public boolean isNoAttrArp() {
        return noAttrArp;
    }

    public void setNoArp(boolean noArp) {
        this.noArp = noArp;
    }

    public void setNoAttrArp(boolean noAttrArp) {
        this.noAttrArp = noAttrArp;
    }

    @Override
    public String toString() {
        return "ARP{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", attributes=" + attributes +
                ", noArp=" + noArp +
                ", noAttrArp=" + noAttrArp +
                '}';
    }

    @SuppressWarnings("unchecked")
    public static ARP noArp() {
        ARP arp = new ARP();

        arp.setNoArp(true);
        arp.setNoAttrArp(false);
        return arp;
    }

    public static ARP fromAttributes(Map<String, List<String>> attributes) {
        ARP arp = new ARP();
        arp.setName("arp");
        arp.setDescription("arp");
        arp.setAttributes(attributes);
        if (CollectionUtils.isEmpty(attributes)) {
            arp.setNoAttrArp(true);
        }
        return arp;
    }


}
