package nl.surfnet.coin.selfservice.api.rest;


import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.domain.Service;

public class RestDataFixture {

  public static interface ServiceUpdater {
    public void apply(Service service);
  }


  public static Service serviceWithSpEntityId(String spEntityId, ServiceUpdater... serviceUpdaters) {
    Service service = new Service(1l, "name", "http://logo", "http://website", false, null, spEntityId);
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
    for (String idp : idpIds) {
      coinUser.addInstitutionIdp(new InstitutionIdentityProvider(idp, "name", "institution id"));
      coinUser.addInstitutionIdp(new InstitutionIdentityProvider(idp, "name", "institution id"));
    }
    return coinUser;
  }

  public static InstitutionIdentityProvider idp(String idp) {
    return new InstitutionIdentityProvider(idp, "name", "institution id");
  }
}
