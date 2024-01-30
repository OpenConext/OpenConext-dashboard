package dashboard.pdp;

import dashboard.domain.Attribute;
import dashboard.domain.Policy;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PdpService {

    List<Policy> policies();

    Policy policy(Long id);

    Policy create(Policy policy);

    Policy update(Policy policy);

    List<Attribute> allowedAttributes();

    ResponseEntity<String> delete(Long id);

    List<Policy> revisions(Long id);

    boolean isAvailable();


}
