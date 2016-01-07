package selfservice.service.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

public class LmngUtilTest {

  private LmngUtil subject = new LmngUtil();

  @Test
  public void testGetLmngSoapRequestForIdpAndSp() throws Exception {
    String endpoint = "https://crmproxy.surfmarket.nl/crmservice.svc";
    String soap = subject.getLmngSoapRequestForIdpAndSp("{D9CE3927-3810-DC11-A6C7-0019B9DE3AA4}", Arrays.asList("{509925FE-E08B-E211-8A6D-0050569E0013}"), new Date(), endpoint, CrmUtil.LicenseRetrievalAttempt.One);
    assertTrue(soap.contains(endpoint));
  }
}
