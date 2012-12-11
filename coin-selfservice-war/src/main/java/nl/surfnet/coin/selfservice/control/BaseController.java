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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.NotificationMessage;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;
import nl.surfnet.coin.selfservice.service.NotificationService;
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
   * The name of the key under which all compoundSps (e.g. the services) are
   * stored
   */
  public static final String COMPOUND_SPS = "compoundSps";

  /**
   * The name of the key under which all identityproviders are stored
   */
  public static final String ALL_IDPS = "allIdps";

  /**
   * The name of the key under which a compoundSps (e.g. the service) is stored
   * for the detail view
   */
  public static final String COMPOUND_SP = "compoundSp";

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
   * The name of the key under which we store the info if LMNG is active (e.g.
   * we use License Info)
   */
  public static final String LMNG_ACTIVE_MODUS = "lmngActiveModus";

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
  private static final String NOTIFICATIONS = "notifications";

  /**
   * The name of the key under which we store the info if the notifications for
   * licenses/linked services were generated already
   */
  private static final String NOTIFICATIONS_LINKED_LICENSE_GENERATED = "linkedLicenseNotificationsGenerated";

  @Resource(name = "providerService")
  private IdentityProviderService idpService;

  @Resource
  private NotificationService notificationService;

  @Resource(name = "localeResolver")
  protected LocaleResolver localeResolver;

  @ModelAttribute(value = "idps")
  public List<IdentityProvider> getMyInstitutionIdps() {
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
  @ModelAttribute(value = "selectedidp")
  public IdentityProvider getRequestedIdp(@RequestParam(required = false) String idpId, HttpServletRequest request) {
    final Object selectedidp = request.getSession().getAttribute("selectedidp");
    if (idpId == null && selectedidp != null) {
      return (IdentityProvider) selectedidp;
    }
    if (idpId == null) {
      idpId = SpringSecurity.getCurrentUser().getIdp();
    }
    for (IdentityProvider idp : SpringSecurity.getCurrentUser().getInstitutionIdps()) {
      if (idp.getId().equals(idpId)) {
        request.getSession().setAttribute("selectedidp", idp);
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
  public List<NotificationMessage> getNotifications(@RequestParam(required = false) String idpId, HttpServletRequest request) {
    Object notifications = request.getSession().getAttribute(NOTIFICATIONS);
    if (notifications == null) {
      notifications = new ArrayList<NotificationMessage>();
    }
    @SuppressWarnings("unchecked")
    List<NotificationMessage> notificationMessages = (ArrayList<NotificationMessage>) notifications;

    IdentityProvider idp = getRequestedIdp(idpId, request);

    if (request.getSession().getAttribute(NOTIFICATIONS_LINKED_LICENSE_GENERATED) == null) {
      notificationMessages = notificationService.getNotifications(idp);
      request.getSession().setAttribute(NOTIFICATIONS_LINKED_LICENSE_GENERATED, Boolean.TRUE);
    }

    request.getSession().setAttribute(NOTIFICATIONS, notificationMessages);

    return notificationMessages;
  }
  
  /** 
   * Handler for RuntimeExceptions. We don't want a 500, but a 400
   * @param e the exception
   * @return the response body
   */
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(RuntimeException.class)
  public Object handleException(RuntimeException e) {
    return e.getMessage();
  }

}