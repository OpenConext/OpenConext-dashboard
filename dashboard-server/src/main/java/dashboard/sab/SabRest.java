package dashboard.sab;

import dashboard.domain.IdentityProvider;
import dashboard.manage.Manage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class SabRest implements Sab {

    private static final Logger LOG = LoggerFactory.getLogger(SabRest.class);

    private final RestTemplate restTemplate;
    private final String restEndPointURL;
    private final Manage manage;

    public SabRest(Manage manage,
                   String sabRestUserName,
                   String sabRestPassword,
                   String restEndPointURL) {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(sabRestUserName, sabRestPassword));
        this.restEndPointURL = restEndPointURL;
        this.manage = manage;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SabPerson> getPersonsInRoleForOrganization(String organisationGuid, String role) {
        if (!StringUtils.hasText(organisationGuid)) {
            return Collections.emptyList();
        }

        String url = String.format("%s/profile?guid={organisationGuid}&role={role}", restEndPointURL);
        Map<String, Object> results = restTemplate.getForEntity(
                url,
                Map.class,
                organisationGuid,
                role
        ).getBody();

        LOG.debug("SAB results 'getPersonsInRoleForOrganization' for {} {} is {}", organisationGuid, role, results);

        List<Map<String, Object>> profiles = (List<Map<String, Object>>) results.get("profiles");
        return profiles.stream()
                .map(profile -> {
                    List<SabRole> sabRoles = ((List<Map<String, String>>) profile.get("authorisations")).stream()
                            .map(authorisation -> new SabRole(authorisation.get("short"), authorisation.get("role")))
                            .collect(toList());
                    return new SabPerson(
                            (String) profile.get("firstname"),
                            (String) profile.get("middlename"),
                            (String) profile.get("surname"),
                            (String) profile.get("uid"),
                            (String) profile.get("email"),
                            sabRoles);
                })
                .filter(p -> p.hasRole(role))
                .toList();

    }

    @Override
    public List<SabPerson> getSabEmailsForOrganization(String entityId, String role) {
        Optional<IdentityProvider> identityProviderOptional = manage.getIdentityProvider(entityId, false);
        return identityProviderOptional.map(identityProvider ->
                        this.getPersonsInRoleForOrganization(identityProvider.getInstitutionId(), role))
                .orElse(Collections.emptyList());
    }
}
