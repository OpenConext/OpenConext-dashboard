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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import dashboard.sab.SabPerson;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 *
 */
@XStreamAlias("ContactPerson")
public class ContactPerson implements Serializable {

    private static final long serialVersionUID = 1L;

    @XStreamAlias("Name")
    private String name;

    @XStreamAlias("EmailAddress")
    private String emailAddress;

    @XStreamAlias("TelephoneNumber")
    private String telephoneNumber;

    @XStreamAlias("contactType")
    @XStreamAsAttribute
    private ContactPersonType contactPersonType;

    @XStreamAsAttribute
    private boolean sirtfiSecurityContact;

    @XStreamAsAttribute
    private boolean sabContact;

    public ContactPerson(String name, String emailAddress, String telephoneNumber, ContactPersonType contactPersonType,
                         boolean isSirtfiSecurityContact, boolean sabContact) {
        this.name = name;
        this.emailAddress = emailAddress;
        this.telephoneNumber = telephoneNumber;
        this.contactPersonType = contactPersonType;
        this.sirtfiSecurityContact = isSirtfiSecurityContact;
        this.sabContact = sabContact;
    }

    public ContactPerson(SabPerson sabPerson) {
        this.name = sabPerson.fullname();
        this.emailAddress = sabPerson.getEmail();
        this.contactPersonType = ContactPersonType.administrative;
        this.sirtfiSecurityContact = false;
        this.sabContact = true;
    }

    public ContactPerson() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public ContactPersonType getContactPersonType() {
        return contactPersonType;
    }

    public void setContactPersonType(ContactPersonType contactPersonType) {
        this.contactPersonType = contactPersonType;
    }

    public boolean isSirtfiSecurityContact() {
        return sirtfiSecurityContact;
    }

    public void setSirtfiSecurityContact(boolean sirtfiSecurityContact) {
        this.sirtfiSecurityContact = sirtfiSecurityContact;
    }

    public boolean isSabContact() {
        return sabContact;
    }

    public void setSabContact(boolean sabContact) {
        this.sabContact = sabContact;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("email", emailAddress)
                .append("type", contactPersonType)
                .toString();
    }

}
