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

package selfservice.domain.csa;

import java.io.Serializable;

public class ArticleMedium implements Serializable {

  private static final long serialVersionUID = 1L;
  public enum ArticleMediumType { ANDROIDMARKET, APPLESTORE};
  
  private String name;
  private String url;
  private ArticleMediumType type;
  
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public ArticleMediumType getType() {
    return type;
  }
  public void setType(ArticleMediumType type) {
    this.type = type;
  }
  
  
}
