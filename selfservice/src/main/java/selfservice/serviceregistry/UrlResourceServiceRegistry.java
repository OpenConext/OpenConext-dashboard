package selfservice.serviceregistry;

import org.springframework.core.io.Resource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

public class UrlResourceServiceRegistry extends ClassPathResourceServiceRegistry {

  private final String idpRemotePath;
  private final String spRemotePath;

  private final RestTemplate restTemplate = new RestTemplate();
  private final int period;
  private final BasicAuthenticationUrlResource idpUrlResource;
  private final BasicAuthenticationUrlResource spUrlResource;

  public UrlResourceServiceRegistry(
    String username,
    String password,
    String idpRemotePath,
    String spRemotePath,
    int period,
    Resource singleTenantsConfigPath) {
    super(false, singleTenantsConfigPath);

    try {
      this.idpUrlResource = new BasicAuthenticationUrlResource(idpRemotePath, username, password);
      this.spUrlResource = new BasicAuthenticationUrlResource(spRemotePath, username, password);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    this.idpRemotePath = idpRemotePath;
    this.spRemotePath = spRemotePath;
    this.period = period;

    SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
    requestFactory.setConnectTimeout(5 * 1000);

    newScheduledThreadPool(1).scheduleAtFixedRate(this::initializeMetadata, period, period, TimeUnit.MINUTES);
    super.initializeMetadata();
  }

  @Override
  protected Resource getIdpResource() {
    LOG.debug("Fetching IDP metadata entries from {}", idpRemotePath);
    return idpUrlResource;
  }

  @Override
  protected Resource getSpResource() {
    LOG.debug("Fetching SP metadata entries from {}", spRemotePath);
    return spUrlResource;
  }

  @Override
  protected void initializeMetadata() {
    try {
      if (spUrlResource.isModified(period) || idpUrlResource.isModified(period) ) {
        super.initializeMetadata();
      } else {
        LOG.debug("Not refreshing SP metadata. Not modified");
      }
    } catch (IOException e) {
      /*
       * By design we catch the error and not rethrow it.
       * UrlResourceServiceRegistry has timing issues when the server reboots and required endpoints are not available yet.
       */
      LOG.error("Error in refreshing / initializing metadata", e);
    }
  }


}
