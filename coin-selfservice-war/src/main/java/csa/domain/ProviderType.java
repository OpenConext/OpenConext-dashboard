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

package csa.domain;

/**
<pre>
 &lt;xs:enumeration value="saml20"/&gt;
 &lt;xs:enumeration value="aselect"/&gt;
 &lt;xs:enumeration value="wsfed1x"/&gt;
 &lt;xs:enumeration value="proxy"/&gt;
 &lt;xs:enumeration value="radius"/&gt;
 &lt;xs:enumeration value="shib13"/&gt;
 </pre>
 */
public enum ProviderType {
  saml20,
  aselect,
  wsfed1x,
  proxy,
  radius,
  shib13
}
