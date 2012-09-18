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

package nl.surfnet.coin.selfservice.service.impl.ntlm;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.params.HttpParams;

/**
 * Custom scheme factory creating a NTLMScheme using our custom JCIFSengine.
 * 
 * This is used temporary as long as we don't have a certificate implementation
 * of the webservice for LMNG
 * 
 * Also @see http://hc.apache.org/httpcomponents-client-ga/ntlm.html
 */
public class NTLMSchemeFactory implements AuthSchemeFactory {

  public AuthScheme newInstance(final HttpParams params) {
    return new NTLMScheme(new JCIFSEngine());
  }
}
