package dashboard.domain;

import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@SuppressWarnings("serial")
public class CoinAuthentication extends PreAuthenticatedAuthenticationToken {

  private CoinUser coinUser;

  public CoinAuthentication(CoinUser coinUser) {
    super(coinUser, "N/A", coinUser.getAuthorities());
    this.coinUser = coinUser;
  }


  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return coinUser;
  }
}
