package selfservice.shibboleth;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.StringUtils.hasText;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.service.IdentityProviderService;

public class ShibbolethPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

  private static final Splitter shibHeaderValueSplitter = Splitter.on(';').omitEmptyStrings();

  public static final Map<String, String> shibHeaders;

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

  private final IdentityProviderService idpService;

  public ShibbolethPreAuthenticatedProcessingFilter(AuthenticationManager authenticationManager, IdentityProviderService idpService) {
    setAuthenticationManager(authenticationManager);
    this.idpService = idpService;
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
    String uid = getFirstShibHeaderValue("name-id", request)
        .orElseThrow(() -> new IllegalArgumentException("Missing name-id Shibboleth header"));

    String idpId = getFirstShibHeaderValue("Shib-Authenticating-Authority", request)
        .orElseThrow(() -> new IllegalArgumentException("Missing Shib-Authenticating-Authority Shibboleth header"));

    CoinUser coinUser = new CoinUser();
    coinUser.setUid(uid);
    coinUser.setDisplayName(getFirstShibHeaderValue("Shib-displayName", request).orElse(null));
    coinUser.setEmail(getFirstShibHeaderValue("Shib-email", request).orElse(null));
    coinUser.setSchacHomeOrganization(getFirstShibHeaderValue("Shib-homeOrg", request).orElse(null));

    Map<String, List<String>> attributes = shibHeaders.values().stream()
        .filter(h -> StringUtils.hasText(request.getHeader(h)))
        .collect(toMap(h -> h, h -> getShibHeaderValues(h, request)));
    coinUser.setAttributeMap(attributes);

    List<IdentityProvider> institutionIdentityProviders = getInstitutionIdentityProviders(idpId);

    checkState(!CollectionUtils.isEmpty(institutionIdentityProviders), "no InstitutionIdentityProviders found for '" + idpId + "'");

    if (institutionIdentityProviders.size() == 1) {
      IdentityProvider idp = institutionIdentityProviders.get(0);
      coinUser.setIdp(idp);
      coinUser.addInstitutionIdp(idp);
    } else {
      coinUser.setIdp(getCurrentIdp(idpId, institutionIdentityProviders));
      coinUser.getInstitutionIdps().addAll(institutionIdentityProviders);
      Collections.sort(coinUser.getInstitutionIdps(), (lh, rh) -> lh.getName().compareTo(rh.getName()));
    }

    return coinUser;
  }

  private List<IdentityProvider> getInstitutionIdentityProviders(String idpId) {
    return idpService.getIdentityProvider(idpId).map(idp -> {
      String institutionId = idp.getInstitutionId();
      return hasText(institutionId) ? idpService.getInstituteIdentityProviders(institutionId) : singletonList(idp);
    }).orElse(Collections.emptyList());
  }

  private Optional<String> getFirstShibHeaderValue(String name, HttpServletRequest request) {
    return getShibHeaderValues(name, request).stream().findFirst();
  }

  private List<String> getShibHeaderValues(String name, HttpServletRequest request) {
    String headerValue = request.getHeader(name);

    return headerValue == null ? Collections.emptyList() : shibHeaderValueSplitter.splitToList(headerValue);
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

  private IdentityProvider getCurrentIdp(String idpId, List<IdentityProvider> institutionIdentityProviders) {
    return institutionIdentityProviders.stream()
        .filter(provider -> provider.getId().equals(idpId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(String.format("The Idp('%s') is not present in the list of Idp's returned by the CsaClient", idpId)));
  }

}
