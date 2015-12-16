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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;


import org.hibernate.annotations.Proxy;

import selfservice.util.DomainObject;

@SuppressWarnings("serial")
@Entity
@Proxy(lazy = false)
public class Screenshot extends DomainObject {

  public static final String FILE_URL = "/screenshots/";

  @Column(name = "field_image")
  @Lob
  private byte[] image;

  @Transient
  private String fileUrl;
  
  @ManyToOne
  @JoinColumn(name = "compound_service_provider_id", nullable = false)
  private CompoundServiceProvider compoundServiceProvider;

  public Screenshot() {
  }

  public Screenshot(Long id) {
    setId(id);
  }

  public Screenshot(byte[] image) {
    this.image = image;
  }

  public byte[] getImage() {
    return image;
  }

  public String getFileUrl() {
    return FILE_URL + getId() + FieldImage.FILE_POSTFIX;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  public CompoundServiceProvider getCompoundServiceProvider() {
    return compoundServiceProvider;
  }

  public void setCompoundServiceProvider(CompoundServiceProvider compoundServiceProvider) {
    this.compoundServiceProvider = compoundServiceProvider;
  }
}
