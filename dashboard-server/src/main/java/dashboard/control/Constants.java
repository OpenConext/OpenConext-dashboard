package dashboard.control;

import dashboard.domain.CoinUser;
import dashboard.domain.IdentityProvider;
import dashboard.util.SpringSecurity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import java.util.Base64;


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
        return "Basic " + new String(Base64.getEncoder().encode(String.format("%s:%s", username, password).getBytes()));
    }

    default String currentUserIdp() {
        CoinUser user = SpringSecurity.getCurrentUser();
        IdentityProvider idp = user.getSwitchedToIdp().orElse(user.getIdp());
        return idp.getId();
    }


}
