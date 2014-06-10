package nl.surfnet.coin.selfservice.util;

import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.control.BaseController;
import org.springframework.mock.web.MockHttpServletRequest;

public class Helpers {
  public static MockHttpServletRequest defaultMockRequest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.getSession().setAttribute(BaseController.SELECTED_IDP, new InstitutionIdentityProvider("id", "name", "inst"));
    return request;
  }

}