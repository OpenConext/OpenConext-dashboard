package selfservice.service;

import java.util.List;

import selfservice.domain.Policy;
import selfservice.domain.Policy.Attribute;

public interface PdpService {

  List<Policy> policies();

  Policy policy(Long id);

  Policy create(Policy policy);

  Policy update(Policy policy);

  List<Attribute> allowedAttributes();

  void delete(Long id);

}
