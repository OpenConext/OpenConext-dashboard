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

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.selfservice.dao.FieldImageDao;
import nl.surfnet.coin.selfservice.dao.ScreenshotDao;
import nl.surfnet.coin.selfservice.domain.FieldImage;
import nl.surfnet.coin.selfservice.domain.Screenshot;

import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * FieldContentController.java
 * 
 */
@Controller
public class ImageController {

  @Resource
  private FieldImageDao fieldImageDao;

  @Resource
  private ScreenshotDao screenshotDao;

  @RequestMapping(method = RequestMethod.GET, value = FieldImage.FILE_URL + "{fieldImageId}")
  public void fieldImage(HttpServletResponse response, @PathVariable("fieldImageId") String fieldImageId) throws IOException {
    // ends with .img
    FieldImage fieldImage = fieldImageDao.findById(parse(fieldImageId));
    flush(response, fieldImage.getImage());
  }

  @RequestMapping(method = RequestMethod.GET, value = Screenshot.FILE_URL + "{screenshotId}")
  public void screenshot(HttpServletResponse response, @PathVariable("screenshotId") String screenshotId) throws IOException {
    Screenshot screenshot = screenshotDao.findById(parse(screenshotId));
    flush(response, screenshot.getImage());
  }

  private void flush(HttpServletResponse response, byte[] bytes) throws IOException {
    response.getOutputStream().write(bytes);
    response.flushBuffer();
  }

  private Long parse(String imageId) {
    Assert.hasText(imageId);
    Assert.isTrue(imageId.length() > FieldImage.FILE_POSTFIX.length());
    return Long.parseLong(imageId.substring(FieldImage.FILE_POSTFIX.length()));
  }

}
