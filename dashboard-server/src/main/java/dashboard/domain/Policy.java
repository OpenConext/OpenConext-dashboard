package dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Policy {

    private Object id;

    private String name;
    private String description;
    private String serviceProviderId;
    private String serviceProviderName;
    private String serviceProviderNameNl;
    private String serviceProviderNamePt;

    private List<String> serviceProviderIds = new ArrayList<>();
    private List<String> serviceProviderNames = new ArrayList<>();
    private List<String> serviceProviderNamesNl = new ArrayList<>();

    private List<String> identityProviderIds = new ArrayList<>();
    private List<String> identityProviderNames = new ArrayList<>();
    private List<String> identityProviderNamesNl = new ArrayList<>();
    private List<String> identityProviderNamesPt = new ArrayList<>();

    private List<Attribute> attributes = new ArrayList<>();

    private String denyAdvice;
    private String denyAdviceNl;
    private String denyAdvicePt;

    private boolean denyRule;
    private boolean allAttributesMustMatch;

    private String userDisplayName;
    private String authenticatingAuthorityName;

    private int numberOfRevisions;
    private int revisionNbr;

    private String created;

    private boolean isActivatedSr;
    private boolean active = true;
    private boolean actionsAllowed;
    private String type = "reg";

}
