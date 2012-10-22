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

package nl.surfnet.coin.selfservice.service.impl.ssl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

/**
 * Mock implementation of the keystore, used in the mock implementation of the
 * LMNGservice
 * 
 * Doesn't do a thing
 */
public class KeyStoreMock extends KeyStore {

  public KeyStoreMock() {
    this(null, null, null);
  }

  /**
   * Constructor for private keys (keystore)
   * 
   * @param privateKey
   * @param password
   * @param certificate
   */
  public KeyStoreMock(String privateKey, String password, String certificate) {
  }

  @Required
  public void setCertificates(Map<String, String> certificates) {
  }

  public java.security.KeyStore getJavaSecurityKeyStore() {
    return null;
  }

  public void addPrivateKey(String alias, String privateKey, String certificate, String password) {
  }

  public void addCertificate(String alias, String certificate) {
  }

  public Map<String, String> getPrivateKeyPasswords() {
    return null;
  }

}
