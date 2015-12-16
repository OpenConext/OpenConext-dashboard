package csa.service;

public interface VootClient {

  boolean hasAccess(String personId, String groupId);

}
