package nl.surfnet.coin.selfservice.api.rest;


import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

@Controller
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController {

  @RequestMapping("/me")
  public ResponseEntity<RestResponse<CoinUser>> me() {
    return new ResponseEntity(new RestResponse(SpringSecurity.getCurrentUser()), HttpStatus.OK);
  }

  @RequestMapping("/me/switch-to-idp/{switchToIdp}")
  public ResponseEntity currentIdp(@PathVariable("switchToIdp") String switchToIdp, HttpServletResponse response) {
    SpringSecurity.setCurrentIdp(switchToIdp);
    response.setHeader(HTTP_X_IDP_ENTITY_ID, switchToIdp);
    return new ResponseEntity(HttpStatus.OK);
  }


}
