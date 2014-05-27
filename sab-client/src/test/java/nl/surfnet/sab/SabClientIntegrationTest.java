/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.sab;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class SabClientIntegrationTest {

  @Test
  @Ignore
  public void test() throws IOException {

    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("x", "x");
    HttpClientTransport transport = new HttpClientTransport(credentials, credentials, URI.create("x"), URI.create("y"));
    SabClient sabClient = new SabClient(transport);
    SabRoleHolder roles = sabClient.getRoles("x");
    System.out.println(roles);
  }
}
