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

package nl.surfnet.coin.selfservice.control.shopadmin;

import javax.annotation.Resource;

import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.dao.CompoundServiceProviderDao;
import nl.surfnet.coin.selfservice.dao.FieldImageDao;
import nl.surfnet.coin.selfservice.dao.FieldStringDao;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.Field.Source;
import nl.surfnet.coin.selfservice.domain.FieldString;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/shopadmin/*")
public class SpLmngDataBindingController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(SpLmngDataBindingController.class);

  @Resource(name = "providerService")
  private ServiceProviderService sps;

  @Resource
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Resource
  private FieldStringDao fieldStringDao;

  @Resource
  private FieldImageDao fieldImageDao;

  @RequestMapping(value = "/compoundSp-detail")
  public ModelAndView get(@RequestParam("spEntityId") String entityId) {
    ServiceProvider serviceProvider = sps.getServiceProvider(entityId);
    Assert.notNull(serviceProvider, "No such SP with entityId: " + entityId);

    CompoundServiceProvider compoundServiceProvider = compoundServiceProviderDao.findByEntityId(serviceProvider.getId());
    if (compoundServiceProvider == null) {
      LOG.debug("No compound Service Provider for SP '{}' yet. Will init one.", entityId);
      compoundServiceProvider = CompoundServiceProvider.builder(serviceProvider, new License());
    }
    return new ModelAndView("shopadmin/compoundSp-detail", "compoundSp", compoundServiceProvider);
  }

  @RequestMapping(value = "/compoundSp-update", method = RequestMethod.POST)
  public @ResponseBody
  String updateField(@RequestParam(value = "compoundsp_id", required = true) Long compoundSpId,
      @RequestParam(value = "field_id", required = true) Long fieldId, @RequestParam(value = "value", required = true) String value,
      @RequestParam(value = "source", required = true) Source source) {
    FieldString field = fieldStringDao.findById(fieldId);
    field.setSource(source);
    field.setValue(value);
    return "ok";
  }

}
