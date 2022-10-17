package dashboard.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PolicyTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getServiceProviderName() throws JsonProcessingException {
        Policy policy = new Policy();
//        policy.setServiceProviderName("name1");
        String value = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(policy);
        assertTrue(value.contains("[ \"name1\" ]"));
    }
}