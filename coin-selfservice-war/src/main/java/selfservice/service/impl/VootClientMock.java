package selfservice.service.impl;

import selfservice.domain.Group;
import selfservice.service.VootClient;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class VootClientMock implements VootClient {

  @Override
  public List<Group> groups(final String userId) {
    switch (userId) {
      case "admin":
        return asList(new Group("dashboard.admin"));
      case "viewer":
        return asList(new Group("dashboard.viewer"));
      case "super":
        return asList(new Group("dashboard.super.user"));
      default:
        return new ArrayList<>();
    }
  }
}
