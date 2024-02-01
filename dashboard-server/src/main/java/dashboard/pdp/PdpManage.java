package dashboard.pdp;

import com.fasterxml.jackson.databind.ObjectMapper;
import dashboard.control.Constants;
import dashboard.domain.Attribute;
import dashboard.domain.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;

public class PdpManage extends PdpServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(PdpManage.class);
    private final String manageBaseUrl;

    public PdpManage(ObjectMapper objectMapper, String manageBaseUrl, String manageUsername, String managePassword) {
        super(objectMapper, manageBaseUrl, manageUsername, managePassword);
        this.manageBaseUrl = manageBaseUrl;
    }

    @Override
    protected URI buildUri(String path) {
        checkArgument(path.startsWith("/"));
        return URI.create(String.format("%s/manage/api/internal%s", manageBaseUrl, path));
    }


    @Override
    public boolean isAvailable() {
        try {
            super.getPdpRestTemplate().getForEntity(this.manageBaseUrl + "/internal/health", Void.class);
            return true;
        } catch (RestClientException e) {
            LOG.warn("Manage is down", e);
            return false;
        }
    }
}
