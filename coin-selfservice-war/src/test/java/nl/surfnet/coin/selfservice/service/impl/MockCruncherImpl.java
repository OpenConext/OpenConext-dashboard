package nl.surfnet.coin.selfservice.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.surfnet.cruncher.Cruncher;
import org.surfnet.cruncher.model.SpStatistic;

public class MockCruncherImpl implements Cruncher {

  public MockCruncherImpl(String cruncherClientKey, String cruncherClientSecret, String cruncherBaseLocation, String apisOAuth2AuthorizationUrl) {
  }

  @Override
  public String getLogins(Date startDate, Date endDate) {
    return getLoginStats();
  }

  @Override
  public String getLoginsByIdpAndSp(Date startDate, Date endDate, String idpEntityId, String spEntityId) {
    return getLoginStats();
  }

  @Override
  public String getLoginsByIdp(Date startDate, Date endDate, String idpEntityId) {
    return getLoginStats();
  }

  @Override
  public String getLoginsBySp(Date startDate, Date endDate, String spEntityId) {
    return getLoginStats();
  }

  @Override
  public List<SpStatistic> getRecentLoginsForUser(String s, String s2) {
    return null;
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
