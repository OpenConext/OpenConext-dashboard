package selfservice.api.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping(headers = {"Content-Type=application/json"}, produces = {"application/json"})
public class JavaScriptErrorController {

  private final static Logger LOG = LoggerFactory.getLogger(JavaScriptErrorController.class);

  private final ObjectMapper objectMapper = new ObjectMapper();

  @RequestMapping(value = "/dashboard/api/jsError", method = RequestMethod.POST)
  public ResponseEntity<Void> reportError(@RequestBody Map<String, Object> payload) throws JsonProcessingException, UnknownHostException {
    payload.put("dateTime", new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss").format(new Date()));
    payload.put("machine", InetAddress.getLocalHost().getHostName());
    String msg = objectMapper.writeValueAsString(payload);
    LOG.error(msg);
    return ResponseEntity.ok().build();
  }

}
