  package nl.surfnet.coin.selfservice.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;

import nl.surfnet.coin.selfservice.service.Cruncher;

public class MockCruncherImpl implements Cruncher {

  @Override
  public String getLogins() {
    return getLoginStats();
  }

  @Override
  public String getLoginsByIdpAndSp(String idpEntityId, String spEntityId) {
    return getLoginStats();
  }

  @Override
  public String getLoginsByIdp(String idpEntityId) {
    return getLoginStats();
  }

  @Override
  public String getLoginsBySp(String spEntityId) {
    return getLoginStats();
  }

  private String getLoginStats() {
    ClassPathResource statsFile = new ClassPathResource("stat-json/stats.json");
    ByteArrayOutputStream output = null;
    try {
      InputStream input = statsFile.getInputStream();
      output = new ByteArrayOutputStream();
      while (input.available() > 0) {
        output.write(input.read());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return output.toString();
  }

}
