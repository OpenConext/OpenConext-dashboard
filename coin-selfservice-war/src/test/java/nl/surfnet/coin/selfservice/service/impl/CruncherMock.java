package nl.surfnet.coin.selfservice.service.impl;

import java.io.IOException;
import java.util.*;

import nl.surfnet.coin.oauth.OauthClient;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.*;
import org.springframework.core.io.ClassPathResource;
import org.surfnet.cruncher.Cruncher;
import org.surfnet.cruncher.model.LoginData;
import org.surfnet.cruncher.model.SpStatistic;

public class CruncherMock implements Cruncher {

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
          .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  public CruncherMock(String cruncherBaseLocation) {
  }

  @Override
  public String getLogins(Date startDate, Date endDate) {
    return getLoginStatsByIdpSp();
  }

  @Override
  public String getLoginsByIdpAndSp(Date startDate, Date endDate, String idpEntityId, String spEntityId) {
    LoginData loginData = new LoginData();
    loginData.setIdpEntityId(idpEntityId);
    loginData.setSpEntityId(spEntityId);
    loginData.setPointStart(startDate.getTime());
    loginData.setPointEnd(endDate.getTime());
    loginData.setPointInterval(Duration.standardDays(1).getMillis());
    loginData.setTotal(Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays());
    List<Integer> data = loginData.getData();
    data.clear();

    Random random = new Random();
    for (int i = 0; i < loginData.getTotal(); i++) {
      data.add(random.nextInt(20) + 50);
    }

    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(Arrays.asList(loginData));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getLoginsByIdp(Date startDate, Date endDate, String idpEntityId) {
    return getLoginStatsByIdpSp();
  }

  @Override
  public String getLoginsBySp(Date startDate, Date endDate, String spEntityId) {
    return getLoginStatsByIdpSp();
  }

  @Override
  public List<SpStatistic> getRecentLoginsForUser(String s, String s2) {
    try {
      return objectMapper.readValue(new ClassPathResource("stat-json/last-login.json").getInputStream(), new TypeReference<List<SpStatistic>>() {});
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String getLoginStatsByIdpSp() {
    try {
      return IOUtils.toString(new ClassPathResource("stat-json/stats-idp-sp.json").getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setOauthClient(OauthClient oc) {
  }
}
