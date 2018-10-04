package dashboard.control;

import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.codec.Base64;
import dashboard.domain.CoinUser;
import dashboard.domain.IdentityProvider;
import dashboard.util.SpringSecurity;

public interface Constants {

    String HTTP_X_IDP_ENTITY_ID = "X-IDP-ENTITY-ID";

    default ClientHttpRequestFactory clientHttpRequestFactory(int timeout) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(timeout);
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setHttpClient(HttpClients.custom()
                .disableCookieManagement().build());

        return requestFactory;
    }

    default String authorizationHeaderValue(String username, String password) {
        return "Basic " + new String(Base64.encode(String.format("%s:%s", username, password).getBytes()));
    }

    default String currentUserIdp() {
        CoinUser user = SpringSecurity.getCurrentUser();
        IdentityProvider idp = user.getSwitchedToIdp().orElse(user.getIdp());
        return idp.getId();
    }


}
