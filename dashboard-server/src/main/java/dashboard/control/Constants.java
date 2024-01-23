package dashboard.control;

import com.google.common.collect.ImmutableList;
import dashboard.domain.CoinUser;
import dashboard.domain.IdentityProvider;
import dashboard.util.SpringSecurity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;


public interface Constants {

    String HTTP_X_IDP_ENTITY_ID = "X-IDP-ENTITY-ID";

    String X_IDP_ENTITY_ID = HTTP_X_IDP_ENTITY_ID;;
    String X_UNSPECIFIED_NAME_ID = "X-UNSPECIFIED-NAME-ID";
    String X_DISPLAY_NAME = "X-DISPLAY-NAME";

    default ClientHttpRequestFactory clientHttpRequestFactory(int timeout) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(timeout);
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setHttpClient(HttpClients.custom()
                .disableCookieManagement().build());

        return requestFactory;
    }

    default ClientHttpRequestInterceptor clientHttpRequestInterceptor(String username, String password) {
        return (request, body, execution) -> {
            CoinUser user = SpringSecurity.getCurrentUser();

            IdentityProvider idp = user.getSwitchedToIdp().orElse(user.getIdp());

            HttpHeaders headers = request.getHeaders();
            headers.setContentType(APPLICATION_JSON);
            headers.setAccept(ImmutableList.of(APPLICATION_JSON));
            headers.set(AUTHORIZATION, authorizationHeaderValue(username, password));
            headers.set(X_IDP_ENTITY_ID, idp.getId());
            headers.set(X_UNSPECIFIED_NAME_ID, user.getUid());
            headers.set(X_DISPLAY_NAME, user.getDisplayName());

            return execution.execute(request, body);
        };
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
