package nl.surfnet.coin.selfservice.service.impl;

import nl.surfnet.coin.selfservice.domain.Group;
import nl.surfnet.coin.selfservice.service.VootClient;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class VootClientMock implements VootClient {

  public VootClientMock(String accessTokenUri, String clientId, String clientSecret, String spaceDelimitedScopes, String serviceUrl) {}

  @Override
  public List<Group> groups(final String userId) {
    switch (userId) {
      case "admin" : return asList(new Group("dashboard.admin"));
      case "viewer" : return asList(new Group("dashboard.viewer"));
      case "super" : return asList(new Group("dashboard.super.user"));
      default : return new ArrayList<Group>();
    }
  }
}
