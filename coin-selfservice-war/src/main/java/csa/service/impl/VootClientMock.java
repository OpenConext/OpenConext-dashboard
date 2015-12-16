package csa.service.impl;

import csa.service.VootClient;

public class VootClientMock implements VootClient {

  public static final String CSA_ADMIN = "admin";

  public VootClientMock() {
  }

  @Override
  public boolean hasAccess(String personId, String groupId) {
    return personId.endsWith(CSA_ADMIN);
  }

}
