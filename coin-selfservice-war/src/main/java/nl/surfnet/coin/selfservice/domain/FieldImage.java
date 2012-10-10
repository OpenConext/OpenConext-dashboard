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
package nl.surfnet.coin.selfservice.domain;

import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import org.apache.commons.io.IOUtils;
import org.hibernate.annotations.Proxy;
import org.springframework.util.StringUtils;

/**
 * StringField.java
 * 
 */
@SuppressWarnings("serial")
@Entity
@Proxy(lazy = false)
public class FieldImage extends Field {

  @Column(name = "field_image")
  @Lob
  private byte[] image;

  @Column(name = "file_name")
  private String filename;

  @Column(name = "file_url")
  private String fileUrl;

  public FieldImage() {
    super();
  }

  public FieldImage(Source source, Key key, byte[] image) {
    super(source, key, null);
    this.image = image;
  }

  public FieldImage(Source source, Key key, String fileUrl) {
    super(source, key, null);
    this.fileUrl = fileUrl;
  }

  public FieldImage(Source source, Key key, byte[] image, CompoundServiceProvider compoundServiceProvider) {
    super(source, key, compoundServiceProvider);
    this.image = image;
  }

  public FieldImage(Source source, Key key, String fileUrl, CompoundServiceProvider compoundServiceProvider) {
    super(source, key, compoundServiceProvider);
    this.fileUrl = fileUrl;
  }

  public FieldImage(String fileUrl) {
    super();
    this.fileUrl = fileUrl;
  }

  public FieldImage(byte[] image, String filename, String fileUrl) {
    super();
    this.image = image;
    this.filename = filename;
    this.fileUrl = fileUrl;
  }



  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getFileUrl() {
    return fileUrl;
  }

  public void setFileUrl(String fileUrl) {
    this.fileUrl = fileUrl;
  }

  public byte[] getImageBytes() {
    if (image != null && image.length > 0) {
      return image;
    }
    if (StringUtils.hasText(fileUrl)) {
      try {
        return IOUtils.toByteArray(new URL(fileUrl).openStream());
      } catch (Exception e) {
        throw new RuntimeException(e);
      } 
    }
    return null;
  }

  @Override
  public String toString() {
    return "FieldImage [filename=" + filename + ", getSource()=" + getSource() + ", getKey()=" + getKey() + "]";
  }

}
