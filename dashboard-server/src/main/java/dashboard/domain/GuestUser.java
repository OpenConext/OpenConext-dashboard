package dashboard.domain;

import static dashboard.domain.CoinAuthority.Authority.ROLE_DASHBOARD_GUEST;

public class GuestUser extends CoinUser {

    public GuestUser() {
        this.addAuthority(new CoinAuthority(ROLE_DASHBOARD_GUEST));
        this.setGuest(true);
    }


}
