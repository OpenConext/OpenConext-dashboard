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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.hibernate.annotations.Proxy;

/**
 * StringField.java
 * 
 */
@SuppressWarnings("serial")
@Entity
@Proxy(lazy = false)
public class FieldImage extends Field {

  public static final String FILE_URL = "/fieldimages/";
  public static final String FILE_POSTFIX = ".img";
  
  @Column(name = "field_image")
  @Lob
  private byte[] image;

  @Transient
  private String fileUrl;
  
//  @Column
//  private String contentType;

  public FieldImage() {
    super();
  }

  public FieldImage(Source source, Key key, byte[] image) {
    super(source, key, null);
    this.image = image;
  }

  public FieldImage(Source source, Key key, byte[] image, CompoundServiceProvider compoundServiceProvider) {
    super(source, key, compoundServiceProvider);
    this.image = image;
  }

  public FieldImage(Source source, Key key, CompoundServiceProvider compoundServiceProvider) {
    super(source, key, compoundServiceProvider);
  }

  public FieldImage(byte[] image) {
    super();
    this.image = image;
  }

  public FieldImage(String fileUrl) {
    this.fileUrl = fileUrl;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  public String getFileUrl() {
     return image == null ? null : FILE_URL + getId() + FILE_POSTFIX;
  }

  @Override
  public String toString() {
    return "FieldImage [fileUrl=" + fileUrl +
      ", getSource()=" + getSource() +
      ", getKey()=" + getKey() +
      ", image=" + (image == null ? "" : (image.length + " bytes")) +
      ", getId()=" + getId() + "]";
  }

  /* (non-Javadoc)
   * @see nl.surfnet.coin.csa.domain.Field#isUnset()
   */
  @Override
  public boolean isUnset() {
    return Field.Source.DISTRIBUTIONCHANNEL.equals(getSource()) && (image == null || image.length == 0);
  }

}
