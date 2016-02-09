package selfservice.pdp;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import org.springframework.beans.factory.annotation.Autowired;

import selfservice.cache.ServicesCache;
import selfservice.domain.Policy;
import selfservice.domain.Policy.Attribute;
import selfservice.domain.Policy.PolicyBuilder;
import selfservice.domain.Service;
import selfservice.service.ServicesService;
import selfservice.util.SpringSecurity;

public class PdpServiceMock implements PdpService {

  private static final List<Attribute> ALLOWED_ATTRIBUTES = ImmutableList.of(
        new Attribute("urn:mace:terena.org:attribute-def:schacHomeOrganization", "Schac home organization"),
        new Attribute("urn:mace:terena.org:attribute-def:schacHomeOrganizationType", "Schac home organization type"),
        new Attribute("urn:mace:dir:attribute-def:eduPersonAffiliation", "Edu person affiliation"),
        new Attribute("urn:mace:dir:attribute-def:eduPersonScopedAffiliation", "Edu person scoped affiliation"));

  private final ListMultimap<Long, Policy> policies = Multimaps.synchronizedListMultimap(ArrayListMultimap.<Long, Policy>create());

  @Autowired
  private ServicesCache servicesCache;

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
  public void delete(Long id) {
    policies.removeAll(id);
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

    return PolicyBuilder.of(policy)
        .withId(id)
        .withUserDisplayName(SpringSecurity.getCurrentUser().getDisplayName())
        .withCreated(new Date())
        .withActionsAllowed(true)
        .withServiceProviderName(servicesCache.getAllServices("en").stream().filter(service -> service.getSpEntityId().equals(policy.getServiceProviderId())).map(Service::getName).findFirst().orElse("????"))
        .build();
  }

  private Policy updatePolicy(Policy policy) {
    return PolicyBuilder.of(policy)
        .withId(policy.getId())
        .withUserDisplayName(SpringSecurity.getCurrentUser().getDisplayName())
        .withCreated(new Date())
        .withActionsAllowed(true)
        .withRevisionNbr(policy.getRevisionNbr() + 1)
        .withNumberOfRevisions(policy.getNumberOfRevisions() + 1)
        .withServiceProviderName(servicesCache.getAllServices("en").stream().filter(service -> service.getSpEntityId().equals(policy.getServiceProviderId())).map(Service::getName).findFirst().orElse("????"))
        .build();
  }

}
