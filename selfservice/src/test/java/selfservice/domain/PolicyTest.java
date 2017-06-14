package selfservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.*;

public class PolicyTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testJsonSerialization() throws IOException {
    Policy policy = new Policy();
    policy = Policy.PolicyBuilder.of(policy).withCreated(new Date()).build();

    String s = objectMapper.writeValueAsString(policy);
    assertTrue(s.contains("created"));

    String json = IOUtils.toString(new ClassPathResource("pdp-json/policy.json").getInputStream());
    policy = objectMapper.readValue(json, Policy.class);
    Date created = policy.getCreated();
    assertNull(created);
  }

}
