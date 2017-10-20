package selfservice.service.impl;

import selfservice.domain.Group;
import selfservice.service.VootClient;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class VootClientMock implements VootClient {

  public static final String CSA_ADMIN = "admin";

  @Override
  public boolean hasAccess(String personId, String groupId) {
    return personId.endsWith(CSA_ADMIN);
  }

  @Override
  public List<Group> groups(final String userId) {
    switch (userId) {
      case "super":
        return asList(new Group("dashboard.super.user"));
      case "admin":
        return asList(new Group("dashboard.admin"));
      case "viewer":
        return asList(new Group("dashboard.viewer"));
      case "csa":
        return asList(new Group("csa.admins"));
      default:
        return Collections.emptyList();
    }
  }
}
