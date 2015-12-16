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
package selfservice.api.csa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import selfservice.domain.InstitutionIdentityProvider;
import selfservice.domain.csa.IdentityProvider;
import selfservice.interceptor.AuthorityScopeInterceptor;
import selfservice.service.IdentityProviderService;

@Controller
@RequestMapping
public class ServiceRegistryController extends BaseApiController {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistryController.class);

  @Resource
  private IdentityProviderService identityProviderService;

  @RequestMapping(method = RequestMethod.GET, value = "/api/protected/identityproviders.json")
  public @ResponseBody
  List<InstitutionIdentityProvider> getIdps(@RequestParam(value = "identityProviderId") String identityProviderId) throws IOException {
    LOG.debug("Got request for identityProviders. identityProviderId: {}", identityProviderId);
    List<InstitutionIdentityProvider> result = new ArrayList<>();
    IdentityProvider identityProvider = identityProviderService.getIdentityProvider(identityProviderId);
    if (identityProvider != null) {
      String institutionId = identityProvider.getInstitutionId();
      if (StringUtils.isBlank(institutionId)) {
        result.add(convertIdentityProviderToInstitutionIdentityProvider(identityProvider)) ;
      } else {
        List<IdentityProvider> instituteIdentityProviders = identityProviderService.getInstituteIdentityProviders(institutionId);
        for (IdentityProvider provider : instituteIdentityProviders) {
          result.add(convertIdentityProviderToInstitutionIdentityProvider(provider)) ;
        }
      }
    }
    LOG.debug("Result of call to getIdps with parameter {}: {}", identityProviderId, result);
    return result;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/protected/all-identityproviders.json")
  public @ResponseBody
  List<InstitutionIdentityProvider> getAllIdps(final HttpServletRequest request) throws IOException {
    LOG.debug("Got request for all identityProviders");
    verifyScope(request, AuthorityScopeInterceptor.OAUTH_CLIENT_SCOPE_CROSS_IDP_SERVICES);
    List<InstitutionIdentityProvider> result = new ArrayList<>();
    List<IdentityProvider> identityProviders = identityProviderService.getAllIdentityProviders();
    for (IdentityProvider identityProvider : identityProviders) {
      result.add(convertIdentityProviderToInstitutionIdentityProvider(identityProvider));
    }
    LOG.debug("Finished request for all identityProviders {}", result);
    return result;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/protected/services-usage.json")
  public @ResponseBody
  List<IdentityProvider> getIdpsUsingSp(@RequestParam(value = "spEntityId") String spEntityId,
                                                   final HttpServletRequest request) throws IOException {
    LOG.debug("Got request for all linked identityProviders tp {}",spEntityId);
    List<IdentityProvider> result = identityProviderService.getLinkedIdentityProviders(spEntityId);
    LOG.debug("Finished request for all linked identityProviders {}", result);
    return result;
  }

  private InstitutionIdentityProvider convertIdentityProviderToInstitutionIdentityProvider(IdentityProvider identityProvider) {
    return new InstitutionIdentityProvider(identityProvider.getId(), identityProvider.getName(), identityProvider.getInstitutionId());
  }
}
