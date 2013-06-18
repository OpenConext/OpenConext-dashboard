package nl.surfnet.coin.selfservice.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import nl.surfnet.coin.oauth.OauthClient;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
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
    ObjectMapper mapper = new ObjectMapper();
    try {
      List<LoginData> result = result = mapper.readValue(getLoginStatsByIdpSp(), new TypeReference<List<LoginData>>() { });
      Iterator<LoginData> iter = result.iterator();
      while (iter.hasNext()) {
        if (!(iter.next().getSpEntityId().equals(spEntityId))) {
          iter.remove();
        }
      }
      return mapper.writeValueAsString(result);
    } catch (JsonParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return getLoginStatsByIdpSp();
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
