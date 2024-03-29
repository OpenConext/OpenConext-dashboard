package dashboard.control;

import dashboard.domain.CoinUser;
import dashboard.domain.IdentityProvider;
import dashboard.domain.Service;

import static dashboard.shibboleth.ShibbolethHeader.Name_Id;
import static java.util.Collections.singletonList;

public class RestDataFixture {

    public interface ServiceUpdater {
        void apply(Service service);
    }

    public static Service serviceWithSpEntityId(String spEntityId, ServiceUpdater... serviceUpdaters) {
        Service service = new Service(1l, "name", "http://logo", "http://website", spEntityId);
        service.setDescription("samenstellen.\r\n\r\n\t\tOf u;, mooi");
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
        coinUser.setCurrentLoaLevel(3);
        coinUser.addAttribute(Name_Id, singletonList(uid));
        for (String idp : idpIds) {
            coinUser.addInstitutionIdp(new IdentityProvider(idp, "institution id", "name", 1L));
            coinUser.addInstitutionIdp(new IdentityProvider(idp, "institution id", "name", 2L));
        }
        return coinUser;
    }

    public static IdentityProvider idp(String idp) {
        return new IdentityProvider(idp, "institution id", "name", 1L);
    }
}
