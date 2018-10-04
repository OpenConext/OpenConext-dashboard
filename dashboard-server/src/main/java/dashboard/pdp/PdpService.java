package dashboard.pdp;

import java.util.List;

import org.springframework.http.ResponseEntity;
import dashboard.domain.Policy;
import dashboard.domain.Policy.Attribute;

public interface PdpService {

  List<Policy> policies();

  Policy policy(Long id);

  Policy create(Policy policy) ;

  Policy update(Policy policy);

  List<Attribute> allowedAttributes();

  ResponseEntity<String> delete(Long id);

  List<Policy> revisions(Long id);

  boolean isAvailable();

}
