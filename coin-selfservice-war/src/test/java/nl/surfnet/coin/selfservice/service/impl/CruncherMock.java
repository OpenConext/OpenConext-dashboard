package nl.surfnet.coin.selfservice.service.impl;

import nl.surfnet.coin.oauth.OauthClient;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.surfnet.cruncher.Cruncher;
import org.surfnet.cruncher.model.SpStatistic;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class CruncherMock implements Cruncher {

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
          .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  public CruncherMock(String cruncherBaseLocation) {
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
    try {
      return objectMapper.readValue(new ClassPathResource("stat-json/last-login.json").getInputStream(), new TypeReference<List<SpStatistic>>() {});
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String getLoginStats() {
    try {
      return IOUtils.toString(new ClassPathResource("stat-json/stats.json").getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setOauthClient(OauthClient oc) {
  }
}
