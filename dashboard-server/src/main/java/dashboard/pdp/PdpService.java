package dashboard.pdp;

import dashboard.domain.Attribute;
import dashboard.domain.Policy;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PdpService {

    List<Policy> policies();

    Policy policy(Object id);

    Policy create(Policy policy);

    Policy update(Policy policy);

    List<Attribute> allowedAttributes();

    ResponseEntity<String> delete(Object id);

    List<Policy> revisions(Object id);

    boolean isAvailable();


}
