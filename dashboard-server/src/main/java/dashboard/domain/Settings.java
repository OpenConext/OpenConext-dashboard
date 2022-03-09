package dashboard.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Settings {

    private String keywordsNl;
    private String keywordsEn;
    private String keywordsPt;
    private String displayNamesEn;
    private String displayNamesNl;
    private String displayNamesPt;
    private String descriptionsEn;
    private String descriptionsNl;
    private String descriptionsPt;
    private String organisationUrlEn;
    private String organisationUrlNl;
    private String organisationUrlPt;
    private String organisationNameEn;
    private String organisationNameNl;
    private String organisationNamePt;
    private String organisationDisplayNameEn;
    private String organisationDisplayNameNl;
    private String organisationDisplayNamePt;
    private boolean publishedInEdugain;
    private boolean connectToRSServicesAutomatically;
    private boolean allowMaintainersToManageAuthzRules;
    private String comments;
    private StateType stateType;
    private ConsentType consentType;

    private List<ContactPerson> contactPersons;
    private boolean displayAdminEmailsInDashboard;
    private boolean displayStatsInDashboard;

    private String typeMetaData;
}
