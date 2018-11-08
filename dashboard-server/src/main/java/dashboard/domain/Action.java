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

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Optional;

import com.google.common.base.MoreObjects;

import org.apache.commons.lang.builder.CompareToBuilder;

public class Action {

    public enum Type {
        LINKREQUEST, UNLINKREQUEST, CHANGE, LINKINVITE;
    }

    private String jiraKey;
    private String userName;
    private String userEmail;
    private String body;

    private String idpId;
    private String spId;
    private String idpName;
    private String spName;

    private ZonedDateTime requestDate;
    private ZonedDateTime updateDate;
    private Type type;
    private String status;

    private String subject;
    private Service service;
    private Settings settings;
    private Consent consent;

    private Action(Builder builder) {
        this.jiraKey = builder.jiraKey;
        this.userName = builder.userName;
        this.userEmail = builder.userEmail;
        this.body = builder.body;
        this.idpId = builder.idpId;
        this.spId = builder.spId;
        this.spName = builder.spName;
        this.idpName = builder.idpName;
        this.requestDate = builder.requestDate;
        this.updateDate = builder.updateDate;
        this.type = builder.type;
        this.status = builder.status;
        this.service = builder.service;
        this.settings = builder.settings;
        this.consent = builder.consent;
    }

    public Optional<String> getJiraKey() {
        return Optional.ofNullable(jiraKey);
    }

    public String getUserName() {
        return userName;
    }

    public String getBody() {
        return body;
    }

    public String getIdpId() {
        return idpId;
    }

    public String getSpId() {
        return spId;
    }

    public String getIdpName() {
        return idpName;
    }

    public ZonedDateTime getRequestDate() {
        return requestDate;
    }

    public ZonedDateTime getUpdateDate() {
        return updateDate;
    }

    public Type getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getSubject() {
        return subject;
    }

    public Service getService() {
        return service;
    }

    public String getSpName() {
        return spName;
    }

    public Settings getSettings() {
        return settings;
    }

    /**
     * get a Comparator that sorts by date ascending: newest first
     */
    public static Comparator<? super Action> sortByDateAsc() {
        return (a1, a2) -> new CompareToBuilder()
                .append(a1.getRequestDate(), a2.getRequestDate())
                .toComparison();
    }

    @Override
    public String toString() {
        return "Action{" +
                "jiraKey='" + jiraKey + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", body='" + body + '\'' +
                ", idpId='" + idpId + '\'' +
                ", spId='" + spId + '\'' +
                ", idpName='" + idpName + '\'' +
                ", spName='" + spName + '\'' +
                ", requestDate=" + requestDate +
                ", updateDate=" + updateDate +
                ", type=" + type +
                ", status='" + status + '\'' +
                ", subject='" + subject + '\'' +
                ", service=" + service +
                ", settings=" + settings +
                ", consent=" + consent +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        result = prime * result + ((idpId == null) ? 0 : idpId.hashCode());
        result = prime * result + ((idpName == null) ? 0 : idpName.hashCode());
        result = prime * result + ((jiraKey == null) ? 0 : jiraKey.hashCode());
        result = prime * result + ((requestDate == null) ? 0 : requestDate.hashCode());
        result = prime * result + ((spId == null) ? 0 : spId.hashCode());
        result = prime * result + ((spName == null) ? 0 : spName.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((userEmail == null) ? 0 : userEmail.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
        Action other = (Action) obj;
        if (body == null) {
            if (other.body != null)
                return false;
        } else if (!body.equals(other.body))
            return false;
        if (idpId == null) {
            if (other.idpId != null)
                return false;
        } else if (!idpId.equals(other.idpId))
            return false;
        if (idpName == null) {
            if (other.idpName != null)
                return false;
        } else if (!idpName.equals(other.idpName))
            return false;
        if (jiraKey == null) {
            if (other.jiraKey != null)
                return false;
        } else if (!jiraKey.equals(other.jiraKey))
            return false;
        if (requestDate == null) {
            if (other.requestDate != null)
                return false;
        } else if (!requestDate.equals(other.requestDate))
            return false;
        if (spId == null) {
            if (other.spId != null)
                return false;
        } else if (!spId.equals(other.spId))
            return false;
        if (spName == null) {
            if (other.spName != null)
                return false;
        } else if (!spName.equals(other.spName))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (subject == null) {
            if (other.subject != null)
                return false;
        } else if (!subject.equals(other.subject))
            return false;
        if (type != other.type)
            return false;
        if (userEmail == null) {
            if (other.userEmail != null)
                return false;
        } else if (!userEmail.equals(other.userEmail))
            return false;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }

    public Builder unbuild() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String jiraKey;
        private String userName;
        private String userEmail;
        private Type type;
        private String status;
        private String body;
        private String idpId;
        private String spId;
        private String spName;
        private String idpName;
        private ZonedDateTime requestDate;
        private ZonedDateTime updateDate;
        private Service service;
        private Settings settings;
        private Consent consent;

        private Builder() {
        }

        private Builder(Action action) {
            this.jiraKey = action.jiraKey;
            this.userName = action.userName;
            this.userEmail = action.userEmail;
            this.type = action.type;
            this.status = action.status;
            this.body = action.body;
            this.idpId = action.idpId;
            this.spId = action.spId;
            this.spName = action.spName;
            this.idpName = action.idpName;
            this.requestDate = action.requestDate;
            this.updateDate = action.updateDate;
            this.service = action.service;
            this.settings = action.settings;
            this.consent = action.consent;
        }

        public Builder requestDate(ZonedDateTime requestDate) {
            this.requestDate = requestDate;
            return this;
        }

        public Builder updateDate(ZonedDateTime updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Builder userEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder jiraKey(String jiraKey) {
            this.jiraKey = jiraKey;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder idpId(String idpId) {
            this.idpId = idpId;
            return this;
        }

        public Builder spId(String spId) {
            this.spId = spId;
            return this;
        }

        public Builder spName(String spName) {
            this.spName = spName;
            return this;
        }

        public Builder idpName(String idpName) {
            this.idpName = idpName;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder service(Service service) {
            this.service = service;
            return this;
        }

        public Builder settings(Settings settings) {
            this.settings = settings;
            return this;
        }

        public Builder consent(Consent consent) {
            this.consent = consent;
            return this;
        }

        public Action build() {
            return new Action(this);
        }
    }

}
