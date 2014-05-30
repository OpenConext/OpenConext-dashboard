package nl.surfnet.coin.selfservice.control;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginDataToCsvTest {

  private LoginDataToCsv subject;
  private LoginDataResponse loginDataResponse;

  @Mock
  private HttpOutputMessage message;
  private HttpHeaders httpHeaders;

  @Before
  public void setUp() throws Exception {
    subject = new LoginDataToCsv();
    loginDataResponse = new LoginDataResponse(IOUtils.toString(getClass().getResourceAsStream("/stat-json/stats-idp-sp.json")), "foo.csv");
    httpHeaders = new HttpHeaders();
  }

  @Test
  public void testHeaderRow() throws Exception {
    LoginDataToCsv.HeaderRow headerRow = new LoginDataToCsv.HeaderRow(loginDataResponse.getLoginData());

    assertEquals(307, headerRow.toCsv().length);
  }

  @Test
  public void testWriteInternal() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(httpHeaders);

    subject.writeInternal(loginDataResponse, message);
    assertEquals(IOUtils.toString(getClass().getResourceAsStream("/stat-json/expected-csv.csv")), outputStream.toString());
  }

}