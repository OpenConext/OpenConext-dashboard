package dashboard.domain;

import com.google.common.base.MoreObjects;

import java.util.List;

public class OfferedService {

    private Service service;
    private List<InstitutionIdentityProvider> identityProviders;

    public OfferedService() {
    }

    public OfferedService(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }

    public List<InstitutionIdentityProvider> getIdentityProviders() {
        return identityProviders;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("service", service)
                .add("identityProviders", identityProviders).toString();
    }
}
