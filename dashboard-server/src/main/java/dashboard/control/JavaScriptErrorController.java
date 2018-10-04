package dashboard.control;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(headers = {"Content-Type=application/json"}, produces = {"application/json"})
public class JavaScriptErrorController {

  private final static Logger LOG = LoggerFactory.getLogger(JavaScriptErrorController.class);

  private final ObjectMapper objectMapper = new ObjectMapper();

  @RequestMapping(value = "/dashboard/api/jsError", method = RequestMethod.POST)
  public ResponseEntity<Void> reportError(@RequestBody Map<String, Object> payload) throws JsonProcessingException, UnknownHostException {

    payload.put("dateTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    payload.put("machine", InetAddress.getLocalHost().getHostName());

    LOG.error(objectMapper.writeValueAsString(payload));

    return ResponseEntity.ok().build();
  }

}
