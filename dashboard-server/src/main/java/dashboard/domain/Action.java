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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public class Action {

    public enum Type {
        LINKREQUEST, UNLINKREQUEST, CHANGE, LINKINVITE;
    }

    private String jiraKey;
    private String userName;
    private String userEmail;
    private String emailTo;
    private boolean shouldSendEmail;
    private boolean connectWithoutInteraction;
    private String body;
    private String personalMessage;
    private String emailContactPerson;

    private String idpId;
    private String spId;
    private Long spEid;
    private String idpName;
    private String spName;
    private String typeMetaData;

    private ZonedDateTime requestDate;
    private ZonedDateTime updateDate;
    private Type type;
    private String status;
    private String resolution;

    private Service service;
    private Settings settings;
    private Consent consent;
    private String loaLevel;
    private List<String> manageUrls;

    private boolean rejected;

    private Action(Builder builder) {
        this.jiraKey = builder.jiraKey;
        this.userName = builder.userName;
        this.userEmail = builder.userEmail;
        this.emailTo = builder.emailTo;
        this.shouldSendEmail = builder.shouldSendEmail;
        this.connectWithoutInteraction = builder.connectWithoutInteraction;
        this.body = builder.body;
        this.personalMessage = builder.personalMessage;
        this.emailContactPerson = builder.emailContactPerson;
        this.idpId = builder.idpId;
        this.spId = builder.spId;
        this.spEid = builder.spEid;
        this.spName = builder.spName;
        this.typeMetaData = builder.typeMetaData;
        this.idpName = builder.idpName;
        this.requestDate = builder.requestDate;
        this.updateDate = builder.updateDate;
        this.type = builder.type;
        this.status = builder.status;
        this.resolution = builder.resolution;
        this.service = builder.service;
        this.settings = builder.settings;
        this.consent = builder.consent;
        this.rejected = builder.rejected;
        this.loaLevel = builder.loaLevel;
        this.manageUrls = builder.manageUrls;
    }

    /**
     * get a Comparator that sorts by date ascending: newest first
     */
    public static Comparator<? super Action> sortByDateAsc() {
        return (a1, a2) -> new CompareToBuilder()
                .append(a1.getRequestDate(), a2.getRequestDate())
                .toComparison();
    }

    public Builder unbuild() {
        return new Builder(this);
    }

    public void addManageUrl(String manageUrl) {
        if (this.manageUrls == null) {
            manageUrls = new ArrayList<>();
        }
        manageUrls.add(manageUrl);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String jiraKey;
        private String userName;
        private String userEmail;
        private String emailTo;
        private boolean shouldSendEmail;
        private boolean connectWithoutInteraction;
        private Type type;
        private String status;
        private String resolution;
        private String body;
        private String personalMessage;
        private String emailContactPerson;
        private String idpId;
        private String spId;
        private Long spEid;
        private String spName;
        private String typeMetaData;
        private String idpName;
        private ZonedDateTime requestDate;
        private ZonedDateTime updateDate;
        private Service service;
        private Settings settings;
        private Consent consent;
        private boolean rejected;
        private String loaLevel;
        private List<String> manageUrls;

        private Builder() {
        }

        private Builder(Action action) {
            this.jiraKey = action.jiraKey;
            this.userName = action.userName;
            this.userEmail = action.userEmail;
            this.emailTo = action.emailTo;
            this.shouldSendEmail = action.shouldSendEmail;
            this.connectWithoutInteraction = action.connectWithoutInteraction;
            this.type = action.type;
            this.status = action.status;
            this.resolution = action.resolution;
            this.body = action.body;
            this.personalMessage = action.personalMessage;
            this.emailContactPerson = action.emailContactPerson;
            this.idpId = action.idpId;
            this.spId = action.spId;
            this.spEid = action.spEid;
            this.spName = action.spName;
            this.typeMetaData = action.typeMetaData;
            this.idpName = action.idpName;
            this.requestDate = action.requestDate;
            this.updateDate = action.updateDate;
            this.service = action.service;
            this.settings = action.settings;
            this.consent = action.consent;
            this.rejected = action.rejected;
            this.loaLevel = action.loaLevel;
            this.manageUrls = action.manageUrls;
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

        public Builder emailTo(String emailTo) {
            this.emailTo = emailTo;
            return this;
        }

        public Builder shouldSendEmail(boolean shouldSendEmail) {
            this.shouldSendEmail = shouldSendEmail;
            return this;
        }

        public Builder connectWithoutInteraction(boolean connectWithoutInteraction) {
            this.connectWithoutInteraction = connectWithoutInteraction;
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

        public Builder personalMessage(String personalMessage) {
            this.personalMessage = personalMessage;
            return this;
        }

        public Builder emailContactPerson(String emailContactPerson) {
            this.emailContactPerson = emailContactPerson;
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

        public Builder spEid(Long spEid) {
            this.spEid = spEid;
            return this;
        }

        public Builder spName(String spName) {
            this.spName = spName;
            return this;
        }

        public Builder typeMetaData(String typeMetaData) {
            this.typeMetaData = typeMetaData;
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

        public Builder resolution(String resolution) {
            this.resolution = resolution;
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

        public Builder loaLevel(String loaLevel) {
            this.loaLevel = loaLevel;
            return this;
        }

        public Builder manageUrls(List<String> manageUrls) {
            this.manageUrls = manageUrls;
            return this;
        }

        public Builder rejected(boolean rejected) {
            this.rejected = rejected;
            return this;
        }

        public Action build() {
            return new Action(this);
        }
    }

}
