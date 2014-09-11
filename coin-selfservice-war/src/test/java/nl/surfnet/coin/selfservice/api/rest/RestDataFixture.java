package nl.surfnet.coin.selfservice.api.rest;

import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import org.surfnet.cruncher.model.SpStatistic;

import java.util.Arrays;
import java.util.function.Consumer;

public class RestDataFixture {

  public static Service serviceWithSpEntityId(String spEntityId, Consumer<Service>... serviceUpdaters) {
    Service service = new Service(1l, "name", "http://logo", "http://website", false, null, spEntityId);
    if(serviceUpdaters != null) {
      Arrays.asList(serviceUpdaters).stream().forEach(action -> action.accept(service));
    }
    return service;
  }

  public static SpStatistic spStatisticFor(String spEntityId, long entryTime) {
    SpStatistic spStatistic = new SpStatistic();
    spStatistic.setEntryTime(entryTime);
    spStatistic.setSpEntityId(spEntityId);
    spStatistic.setSpName("sp name");
    return spStatistic;
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

}
