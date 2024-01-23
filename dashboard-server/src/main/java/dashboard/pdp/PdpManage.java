package dashboard.pdp;

import dashboard.control.Constants;
import dashboard.domain.Attribute;
import dashboard.domain.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;

public class PdpManage implements PdpService , Constants {

    private static final Logger LOG = LoggerFactory.getLogger(PdpManage.class);

    private final RestTemplate restTemplate ;
    private final String manageBaseUrl;

    public PdpManage(String manageBaseUrl, String manageUsername, String managePassword) {
        this.restTemplate = new RestTemplate(clientHttpRequestFactory(10 * 1000));

        this.restTemplate.getInterceptors().add(clientHttpRequestInterceptor(manageUsername, managePassword));

        this.manageBaseUrl = manageBaseUrl;

    }

    @Override
    public List<Policy> policies() {
        restTemplate.getForEntity(manageBaseUrl + "/")
        return null;
    }

    @Override
    public Policy policy(Long id) {
        return null;
    }

    @Override
    public Policy create(Policy policy) {
        return null;
    }

    @Override
    public Policy update(Policy policy) {
        return null;
    }

    @Override
    public List<Attribute> allowedAttributes() {
        return null;
    }

    @Override
    public ResponseEntity<String> delete(Long id) {
        return null;
    }

    @Override
    public List<Policy> revisions(Long id) {
        return null;
    }

    @Override
    public boolean isAvailable() {
        try {
            restTemplate.getForEntity(this.manageBaseUrl + "/internal/health", Void.class);
            return true;
        } catch (RestClientException e) {
            LOG.warn("Manage is down", e);
            return false;
        }
    }
}
