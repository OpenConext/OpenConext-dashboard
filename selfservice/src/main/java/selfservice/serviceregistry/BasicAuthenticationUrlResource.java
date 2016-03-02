package selfservice.serviceregistry;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.Base64.getEncoder;
import static org.springframework.http.HttpHeaders.IF_MODIFIED_SINCE;

public class BasicAuthenticationUrlResource extends UrlResource {

  private static final ZoneId GMT = ZoneId.of("GMT");
  private final String basicAuth;

  public BasicAuthenticationUrlResource(String path, String username, String password) throws MalformedURLException {
    super(path);
    this.basicAuth = "Basic " + new String(getEncoder().encode((username + ":" + password).getBytes()));
  }

  @Override
  public InputStream getInputStream() throws IOException {
    URLConnection con = this.getURL().openConnection();
    setHeaders(con);
    return con.getInputStream();
  }

  public boolean isModified(int minutes) throws IOException {
      HttpURLConnection con = (HttpURLConnection) this.getURL().openConnection();
      con.setRequestMethod("HEAD");
      setHeaders(con);

      String lastRefresh = RFC_1123_DATE_TIME.format(ZonedDateTime.now(GMT).minusMinutes(minutes));
      con.setRequestProperty(IF_MODIFIED_SINCE, lastRefresh);

      int responseCode = con.getResponseCode();
      return responseCode != HttpStatus.NOT_MODIFIED.value();
  }

  protected void setHeaders(URLConnection con) {
    con.setRequestProperty("Authorization", basicAuth);
    con.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
    con.setConnectTimeout(5 * 1000);
  }

}
