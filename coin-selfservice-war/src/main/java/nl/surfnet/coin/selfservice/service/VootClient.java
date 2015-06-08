package nl.surfnet.coin.selfservice.service;

import nl.surfnet.coin.selfservice.domain.Group;

import java.util.List;

public interface VootClient {

  List<Group> groups(String userId);
}
