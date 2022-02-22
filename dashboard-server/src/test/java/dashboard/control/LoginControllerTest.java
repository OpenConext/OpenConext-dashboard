package dashboard.control;

import dashboard.AbstractTest;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.Charset;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginControllerTest extends AbstractTest {

    @Test
    void login() {
        Headers headers = given()
                .when()
                .redirects().follow(false)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .queryParam("redirect_url", "http://localhost/redirect")
                .queryParam("loa", "3")
                .get("/login")
                .headers();
        String location = headers.get("Location").getValue();
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(location).build();
        String path = uriComponents.getPath();
        MultiValueMap<String, String> queryParams = uriComponents.getQueryParams();
        String target = URLDecoder.decode(queryParams.getFirst("target"), Charset.defaultCharset());
        String authnContextClassRef = URLDecoder.decode(queryParams.getFirst("authnContextClassRef"), Charset.defaultCharset());

        assertEquals("/Shibboleth.sso/Login", path);
        assertEquals("/startSSO?redirect_url=http://localhost/redirect", target);
        assertEquals("http://test2.surfconext.nl/assurance/loa3", authnContextClassRef);
    }

    @Test
    void startSSO() {
        String redirectUrl = "http://localhost:3000/redirect";
        Headers headers = given()
                .when()
                .redirects().follow(false)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .queryParam("redirect_url", redirectUrl)
                .get("/startSSO")
                .headers();
        String location = headers.get("Location").getValue();
        assertEquals(redirectUrl, location);
    }

    @Test
    void startSSOOpenRedirect() {
        String redirectUrl = "http://nope";
        given()
                .when()
                .redirects().follow(false)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .queryParam("redirect_url", redirectUrl)
                .get("/startSSO")
                .then()
                .statusCode(400);
    }
}