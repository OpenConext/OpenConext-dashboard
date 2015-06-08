package nl.surfnet.coin.selfservice.shibboleth;


import com.google.common.collect.ImmutableMap;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.service.Csa;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

public class ShibbolethPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

  public static Map<String, String> shibHeaders;

  static {
    shibHeaders = ImmutableMap.<String, String>builder()
      .put("urn:mace:dir:attribute-def:uid", "name-id")
      .put("urn:mace:dir:attribute-def:sn", "Shib-uid")
      .put("urn:mace:dir:attribute-def:surName", "Shib-surName")
      .put("urn:mace:dir:attribute-def:givenName", "Shib-givenName")
      .put("urn:mace:dir:attribute-def:cn", "Shib-commonName")
      .put("urn:mace:dir:attribute-def:displayName", "Shib-displayName")
      .put("urn:mace:dir:attribute-def:mail", "Shib-email")
      .put("urn:mace:dir:attribute-def:eduPersonAffiliation", "Shib-eduPersonAffiliation")
      .put("urn:mace:dir:attribute-def:eduPersonEntitlement", "Shib-eduPersonEntitlement")
      .put("urn:mace:dir:attribute-def:eduPersonPrincipalName", "Shib-eduPersonPN")
      .put("urn:mace:dir:attribute-def:preferredLanguage", "Shib-preferredLanguage")
      .put("urn:mace:terena.org:attribute-def:schacHomeOrganization", "Shib-homeOrg")
      .put("urn:mace:terena.org:attribute-def:schacHomeOrganizationType", "Shib-schacHomeOrganizationType")
      .put("urn:mace:surffederatie.nl:attribute-def:nlEduPersonHomeOrganization", "Shib-nlEduPersonHomeOrganization")
      .put("urn:mace:surffederatie.nl:attribute-def:nlEduPersonStudyBranch", "Shib-nlEduPersonStudyBranch")
      .put("urn:mace:surffederatie.nl:attribute-def:nlStudielinkNummer", "Shib-nlStudielinkNummer")
      .put("urn:mace:surffederatie.nl:attribute-def:nlDigitalAuthorIdentifier", "Shib-nlDigitalAuthorIdentifier")
      .put("urn:mace:surffederatie_nl:attribute-def:nlEduPersonHomeOrganization", "Shib-nlEduPersonHomeOrganization")
      .put("urn:mace:surffederatie_nl:attribute-def:nlEduPersonOrgUnit", "Shib-nlEduPersonOrgUnit")
      .put("urn:mace:surffederatie_nl:attribute-def:nlEduPersonStudyBranch", "Shib-nlEduPersonStudyBranch")
      .put("urn:mace:surffederatie_nl:attribute-def:nlDigitalAuthorIdentifier", "Shib-nlDigitalAuthorIdentifier")
      .put("urn:oid:1.3.6.1.4.1.1076.20.100.10.10.1", "Shib-userStatus")
      .put("urn:oid:1.3.6.1.4.1.5923.1.1.1.1", "Shib-accountstatus")
      .put("urn:oid:1.3.6.1.4.1.1076.20.100.10.10.2", "Shib-voName")
      .put("urn:oid:1.3.6.1.4.1.5923.1.5.1.1", "Shib-memberOf")
      .build();
  }

  private Csa csaClient;
  private Collection<String> shibKeys;

  public ShibbolethPreAuthenticatedProcessingFilter(AuthenticationManager authenticationManager, Csa csaClient) {
    super();
    setAuthenticationManager(authenticationManager);
    this.csaClient = csaClient;
    this.shibKeys = shibHeaders.values();
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

    Map<String, List<String>> attributes = shibKeys.stream().filter(h -> StringUtils.hasText(request.getHeader(h))).collect(Collectors.toMap(h -> h, h -> Arrays.asList(request.getHeader(h))));
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

}
