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

package selfservice.command;

import selfservice.domain.IdentityProvider;

/**
 * Pojo defining a {@link IdentityProvider} and it's LMNG identifier
 */
public class LmngIdentityBinding {

  private String lmngIdentifier;
  private IdentityProvider identityProvider;

  public LmngIdentityBinding() {
  }

  /**
   * Constructor with initial identityprovider
   * @param identityProvider
   */
  public LmngIdentityBinding(IdentityProvider identityProvider) {
    this.identityProvider = identityProvider;
  }

  /**
   * Constructor with initial fields
   *
   * @param lmngIdentifier
   * @param identityProvider
   */
  public LmngIdentityBinding(String lmngIdentifier, IdentityProvider identityProvider) {
    super();
    this.lmngIdentifier = lmngIdentifier;
    this.identityProvider = identityProvider;
  }

  public String getLmngIdentifier() {
    return lmngIdentifier;
  }

  public void setLmngIdentifier(String lmngIdentifier) {
    this.lmngIdentifier = lmngIdentifier;
  }

  public IdentityProvider getIdentityProvider() {
    return identityProvider;
  }

  public void setIdentityProvider(IdentityProvider identityProvider) {
    this.identityProvider = identityProvider;
  }

}
