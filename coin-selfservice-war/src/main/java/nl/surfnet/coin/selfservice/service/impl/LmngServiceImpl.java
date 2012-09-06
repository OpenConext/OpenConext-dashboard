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

package nl.surfnet.coin.selfservice.service.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;

import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.service.LicensingService;

/**
 * Implementation of a licensing service that get's it information from a
 * webservice interface on LMNG
 */
public class LmngServiceImpl implements LicensingService {

  private static final String PATH_SOAP_REQUEST_GET_LICENSE_QUERY = "lmngqueries/licenseForIdentityProvider.xml";

  @Override
  public List<License> getLicensesForIdentityProvider(IdentityProvider identityProvider) {
    return getLicensesForIdentityProvider(identityProvider, new Date());
  }

  @Override
  public List<License> getLicensesForIdentityProvider(IdentityProvider identityProvider, Date validOn) {
    // TODO implement this method
    List<License> result = new ArrayList<License>();
    // get the file with the soap request

    // TODO add dependencies for this scriplet
    // Resource res = new
    // ClassPathResource(PATH_SOAP_REQUEST_GET_LICENSE_QUERY);
    // InputStream inputStream = res.getInputStream();
    // String soapRequest = IOUtils.toString(inputStream);
    // //TODO get correct IDP-id for LMNG and dateFormatter
    // String.format(soapRequest, identityProvider.getInstitutionId(),
    // validOn.toString());

    // replace identityProviderId and date(s)

    // call the webservice
    QName qname = new QName("");
    Service service = Service.create(qname);
    service.addPort(qname, HTTPBinding.HTTP_BINDING, "http://lmng01.dev.coin.surf.net:80/MSCRMServices/2007/CrmServiceWsdl.aspx");
    Dispatch<Source> dispatcher = service.createDispatch(qname, Source.class, Service.Mode.PAYLOAD);
    Map<String,Object> requestContext = dispatcher.getRequestContext();
    requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "POST");
    Source webserviceResult = dispatcher.invoke(new StreamSource(new StringReader("")));
    // read/parse the response with xstream to License objects
    
    // add each object to the result list

    return result;
  }

}
