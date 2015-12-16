package csa.service.impl;/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import csa.domain.Account;
import csa.domain.Article;
import csa.model.License;

public interface CrmUtil {

  public enum LicenseRetrievalAttempt{ One, Two, Three }

  List<Article> parseArticlesResult(String webserviceResult) throws ParserConfigurationException, SAXException, IOException;

  List<License> parseLicensesResult(String webserviceResult) throws ParserConfigurationException, SAXException, IOException;

  List<Account> parseAccountsResult(String webserviceResult) throws ParserConfigurationException, SAXException, IOException;

  String parseResultInstitute(String webserviceResult) throws ParserConfigurationException, SAXException, IOException;

  String getLmngSoapRequestForIdpAndSp(String institutionId, List<String> serviceIds, Date validOn, String endpoint, LicenseRetrievalAttempt licenseRetrievalAttempt) throws IOException;

  String getLmngSoapRequestForSps(Collection<String> serviceIds, String endpoint) throws IOException;

  String getLmngSoapRequestForAllAccount(boolean isInstitution, String endpoint) throws IOException;

  String getLmngRequestEnvelope() throws IOException;

}
