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
package nl.surfnet.coin.selfservice.control;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * ImageControllerTest.java
 *
 */
public class ImageControllerTest {

  @Test
  public void testImageScale() throws IOException {
    BufferedImage img = ImageIO.read(new ClassPathResource("1024x768.png").getInputStream());
    BufferedImage small = Scalr.resize(img , 300);
    ImageIO.write(small, "png", new File("./target/300xx.png"));
    
  }
  
}
