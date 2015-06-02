package nl.surfnet.coin.selfservice.shibboleth;


import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.service.Csa;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShibbolethPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

  private Csa csaClient;
  private List<String> shibHeaders;

  public ShibbolethPreAuthenticatedProcessingFilter(AuthenticationManager authenticationManager, Csa csaClient) {
    super();
    setAuthenticationManager(authenticationManager);
    this.csaClient = csaClient;
    this.shibHeaders = shibHeaders();
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
    String uid = request.getHeader("Shib-uid");
    if (!StringUtils.hasText(uid)) {
      throw new IllegalArgumentException("Header must include Shib-uid");
    }
    final String idpId = request.getHeader("Shib-Authenticating-Authority");
    List<InstitutionIdentityProvider> institutionIdentityProviders = csaClient.getInstitutionIdentityProviders(idpId);

    CoinUser coinUser = new CoinUser();
    coinUser.setUid(uid);
    coinUser.setDisplayName(request.getHeader("Shib-displayName"));
    coinUser.setEmail(request.getHeader("Shib-email"));
    coinUser.setSchacHomeOrganization(request.getHeader("Shib-homeOrg"));

    Map<String, List<String>> attributes = shibHeaders.stream().filter(h -> StringUtils.hasText(request.getHeader(h))).collect(Collectors.toMap(h -> h, h -> Arrays.asList(request.getHeader(h))));
    coinUser.setAttributeMap(attributes);

    if (CollectionUtils.isEmpty(institutionIdentityProviders)) {
      //duhh, fail fast, big problems
      throw new IllegalArgumentException("Csa#getInstitutionIdentityProviders('" + idpId + "') returned zero result");
    }
    if (institutionIdentityProviders.size() == 1) {
      //most common case
      InstitutionIdentityProvider idp = institutionIdentityProviders.get(0);
      coinUser.setIdp(idp);
      coinUser.addInstitutionIdp(idp);
    } else {
      coinUser.setIdp(getCurrentIdp(idpId, institutionIdentityProviders));
      coinUser.getInstitutionIdps().addAll(institutionIdentityProviders);
      Collections.sort(coinUser.getInstitutionIdps(), (lh, rh) -> lh.getName().compareTo(rh.getName()));
    }
    return coinUser;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

  private InstitutionIdentityProvider getCurrentIdp(String idpId, List<InstitutionIdentityProvider> institutionIdentityProviders) {
    for (InstitutionIdentityProvider provider : institutionIdentityProviders) {
      if (provider.getId().equals(idpId)) {
        return provider;
      }
    }
    throw new IllegalArgumentException("The Idp('" + idpId + "') is not present in the list of Idp's returned by the CsaClient");
  }

  /**
   * List of attributes from Shibboleth that will be filtered in. If attributes are not in this list they will NOT be shown.
   */
  private List<String> shibHeaders() {
    return Arrays.asList(
      "name-id",
      "Shib-uid",
      "Shib-surName",
      "Shib-givenName",
      "Shib-commonName",
      "Shib-displayName",
      "Shib-email",
      "Shib-eduPersonAffiliation",
      "Shib-eduPersonEntitlement",
      "Shib-eduPersonPN",
      "Shib-preferredLanguage",
      "Shib-homeOrg",
      "Shib-schacHomeOrganizationType",
      "Shib-nlEduPersonHomeOrganization",
      "Shib-nlEduPersonStudyBranch",
      "Shib-nlStudielinkNummer",
      "Shib-nlDigitalAuthorIdentifier",
      "Shib-nlEduPersonHomeOrganization",
      "Shib-nlEduPersonOrgUnit",
      "Shib-nlEduPersonStudyBranch",
      "Shib-nlStudielinkNummer",
      "Shib-nlDigitalAuthorIdentifier",
      "Shib-userStatus",
      "Shib-accountstatus",
      "Shib-voName",
      "Shib-memberOf");
  }
}
