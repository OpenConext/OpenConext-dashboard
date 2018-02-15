package selfservice.api.dashboard;

import static java.util.Collections.singletonList;
import static selfservice.shibboleth.ShibbolethHeader.Name_Id;

import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Service;

public class RestDataFixture {

  public interface ServiceUpdater {
    void apply(Service service);
  }

  public static Service serviceWithSpEntityId(String spEntityId, ServiceUpdater... serviceUpdaters) {
    Service service = new Service(1l, "name", "http://logo", "http://website",spEntityId);

    if (serviceUpdaters != null) {
      for (ServiceUpdater serviceUpdater : serviceUpdaters) {
        serviceUpdater.apply(service);
      }
    }
    return service;
  }

  public static CoinUser coinUser(String uid, String... idpIds) {
    CoinUser coinUser = new CoinUser();
    coinUser.setUid(uid);
    coinUser.addAttribute(Name_Id, singletonList(uid));
    for (String idp : idpIds) {
      coinUser.addInstitutionIdp(new IdentityProvider(idp, "institution id", "name"));
      coinUser.addInstitutionIdp(new IdentityProvider(idp, "institution id", "name"));
    }
    return coinUser;
  }

  public static IdentityProvider idp(String idp) {
    return new IdentityProvider(idp, "institution id", "name");
  }
}
