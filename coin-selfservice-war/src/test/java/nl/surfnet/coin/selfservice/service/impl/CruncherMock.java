package nl.surfnet.coin.selfservice.service.impl;

import java.io.IOException;
import java.util.*;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Service;
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

import javax.annotation.Resource;

public class CruncherMock implements Cruncher {

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
          .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  @Resource
  private Csa csa;

  public CruncherMock(String cruncherBaseLocation) {
  }

  @Override
  public String getLoginsByIdpAndSp(Date startDate, Date endDate, String idpEntityId, String spEntityId) {
    Service service = csa.getServiceForIdp(idpEntityId, spEntityId);
    LoginData loginData = generateLoginData(startDate, endDate, idpEntityId, service);

    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(Arrays.asList(loginData));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getLoginsByIdp(final Date startDate, final Date endDate, final String idpEntityId) {
    List<Service> services = csa.getServicesForIdp(idpEntityId);

    List<LoginData> loginData = Lists.transform(services, new Function<Service, LoginData>() {
      @Override
      public LoginData apply(Service service) {
        return generateLoginData(startDate, endDate, idpEntityId, service);
      }
    });

    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(loginData);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private LoginData generateLoginData(Date startDate, Date endDate, String idpEntityId, Service service) {
    LoginData loginData = new LoginData();
    loginData.setSpName(service.getSpName());
    loginData.setIdpEntityId(idpEntityId);
    loginData.setSpEntityId(service.getSpEntityId());
    loginData.setPointStart(startDate.getTime());
    loginData.setPointEnd(endDate.getTime());
    loginData.setPointInterval(Duration.standardDays(1).getMillis());
    loginData.setTotal(0);
    List<Integer> data = loginData.getData();
    data.clear();

    Random random = new Random();
    for (int i = 0; i < Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays(); i++) {
      data.add(random.nextInt(20) + 50);
    }

    return loginData;
  }

  @Override
  public String getLogins(Date startDate, Date endDate) {
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
