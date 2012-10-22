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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.ssl.Base64;
import org.springframework.beans.factory.annotation.Required;

import nl.surfnet.spring.security.opensaml.util.KeyStoreUtil;

public class KeyStore {

  private java.security.KeyStore keyStore;
  private Map<String, String> passwords = new HashMap<String, String>();

  /**
   * Constructor for public keys (truststore)
   */
  public KeyStore() {
    this(null, null, null);
  }

  /**
   * Constructor for private keys (keystore)
   * @param privateKey
   * @param password
   * @param certificate
   */
  public KeyStore(String privateKey, String password, String certificate) {
    try {
      keyStore = java.security.KeyStore.getInstance("JKS");
      keyStore.load(null, password == null ? null : password.toCharArray());
      if (privateKey != null && certificate != null) {
        addPrivateKey("lmngKey", privateKey, certificate, password);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Required
  public void setCertificates(Map<String, String> certificates) {
    for (Map.Entry<String, String> entry : certificates.entrySet()) {
      addCertificate(entry.getKey(), entry.getValue());
    }
  }

  public java.security.KeyStore getJavaSecurityKeyStore() {
    return keyStore;
  }

  /**
   * Add a private key (plus its certificate chain) to the given key store.
   * 
   * @param alias
   *          alias of the key
   * @param privateKey
   *          the private key in Base64 encoded BER format.
   * @param certificate
   *          the certificate in PEM format, without ---BEGIN CER.... wrapper
   * @param password
   *          password to protect key with
   */
  public void addPrivateKey(String alias, String privateKey, String certificate, String password) {
    String wrappedCert = "-----BEGIN CERTIFICATE-----\n" + certificate + "\n-----END CERTIFICATE-----";
    byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes());

    try {
      KeyStoreUtil.appendKeyToKeyStore(keyStore, alias, new ByteArrayInputStream(wrappedCert.getBytes()), new ByteArrayInputStream(
          decodedKey), password.toCharArray());
      passwords.put(alias, password);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void addCertificate(String alias, String certificate) {
    KeyStoreUtil.appendCertificateToKeyStore(keyStore, alias, certificate);
  }

  public Map<String, String> getPrivateKeyPasswords() {
    return passwords;
  }

}
