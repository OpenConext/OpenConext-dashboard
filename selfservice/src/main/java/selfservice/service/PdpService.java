package selfservice.service;

import java.util.List;

import selfservice.domain.CoinUser;
import selfservice.domain.Policy;

public interface PdpService {

  List<Policy> allPolicies(CoinUser currentUser);

  Policy policy(Long id);
}
