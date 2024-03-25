package dashboard.pdp;

import com.google.common.collect.*;
import dashboard.domain.Attribute;
import dashboard.domain.Policy;
import dashboard.domain.Service;
import dashboard.manage.EntityType;
import dashboard.service.Services;
import dashboard.util.SpringSecurity;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class PdpServiceMock implements PdpService {

    private static final List<Attribute> ALLOWED_ATTRIBUTES = ImmutableList.of(
            new Attribute("urn:mace:terena.org:attribute-def:schacHomeOrganization", "Schac home organization"),
            new Attribute("urn:mace:terena.org:attribute-def:schacHomeOrganizationType", "Schac home organization type"),
            new Attribute("urn:mace:dir:attribute-def:eduPersonAffiliation", "Edu person affiliation"),
            new Attribute("urn:mace:dir:attribute-def:eduPersonScopedAffiliation", "Edu person scoped affiliation"));

    private final ListMultimap<Object, Policy> policies = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    @Autowired
    private Services services;

    @Override
    public List<Policy> policies() {
        return policies.keySet().stream().map(key -> Iterables.getLast(policies.get(key))).collect(toList());
    }

    @Override
    public Policy policy(Object id) {
        return ofNullable(policies.get(id)).map(Iterables::getLast).orElseThrow(RuntimeException::new);
    }

    @Override
    public Policy create(Policy policy) {
        policies.values().stream().filter(p -> p.getName().equals(policy.getName())).findAny().ifPresent(duplicate -> {
            throw new PolicyNameNotUniqueException(String.format("Policy name '%s' already exists", policy.getName()));
        });

        Policy policyWithId = savePolicy(policy);

        policies.put(policyWithId.getId(), policyWithId);

        return policyWithId;
    }

    @Override
    public Policy update(Policy policy) {
        Policy updatedPolicy = updatePolicy(policy);
        policies.put(policy.getId(), updatedPolicy);
        return policy;
    }

    @Override
    public List<Attribute> allowedAttributes() {
        return ALLOWED_ATTRIBUTES;
    }

    @Override
    public ResponseEntity<String> delete(Object id) {
        policies.removeAll(id);
        return null;
    }

    @Override
    public List<Policy> revisions(Object id) {
        return Optional.ofNullable(policies.get(id)).orElseThrow(RuntimeException::new);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @SneakyThrows
    private Policy savePolicy(Policy policy) {
        policy.setId(UUID.randomUUID().toString());
        policy.setUserDisplayName(SpringSecurity.getCurrentUser().getDisplayName());
        policy.setCreated(String.valueOf(System.currentTimeMillis()));
        policy.setActionsAllowed(true);
        policy.setServiceProviderName(services.getServiceByEntityId(SpringSecurity.getCurrentUser().getIdp().getId(),
                        policy.getServiceProviderId(), EntityType.saml20_sp, Locale.ENGLISH)
                .map(Service::getName)
                .orElse("????"));
        return policy;
    }

    @SneakyThrows
    private Policy updatePolicy(Policy policy) {
        policy.setId(policy.getId());
        policy.setUserDisplayName(SpringSecurity.getCurrentUser().getDisplayName());
        policy.setCreated(String.valueOf(System.currentTimeMillis()));
        policy.setActionsAllowed(true);
        policy.setServiceProviderName(services.getServiceByEntityId(SpringSecurity.getCurrentUser().getIdp().getId(),
                        policy.getServiceProviderId(), EntityType.saml20_sp, Locale.ENGLISH)
                .map(Service::getName)
                .orElse("????"));
        policy.setRevisionNbr(policy.getRevisionNbr() + 1);
        policy.setNumberOfRevisions(policy.getNumberOfRevisions() + 1);
        return policy;
    }

}
