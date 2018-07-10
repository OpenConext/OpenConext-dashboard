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

package selfservice.sab;

import java.io.IOException;
import java.io.InputStream;

public class LocalFileTransport implements SabTransport {

  private String filename;
  private final String restFileName;

  public LocalFileTransport(String filename, String restFileName) {
    this.filename = filename;
    this.restFileName = restFileName;
  }

  @Override
  public InputStream getResponse(String request) throws IOException {
    return this.getClass().getResourceAsStream(filename);
  }

  @Override
  public InputStream getRestResponse(String organisationAbbreviation, String role) {
    return this.getClass().getResourceAsStream(restFileName);
  }
}
