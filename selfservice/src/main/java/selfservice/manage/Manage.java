package selfservice.manage;

import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;

import java.util.List;
import java.util.Optional;

public interface Manage {

  /**
   * Get a list of all available Service Providers for the given idpId.
   *
   * @param idpId the IDP entity ID to filter on
   * @return list of {@link ServiceProvider}
   */
  List<ServiceProvider> getAllServiceProviders(String idpId);

  /**
   * Get a {@link ServiceProvider} by its entity ID.
   *
   * @param spEntityId the entity id of the ServiceProvider
   * @param idpEntityId the entity id of the Identity Provider.
   * @return the {@link ServiceProvider} object.
   */
  ServiceProvider getServiceProvider(String spEntityId, String idpEntityId);

  /**
   * Get a {@link ServiceProvider} by its entity ID, without a idpEntityId
   *
   * @param spEntityId the entity id of the ServiceProvider
   * @return the {@link ServiceProvider} object.
   */
  Optional<ServiceProvider> getServiceProvider(String spEntityId);

  /**
   * Get a list of all available Service Providers (IDP independent).
   *
   * @return list of {@link ServiceProvider}
   */
  List<ServiceProvider> getAllServiceProviders();

  void refreshMetaData();

  /**
   * Get an identity provider by its id.
   * @param idpEntityId the id.
   * @return IdentityProvider
   */
  Optional<IdentityProvider> getIdentityProvider(String idpEntityId);

  /**
   * Get a list of all idps that have the same instituteId as the given one.
   * @param instituteId the instituteId
   * @return List&lt;IdentityProvider&gt;
   */
  List<IdentityProvider> getInstituteIdentityProviders(String instituteId);

  /**
   * Get a list of all idps
   * @return List&lt;IdentityProvider&gt;
   */
  List<IdentityProvider> getAllIdentityProviders();

  /**
   * Get a list of all idps connected to a SP
   * @return List&lt;IdentityProvider&gt;
   */
  List<IdentityProvider> getLinkedIdentityProviders(String spId);


  /**
   * Get a list of all SP identifiers linked to the Idp
   * @return List&lt;String&gt;
   */
  List<String> getLinkedServiceProviderIDs(String idpId);
}
