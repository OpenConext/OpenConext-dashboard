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
package selfservice.janus;

import java.util.List;

import selfservice.domain.ARP;
import selfservice.janus.domain.EntityMetadata;
import selfservice.janus.domain.JanusEntity;

public interface Janus {

  enum Metadata {
    OAUTH_SECRET("coin:oauth:secret"),
    OAUTH_CONSUMERKEY("coin:gadgetbaseurl"),
    OAUTH_CALLBACKURL("coin:oauth:callback_url"),
    OAUTH_TWOLEGGEDALLOWED("coin:oauth:two_legged_allowed"),
    OAUTH_APPTITLE("name:en"),
    OAUTH_APPDESCRIPTION("description:en"),
    OAUTH_APPTHUMBNAIL("logo:0:url"),
    OAUTH_APPICON("coin:oauth:app_icon"),
    OAUTH_CONSENTNOTREQUIRED("coin:oauth:consent_not_required"),
    ORGANIZATION_URL("OrganizationURL:en"),
    ORGANIZATION_URL_NL("OrganizationURL:nl"),
    ORGANIZATION_NAME("OrganizationName:en"),
    ORGANIZATION_NAME_NL("OrganizationName:nl"),
    INSITUTION_ID("coin:institution_id"),
    LOGO_URL("logo:0:url"),
    NAMEIDFORMAT("NameIDFormat"),
    DISPLAYNAME("displayName:en"),
    DISPLAYNAME_NL("displayName:nl"),
    NAME("name:en"),
    NAME_NL("name:nl"),
    DESCRIPTION("description:en"),
    DESCRIPTION_NL("description:nl"),
    CONTACTS_0_TYPE("contacts:0:contactType"),
    CONTACTS_0_EMAIL("contacts:0:emailAddress"),
    CONTACTS_0_GIVENNAME("contacts:0:givenName"),
    CONTACTS_0_SURNAME("contacts:0:surName"),
    CONTACTS_0_TELEPHONE("contacts:0:telephoneNumber"),
    CONTACTS_1_TYPE("contacts:1:contactType"),
    CONTACTS_1_EMAIL("contacts:1:emailAddress"),
    CONTACTS_1_GIVENNAME("contacts:1:givenName"),
    CONTACTS_1_SURNAME("contacts:1:surName"),
    CONTACTS_1_TELEPHONE("contacts:1:telephoneNumber"),
    CONTACTS_2_TYPE("contacts:2:contactType"),
    CONTACTS_2_EMAIL("contacts:2:emailAddress"),
    CONTACTS_2_GIVENNAME("contacts:2:givenName"),
    CONTACTS_2_SURNAME("contacts:2:surName"),
    CONTACTS_2_TELEPHONE("contacts:2:telephoneNumber"),
    EULA("coin:eula"),
    SS_IDP_VISIBLE_ONLY("coin:ss:idp_visible_only"),
    WORKFLOWSTATE("workflowState"),
    URL_EN("url:en"),
    URL_NL("url:nl"),
    APPLICATION_URL("coin:application_url"),
    PUBLISHED_EDU_GAIN("coin:publish_in_edugain");

    private String val;

    public String val() {
      return val;
    }

    Metadata(String val) {
      this.val = val;
    }
  }

  /**
   * Get a client's metadata by his entityId.
   *
   * @param entityId the entityId
   */
  EntityMetadata getMetadataByEntityId(String entityId);

  /**
   * Refer to {@link Janus#getAllowedSps(String, String)} but without the revision parameter.
   */
  List<String> getAllowedSps(String idpentityid);

  /**
   * Get a list of SP entity ID's that are allowed for this IdP.
   *
   * @param idpentityid the IdPs entity id.
   * @param revision    the revision.
   * @return List of entity id's from service providers the idp is linked to
   */
  List<String> getAllowedSps(String idpentityid, String revision);

  /**
   * Get a list of Idps linked to this sp
   */
  List<String> getAllowedIdps(String spentityid);

  /**
   * Retrieves a list of all Service Providers.
   *
   * @return for each SP (by entity id), a map of attributes and its values.
   */
  List<EntityMetadata> getSpList();

  /**
   * Retrieves a list of all Identity Providers.
   *
   * @return for each IDP (by entity id), a map of attributes and its values.
   */
  List<EntityMetadata> getIdpList();

  /**
   * Retrieves the Attribute Release Policy for this entity
   *
   * @param entityId the identifier of the entity
   * @return ARP
   */
  ARP getArp(String entityId);

  /**
   * Return {@literal true} or {@literal false} depending wether connection is allowed or not.
   *
   * @param spEntityId  identifier of the Service Provider
   * @param idpEntityId identifier of the Identity Provider
   * @return {@literal true} of the connection is allowed, otherwise {@literal false}
   */
  boolean isConnectionAllowed(String spEntityId, String idpEntityId);

  /**
   * Gets the Janus Entity information for the given entityId
   *
   * @param entityId unique identifier of a Janus entity
   * @return {@link JanusEntity}
   */
  JanusEntity getEntity(String entityId);


}
