package nl.surfnet.coin.selfservice.control;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.surfnet.cruncher.model.LoginData;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class LoginDataResponseTest {
  @Test
  public void testParseJson() throws Exception {
    LoginDataResponse loginDataResponse = new LoginDataResponse(IOUtils.toString(getClass().getResourceAsStream("/stat-json/stats-idp-sp.json")), "foo.csv");
    List<LoginData> loginDataList = loginDataResponse.getLoginData();
    assertEquals(7, loginDataList.size());
  }
}