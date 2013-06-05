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

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.domain.NotificationMessage;
import nl.surfnet.coin.selfservice.service.NotificationService;
import nl.surfnet.coin.selfservice.util.AjaxResponseException;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.LocaleResolver;

/**
 * Abstract controller used to set model attributes to the request
 */
@Controller
public abstract class BaseController {

  /**
   * The name of the key under which all services are
   * stored
   */
  public static final String SERVICES = "services";

  /**
   * The name of the key under which a service is stored
   * for the detail view
   */
  public static final String SERVICE = "service";

  /**
   * The name of the key under which we store the info if a logged user is
   * allowed to request connections / disconnects
   */
  public static final String SERVICE_APPLY_ALLOWED = "applyAllowed";

  /**
   * The name of the key under which we store the info if a logged user is
   * allowed to ask questions
   */
  public static final String SERVICE_QUESTION_ALLOWED = "questionAllowed";

  /**
   * The name of the key under which we store the info if the status of a
   * technical connection is visible to the current user.
   */
  public static final String SERVICE_CONNECTION_VISIBLE = "connectionVisible";

  /**
   * The name of the key under which we store the info if the connection facet is visible to the current user.
   */
  public static final String FACET_CONNECTION_VISIBLE = "facetConnectionVisible";

  /**
   * The name of the key under which we store the info if a logged user is
   * allowed to filter in the app grid
   */
  public static final String FILTER_APP_GRID_ALLOWED = "filterAppGridAllowed";

  /**
   * The name of the key under which we store the info if a logged user is a
   * kind of admin
   */
  public static final String IS_ADMIN_USER = "isAdminUser";

  /**
   * The name of the key that defines whether a deeplink to SURFMarket should be
   * shown.
   */
  public static final String DEEPLINK_TO_SURFMARKET_ALLOWED = "deepLinkToSurfMarketAllowed";

  /**
   * The name of the key under which we store the info if the logged in user is
   * Distribution Channel Admin (aka God)
   */
  public static final String IS_GOD = "isGod";

  /**
   * The name of the key under which we store the token used to prevent session
   * hijacking
   */
  public static final String TOKEN_CHECK = "tokencheck";

  /**
   * The name of the key under which we store the notifications
   */
  public static final String NOTIFICATIONS = "notificationMessage";

  /**
   * The name of the key under which we store the info if the notifications for
   * licenses/linked services were generated already
   */
  public static final String NOTIFICATION_POPUP_CLOSED = "notificationPopupClosed";

  /**
   * The name of the key under which we store the information from Api regarding
   * group memberships and actual members for auto-completion in the
   * recommendation modal popup.
   */
  public static final String GROUPS_WITH_MEMBERS = "groupsWithMembers";

  /**
   * Key in which we store whether a user should see the technical attribute names of an ARP.
   */
  public static final String RAW_ARP_ATTRIBUTES_VISIBLE = "rawArpAttributesVisible";

  /**
   * Key for the selectedIdp in the session
   */
  public static final String SELECTED_IDP = "selectedIdp";

  @Resource
  private NotificationService notificationService;

  @Resource(name = "localeResolver")
  protected LocaleResolver localeResolver;

  @ModelAttribute(value = "idps")
  public List<InstitutionIdentityProvider> getMyInstitutionIdps() {
    return SpringSecurity.getCurrentUser().getInstitutionIdps();
  }

  @ModelAttribute(value = "locale")
  public Locale getLocale(HttpServletRequest request) {
    return localeResolver.resolveLocale(request);
  }

  /**
   * Exposes the requested IdP for use in RequestMapping methods.
   * 
   * @param idpId
   *          the idp selected in the view
   * @param request
   *          HttpServletRequest, for storing/retrieving the selected idp in the
   *          http session.
   * @return the IdentityProvider selected, or null in case of unknown/invalid
   *         idpId
   */
  @ModelAttribute(value = SELECTED_IDP)
  public InstitutionIdentityProvider getRequestedIdp(@RequestParam(required = false) String idpId, HttpServletRequest request) {
    final InstitutionIdentityProvider selectedIdp = (InstitutionIdentityProvider) request.getSession().getAttribute(SELECTED_IDP);
    if (idpId == null && selectedIdp != null) {
      return selectedIdp;
    }
    if (idpId == null) {
      idpId = SpringSecurity.getCurrentUser().getIdp().getId();
    }
    for (InstitutionIdentityProvider idp : SpringSecurity.getCurrentUser().getInstitutionIdps()) {
      if (idp.getId().equals(idpId)) {
        request.getSession().setAttribute(SELECTED_IDP, idp);
        SpringSecurity.getCurrentUser().setIdp(idp);
        return idp;
      }
    }
    throw new RuntimeException("There is no Selected IdP");
  }

  /**
   * Get notifications from the session (if available) and place as model
   * attribute. Create/generate possible notifications if not found on session
   * and add to session.
   */
  @ModelAttribute(value = "notifications")
  public NotificationMessage getNotifications(@RequestParam(required = false) String idpId, HttpServletRequest request) {
    NotificationMessage notifications = (NotificationMessage) request.getSession().getAttribute(NOTIFICATIONS);
    if (notifications == null) {
      InstitutionIdentityProvider idp = getRequestedIdp(idpId, request);
      notifications = notificationService.getNotifications(idp);
      request.getSession().setAttribute(NOTIFICATIONS, notifications);
    }
    return notifications;
  }

  protected void notificationPopupClosed(HttpServletRequest request) {
    request.getSession().setAttribute(NOTIFICATION_POPUP_CLOSED, Boolean.TRUE);
  }

  /**
   * Handler for {@link AjaxResponseException}. We don't want a 500, but a 400
   * and we want to stream the error message direct to the javaScript
   * 
   * @param e
   *          the exception
   * @return the response body
   */
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(AjaxResponseException.class)
  public Object handleAjaxResponseException(AjaxResponseException e) {
    return e.getMessage();
  }

}