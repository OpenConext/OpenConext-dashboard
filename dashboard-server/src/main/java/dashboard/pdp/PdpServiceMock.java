package dashboard.pdp;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import dashboard.domain.Policy;
import dashboard.domain.Policy.Attribute;
import dashboard.domain.Policy.PolicyBuilder;
import dashboard.domain.Service;
import dashboard.manage.EntityType;
import dashboard.service.Services;
import dashboard.util.SpringSecurity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class PdpServiceMock implements PdpService {

    private static final List<Attribute> ALLOWED_ATTRIBUTES = ImmutableList.of(
        new Attribute("urn:mace:terena.org:attribute-def:schacHomeOrganization", "Schac home organization"),
        new Attribute("urn:mace:terena.org:attribute-def:schacHomeOrganizationType", "Schac home organization type"),
        new Attribute("urn:mace:dir:attribute-def:eduPersonAffiliation", "Edu person affiliation"),
        new Attribute("urn:mace:dir:attribute-def:eduPersonScopedAffiliation", "Edu person scoped affiliation"));

    private final ListMultimap<Long, Policy> policies = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    @Autowired
    private Services services;

    @Override
    public List<Policy> policies() {
        return policies.keySet().stream().map(key -> Iterables.getLast(policies.get(key))).collect(toList());
    }

    @Override
    public Policy policy(Long id) {
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
    public ResponseEntity<String> delete(Long id) {
        policies.removeAll(id);
        return null;
    }

    @Override
    public List<Policy> revisions(Long id) {
        return Optional.ofNullable(policies.get(id)).orElseThrow(RuntimeException::new);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    private Policy savePolicy(Policy policy) {
        Long id = policies.keySet().stream().max(Long::compare).map(l -> l + 1).orElse(1L);

        try {
            return PolicyBuilder.of(policy)
                .withId(id)
                .withUserDisplayName(SpringSecurity.getCurrentUser().getDisplayName())
                .withCreated(String.valueOf(System.currentTimeMillis()))
                .withActionsAllowed(true)
                .withServiceProviderName(services.getServiceByEntityId(SpringSecurity.getCurrentUser().getIdp().getId(),
                    policy.getServiceProviderId(), EntityType.saml20_sp, Locale.ENGLISH)
                    .map(Service::getName)
                    .orElse("????"))
                .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Policy updatePolicy(Policy policy) {
        try {
            return PolicyBuilder.of(policy)
                .withId(policy.getId())
                .withUserDisplayName(SpringSecurity.getCurrentUser().getDisplayName())
                .withCreated(String.valueOf(System.currentTimeMillis()))
                .withActionsAllowed(true)
                .withRevisionNbr(policy.getRevisionNbr() + 1)
                .withNumberOfRevisions(policy.getNumberOfRevisions() + 1)
                .withServiceProviderName(services.getServiceByEntityId(SpringSecurity.getCurrentUser().getIdp().getId(),
                    policy.getServiceProviderId(), EntityType.saml20_sp, Locale.ENGLISH)
                    .map(Service::getName)
                    .orElse("????"))
                .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
