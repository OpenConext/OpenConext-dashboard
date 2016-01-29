/*
 Copyright 2012 SURFnet bv, The Netherlands

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package csa.control.shopadmin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import csa.control.BaseController;
import csa.dao.FieldStringDao;
import csa.dao.ScreenshotDao;
import csa.domain.Field;
import csa.domain.FieldImage;
import csa.domain.FieldString;
import csa.service.impl.CompoundSPService;
import csa.dao.CompoundServiceProviderDao;
import csa.dao.FieldImageDao;
import csa.domain.CompoundServiceProvider;
import csa.domain.Screenshot;

@Controller
@RequestMapping(value = "/shopadmin/*")
public class SpLmngDataBindingController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(SpLmngDataBindingController.class);

  @Resource
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Resource
  private CompoundSPService compoundSPService;

  @Resource
  private FieldStringDao fieldStringDao;

  @Resource
  private FieldImageDao fieldImageDao;

  @Resource
  private ScreenshotDao screenshotDao;

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @RequestMapping(value = "/compoundSp-detail")
  public ModelAndView get(@RequestParam("spEntityId") String entityId) {
    CompoundServiceProvider compoundServiceProvider = compoundSPService.getCSPByServiceProviderEntityId(entityId);
    Map model = new HashMap();
    model.put(BaseController.COMPOUND_SP, compoundServiceProvider);
    LOG.debug("Listing service with id: {}", entityId);
    LOG.debug("Listing fields: {}", compoundServiceProvider.getFields());

    return new ModelAndView("shopadmin/compoundSp-detail", model);
  }

  @RequestMapping(value = "/compoundSp-update", method = RequestMethod.POST, params = "usethis=usethis-image")
  public @ResponseBody
  String updateImageField(@RequestParam(value = "fieldId") Long fieldId, @RequestParam(value = "source") Field.Source source) {
    FieldImage fieldImage = fieldImageDao.findOne(fieldId);
    validateCombination(source, fieldImage);
    fieldImage.setSource(source);
    fieldImage = fieldImageDao.save(fieldImage);
    return fieldImage.getSource().name();
  }

  @RequestMapping(value = "/compoundSp-update", method = RequestMethod.POST)
  public @ResponseBody
  String updateStringField(@RequestParam(value = "fieldId") Long fieldId, @RequestParam(value = "value", required = false) String value,
      @RequestParam(value = "source") Field.Source source, @RequestParam(value = "usethis", required = false) String useThis) {
    FieldString field = fieldStringDao.findOne(fieldId);
    validateCombination(source, field);
    field.setValue(value);
    if (StringUtils.hasText(useThis)) {
      field.setSource(source);
    }
    field = fieldStringDao.save(field);
    return field.getSource().name();
  }

  private void validateCombination(Field.Source source, Field field) {
    //Check the combination Field#Key and Field#Source
    if (!CompoundServiceProvider.isAllowedCombination(field.getKey(), source)) {
      throw new IllegalArgumentException(String.format("Not allowed combination. Key %s and Source %s", field.getKey(), source));
    }
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public @ResponseBody
  String upload(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "source") Field.Source source,
      @RequestParam(value = "fieldId") Long fieldId, @RequestParam(value = "usethis", required = false) String useThis) throws IOException {
    if (Field.Source.DISTRIBUTIONCHANNEL.equals(source)) {
      Assert.isTrue(file != null, "File upload is required for Distrubution Channel");
    }
    FieldImage field = fieldImageDao.findOne(fieldId);
    if (StringUtils.hasText(useThis)) {
      field.setSource(source);
    }
    if (file != null) {
      field.setImage(file.getBytes());
    }
    field = fieldImageDao.save(field);
    return field.getFileUrl();
  }

  @RequestMapping(value = "/upload-screenshot", method = RequestMethod.POST, produces = "application/json")
  public @ResponseBody
  Screenshot screenshot(@RequestParam(value = "file", required = true) MultipartFile file,
      @RequestParam(value = "compoundServiceProviderId") Long compoundServiceProviderId,
      HttpServletResponse response) throws IOException {
    Screenshot screenshot = new Screenshot(file.getBytes());
    CompoundServiceProvider csp = compoundServiceProviderDao.findOne(compoundServiceProviderId);
    csp.addScreenShot(screenshot);
    screenshot = screenshotDao.save(screenshot);
    response.setHeader("X-UA-Compatible", "IE=edge,chrome=1");
    return new Screenshot(screenshot.getId());
  }

  @RequestMapping(value = "/remove-screenshot/{screenshotId}", method = RequestMethod.DELETE)
  public @ResponseBody
  String screenshot(@PathVariable("screenshotId") Long screenshotId) throws IOException {
    Screenshot sc = screenshotDao.findOne(screenshotId);
    screenshotDao.delete(sc);
    LOG.debug("Screenshot " + screenshotId + " removed");
    return "ok";
  }

}
