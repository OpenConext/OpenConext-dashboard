package selfservice.service;

import selfservice.domain.Group;

import java.util.List;

public interface VootClient {

  List<Group> groups(String userId);
}
