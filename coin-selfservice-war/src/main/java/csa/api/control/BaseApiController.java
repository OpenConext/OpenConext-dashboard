/*
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
package csa.api.control;

import csa.domain.CheckTokenResponse;
import csa.filter.AuthorizationServerFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public abstract class BaseApiController {

  private static final Logger LOG = LoggerFactory.getLogger(BaseApiController.class);

  /**
   * Attribute in the VerifyTokenResponse that contains the Identity Provider (its entityId)
   */
  public static final String IDENTITY_PROVIDER_ATTRIBUTE = "IDENTITY_PROVIDER";

  /*
 * Retrieve IDP Entity ID from the oauth token stored in the request
 *
 */
  protected String getIdpEntityIdFromToken(final HttpServletRequest request) {
    CheckTokenResponse checkToken = (CheckTokenResponse) request.getAttribute(AuthorizationServerFilter.CHECK_TOKEN_RESPONSE);
    return checkToken.getIdPEntityId();
  }

  protected void verifyScope(HttpServletRequest request, String scopeRequired) {
    CheckTokenResponse checkToken = (CheckTokenResponse) request.getAttribute(AuthorizationServerFilter.CHECK_TOKEN_RESPONSE);
    List<String> scopes = checkToken.getScopes();
    if (CollectionUtils.isEmpty(scopes) || !scopes.contains(scopeRequired)) {
      throw new ScopeVerificationException("Scope required is '" + scopeRequired + "', but not granted. Granted scopes: " + scopes);
    }
  }

  @ExceptionHandler(ScopeVerificationException.class)
  public void handleSecurityException(Exception ex, HttpServletResponse response) throws IOException {
    LOG.info("Will return 403 Forbidden", ex);
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Granted scope not sufficient");
    response.flushBuffer();
  }

  @ExceptionHandler(Exception.class)
  public void handleGenericException(Exception ex, HttpServletResponse response) throws IOException {
    LOG.info("Will return 409 Conflict", ex);
    response.sendError(HttpServletResponse.SC_CONFLICT, "The request could not be completed due to a conflict with the current state of the resource.");
    response.flushBuffer();
  }

}
