package csa.shibboleth;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import csa.Application;
import csa.domain.CoinAuthority;
import csa.domain.CoinUser;
import csa.domain.IdentityProvider;
import csa.janus.Janus;
import csa.janus.domain.JanusEntity;
import csa.service.IdentityProviderService;

public class RichUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

  private static final Logger LOG = LoggerFactory.getLogger(RichUserDetailsService.class);

  private final Environment environment;

  private final IdentityProviderService identityProviderService;

  private final Janus janusClient;

  public RichUserDetailsService(Environment environment, IdentityProviderService identityProviderService, Janus janusClient) {
    this.environment = environment;
    this.identityProviderService = identityProviderService;
    this.janusClient = janusClient;
  }

  @Override
  public CoinUser loadUserDetails(final PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
    ShibbolethPrincipal shibbolethPrincipal = (ShibbolethPrincipal) token.getPrincipal();

    CoinUser coinUser = new CoinUser();
    coinUser.setUid(shibbolethPrincipal.getUid());
    coinUser.setDisplayName(shibbolethPrincipal.getDisplayName());
    coinUser.setEmail(shibbolethPrincipal.getEmail());
    if (Arrays.asList(environment.getActiveProfiles()).contains(Application.DEV_PROFILE_NAME)) {
      setMockProperties(coinUser);
      return coinUser;
    }
    final String idpId = shibbolethPrincipal.getIdpId();
    LOG.debug("User logged in through IDP with id: {}", idpId);

    coinUser.setInstitutionId(getInstitutionId(idpId));

    List<IdentityProvider> instituteIdPs = identityProviderService.getInstituteIdentityProviders(coinUser.getInstitutionId());
    if (!instituteIdPs.isEmpty()) {
      for (IdentityProvider idp : instituteIdPs) {
        coinUser.addInstitutionIdp(idp);
      }
    }
    if (coinUser.getInstitutionIdps().isEmpty()) {
      IdentityProvider idp = getInstitutionIdP(idpId);
      coinUser.addInstitutionIdp(idp);
    }
    for (IdentityProvider idp : coinUser.getInstitutionIdps()) {
      if (idp.getId().equalsIgnoreCase(idpId)) {
        coinUser.setIdp(idp);
      }
    }

    return coinUser;
  }

  private String getInstitutionId(String idpId) {
    final IdentityProvider identityProvider = identityProviderService.getIdentityProvider(idpId);
    if (identityProvider != null) {
      final String institutionId = identityProvider.getInstitutionId();
      if (!StringUtils.isBlank(institutionId)) {
        return institutionId;
      }
    }
    return null;
  }

  private IdentityProvider getInstitutionIdP(String idpId) {
    IdentityProvider idp = identityProviderService.getIdentityProvider(idpId);
    if (idp == null) {
      final JanusEntity entity = janusClient.getEntity(idpId);
      if (entity == null) {
        idp = new IdentityProvider(idpId, null, idpId);
      } else {
        idp = new IdentityProvider(entity.getEntityId(), null, entity.getPrettyName());
      }
    }
    return idp;
  }

  private void setMockProperties(CoinUser coinUser) {
    final String institutionId = "mock-institution-id";
    coinUser.setInstitutionId(institutionId);

    coinUser.setAuthorities(Arrays.asList(new CoinAuthority(CoinAuthority.Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN)));
    final String idpId = "http://mock-idp";
    final IdentityProvider currentIdp = new IdentityProvider(idpId, institutionId, "Main IdP (name en)");
    final IdentityProvider otherIdp = new IdentityProvider("https://idp_with_all_but_one_sp", institutionId, "IdP with All SP's connected, but one (name en)");

    coinUser.addInstitutionIdp(currentIdp);
    coinUser.addInstitutionIdp(otherIdp);
    coinUser.setIdp(currentIdp);
  }
}
