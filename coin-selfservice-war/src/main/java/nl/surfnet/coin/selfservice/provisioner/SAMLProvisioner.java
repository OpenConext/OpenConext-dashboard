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

import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_USER;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.annotation.Resource;

import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.janus.domain.JanusEntity;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;
import nl.surfnet.coin.selfservice.util.PersonAttributeUtil;
import nl.surfnet.spring.security.opensaml.Provisioner;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AuthenticatingAuthority;
import org.opensaml.saml2.core.AuthnStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * implementation to return UserDetails from a SAML Assertion
 */
public class SAMLProvisioner implements Provisioner {

  private static final String DISPLAY_NAME = "urn:mace:dir:attribute-def:displayName";
  private static final String EMAIL = "urn:mace:dir:attribute-def:mail";
  private static final String SCHAC_HOME = "urn:mace:terena.org:attribute-def:schacHomeOrganization";
  
  private String uuidAttribute = "urn:oid:1.3.6.1.4.1.1076.20.40.40.1";

  private static final Logger LOG = LoggerFactory.getLogger(SAMLProvisioner.class);

  private IdentityProviderService identityProviderService;

  @Resource(name = "janusClient")
  private Janus janusClient;

  @Override
  public UserDetails provisionUser(Assertion assertion) {

    CoinUser coinUser = new CoinUser();

    final String idpId = getAuthenticatingAuthority(assertion);
    coinUser.setIdp(idpId);

    coinUser.setInstitutionId(getInstitutionId(idpId));

    for (IdentityProvider idp : identityProviderService.getInstituteIdentityProviders(coinUser.getInstitutionId())) {
      coinUser.addInstitutionIdp(idp);
    }
    // Add the one the user is currently identified by if it's not in the list
    // already.
    if (coinUser.getInstitutionIdps().isEmpty()) {
      IdentityProvider idp = getInstitutionIdP(idpId);
      coinUser.addInstitutionIdp(idp);
    }

    coinUser.setUid(getValueFromAttributeStatements(assertion, uuidAttribute));
    coinUser.setDisplayName(getValueFromAttributeStatements(assertion, DISPLAY_NAME));
    coinUser.setEmail(getValueFromAttributeStatements(assertion, EMAIL));
    coinUser.setSchacHomeOrganization(getValueFromAttributeStatements(assertion, SCHAC_HOME));

    coinUser.addAuthority(new CoinAuthority(ROLE_USER));
    coinUser.setAttributeMap(PersonAttributeUtil.getAttributesAsMap(assertion));

    return coinUser;
  }

  private String getInstitutionId(String idpId) {
    final IdentityProvider identityProvider = identityProviderService.getIdentityProvider(idpId);
    if (identityProvider != null) {
      final String institutionId = identityProvider.getInstitutionId();
      if (!StringUtils.isBlank(institutionId)) {
        return institutionId;
      }
    }
    //corner case, but possible
    return null;
  }

  private IdentityProvider getInstitutionIdP(String idpId) {
    IdentityProvider idp = identityProviderService.getIdentityProvider(idpId);
    if (idp == null) {
      final JanusEntity entity = janusClient.getEntity(idpId);
      if (entity == null) {
        idp = new IdentityProvider(idpId, null, idpId);
      } else {
        idp = new IdentityProvider(entity.getEntityId(), null, entity.getPrettyName());
      }
    }
    return idp;
  }

  private String getAuthenticatingAuthority(final Assertion assertion) {
    final List<AuthnStatement> authnStatements = assertion.getAuthnStatements();
    for (AuthnStatement as : authnStatements) {
      final List<AuthenticatingAuthority> authorities = as.getAuthnContext().getAuthenticatingAuthorities();
      for (AuthenticatingAuthority aa : authorities) {
        if (StringUtils.isNotBlank(aa.getURI())) {
          try {
            return URLDecoder.decode(aa.getURI(), "UTF-8");
          } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Machine does not support UTF-8", e);
          }
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

  public void setIdentityProviderService(IdentityProviderService identityProviderService) {
    this.identityProviderService = identityProviderService;
  }

  public void setUuidAttribute(String uuidAttribute) {
    this.uuidAttribute = uuidAttribute;
  }

 
}
