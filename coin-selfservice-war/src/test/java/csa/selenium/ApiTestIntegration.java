package csa.selenium;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import csa.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
@ActiveProfiles("dev")
public class ApiTestIntegration {

  private static final String apiUrl = "/api/public/services.json";

  @Value("${local.server.port}")
  private int port;

  @Test
  public void getServices() throws Exception{
    CloseableHttpClient httpclient = HttpClients.createDefault();

    HttpGet httpget = new HttpGet("http://localhost:" + port + apiUrl); // get services json

    final CloseableHttpResponse httpResponse = httpclient.execute(httpget);
    assertEquals(httpResponse.getEntity().getContentType().getValue(), "application/json;charset=UTF-8");
    final String jsonResponse = IOUtils.toString(httpResponse.getEntity().getContent());
    assertThat(jsonResponse, CoreMatchers.containsString("8833CEAE-960C-E211-B6B9-005056950050"));
  }

}
