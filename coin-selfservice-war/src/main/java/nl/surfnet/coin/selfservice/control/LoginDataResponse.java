package nl.surfnet.coin.selfservice.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.surfnet.cruncher.model.LoginData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LoginDataResponse {
  private final String filename;
  private final LoginData[] loginDatas;

  public LoginDataResponse(String statistics, String filename) {
    try {
      loginDatas = new ObjectMapper().readValue(statistics, LoginData[].class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }

  public List<LoginData> getLoginData() {
    return Arrays.asList(loginDatas);
  }
}
