package selfservice.shibboleth;


import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import selfservice.domain.CoinUser;
import selfservice.domain.InstitutionIdentityProvider;
import selfservice.service.Csa;

public class ShibbolethPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

  public static Map<String, String> shibHeaders;

  static {
    shibHeaders = ImmutableMap.<String, String>builder()
      .put("urn:mace:dir:attribute-def:uid", "Shib-uid")
      .put("urn:mace:dir:attribute-def:sn", "Shib-surName")
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
      .put("urn:mace:surffederatie_nl:attribute-def:nlEduPersonOrgUnit", "Shib-nlEduPersonOrgUnit")
      .put("urn:oid:1.3.6.1.4.1.1076.20.100.10.10.1", "Shib-userStatus")
      .put("urn:oid:1.3.6.1.4.1.5923.1.1.1.1", "Shib-accountstatus")
      .put("urn:oid:1.3.6.1.4.1.1076.20.100.10.10.2", "Shib-voName")
      .put("urn:oid:1.3.6.1.4.1.5923.1.5.1.1", "Shib-memberOf")
      .build();
  }

  private Csa csaClient;

  public ShibbolethPreAuthenticatedProcessingFilter(AuthenticationManager authenticationManager, Csa csaClient) {
    setAuthenticationManager(authenticationManager);
    this.csaClient = csaClient;
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
    String uid = request.getHeader("name-id");

    checkArgument(StringUtils.hasText(uid), "Header must include name-id");

    String idpId = request.getHeader("Shib-Authenticating-Authority");
    //it can happen that the Authenticating Authority looks like this: http://mock-idp;http://mock-idp
    idpId = idpId.split(";")[0];

    CoinUser coinUser = new CoinUser();
    coinUser.setUid(uid);
    coinUser.setDisplayName(request.getHeader("Shib-displayName"));
    coinUser.setEmail(request.getHeader("Shib-email"));
    coinUser.setSchacHomeOrganization(request.getHeader("Shib-homeOrg"));

    Map<String, List<String>> attributes = shibHeaders.values().stream()
        .filter(h -> StringUtils.hasText(request.getHeader(h)))
        .collect(toMap(h -> h, h -> asList(request.getHeader(h))));
    coinUser.setAttributeMap(attributes);

    List<InstitutionIdentityProvider> institutionIdentityProviders = csaClient.getInstitutionIdentityProviders(idpId);

    checkArgument(!CollectionUtils.isEmpty(institutionIdentityProviders), "Csa#getInstitutionIdentityProviders('" + idpId + "') returned zero result");

    if (institutionIdentityProviders.size() == 1) {
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
    return institutionIdentityProviders.stream()
        .filter(provider -> provider.getId().equals(idpId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("The Idp('" + idpId + "') is not present in the list of Idp's returned by the CsaClient"));
  }

}
