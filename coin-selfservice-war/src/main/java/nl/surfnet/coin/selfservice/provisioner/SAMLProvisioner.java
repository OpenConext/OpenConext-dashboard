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

package nl.surfnet.coin.selfservice.provisioner;

import java.util.List;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.util.PersonAttributeUtil;
import nl.surfnet.spring.security.opensaml.Provisioner;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AuthenticatingAuthority;
import org.opensaml.saml2.core.AuthnStatement;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * implementation to return UserDetails from a SAML Assertion
 */
public class SAMLProvisioner implements Provisioner {

  private static final String DISPLAY_NAME = "urn:mace:dir:attribute-def:displayName";
  private static final String EMAIL = "urn:mace:dir:attribute-def:mail";
  private static final String SCHAC_HOME = "urn:mace:terena.org:attribute-def:schacHomeOrganization";
  
  private String uuidAttribute = "urn:oid:1.3.6.1.4.1.1076.20.40.40.1";

  @Override
  public UserDetails provisionUser(Assertion assertion) {

    CoinUser coinUser = new CoinUser();

    final String idpId = getAuthenticatingAuthority(assertion);
    coinUser.setIdp(idpId);



    coinUser.setUid(getValueFromAttributeStatements(assertion, uuidAttribute));
    coinUser.setDisplayName(getValueFromAttributeStatements(assertion, DISPLAY_NAME));
    coinUser.setEmail(getValueFromAttributeStatements(assertion, EMAIL));
    coinUser.setSchacHomeOrganization(getValueFromAttributeStatements(assertion, SCHAC_HOME));

    coinUser.setAttributeMap(PersonAttributeUtil.getAttributesAsMap(assertion));

    return coinUser;
  }


  private String getAuthenticatingAuthority(final Assertion assertion) {
    final List<AuthnStatement> authnStatements = assertion.getAuthnStatements();
    for (AuthnStatement as : authnStatements) {
      final List<AuthenticatingAuthority> authorities = as.getAuthnContext().getAuthenticatingAuthorities();
      for (AuthenticatingAuthority aa : authorities) {
        if (StringUtils.isNotBlank(aa.getURI())) {
          return aa.getURI();
        }
      }
    }
    throw new RuntimeException("No AuthenticatingAuthority present in the Assertion:" + ToStringBuilder.reflectionToString(assertion));
  }

  private String getValueFromAttributeStatements(final Assertion assertion, final String name) {
    final List<AttributeStatement> attributeStatements = assertion.getAttributeStatements();
    for (AttributeStatement attributeStatement : attributeStatements) {
      final List<Attribute> attributes = attributeStatement.getAttributes();
      for (Attribute attribute : attributes) {
        if (name.equals(attribute.getName())) {
          return attribute.getAttributeValues().get(0).getDOM().getFirstChild().getNodeValue();
        }
      }
    }
    return "";
  }


  public void setUuidAttribute(String uuidAttribute) {
    this.uuidAttribute = uuidAttribute;
  }

 
}
