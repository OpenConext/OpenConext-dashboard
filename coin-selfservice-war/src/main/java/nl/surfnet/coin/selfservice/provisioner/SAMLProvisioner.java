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

import org.opensaml.saml2.core.Assertion;
import org.springframework.security.core.userdetails.UserDetails;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.spring.security.opensaml.Provisioner;

/**
 * implementation to return UserDetails from a SAML Assertion
 */
public class SamlProvisioner implements Provisioner {

  @Override
  public UserDetails provisionUser(Assertion assertion) {
/*assertion.getID();
    final AttributeStatement attributeStatement = assertion.getAttributeStatements().get(0);
    final List<Attribute> attributes = attributeStatement.getAttributes();
    for(Attribute a: attributes) {
      a.getName();
    }*/
    return new CoinUser();


  }
}
