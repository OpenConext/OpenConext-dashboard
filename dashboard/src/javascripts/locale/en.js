// Interpolation works as follows:
//
// Make a key with the translation and enclose the variable with {{}}
// ie "Hello {{name}}" Do not add any spaces around the variable name.
// Provide the values as: I18n.t("key", {name: "John Doe"})

import I18n from "i18n-js";

I18n.translations.en = {
  code: "EN",
  name: "English",
  select_locale: "Select English",

  boolean: {
    yes: "Yes",
    no: "No"
  },

  browser_not_supported: {
    title: "Your browser is not supported.",
    description_html: "Your version of Internet Explorer is not supported. Please update your browser to a more modern version."
  },

  date: {
    month_names: [null, "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
  },

  header: {
    title: "Dashboard",
    welcome: "Welcome,",
    links: {
      help_html: "<a href=\"https://wiki.surfnet.nl/display/surfconextdev/Beschikbare+diensten+activeren#Beschikbaredienstenactiveren-HandleidingSURFconextDashboard\" target=\"_blank\">Help SURFconext Dashboard</a>",
      logout: "Logout",
      exit: "Exit"
    },
    you: "You",
    profile: "Profile",
    switch_idp: "Switch IDP"
  },

  navigation: {
    apps: "Services",
    policies: "Authorization policies",
    notifications: "Notifications",
    history: "History",
    stats: "Statistics",
    my_idp: "My institute"
  },

  facets: {
    title: "Filters",
    reset: "Reset",
    download: "Export overview",
    unknown: "Unknown",
    totals: {
      all: "Showing all {{total}} services",
      filtered: "Showing {{count}} of {{total}} services"
    },
    static: {
      connection: {
        all: "All",
        has_connection: "Yes",
        name: "Service connected",
        no_connection: "No",
      },
      license: {
        has_license_sp: "Yes, with service provider",
        has_license_surfmarket: "Yes, with SURFmarket",
        name: "License",
        not_needed: "Not needed",
        unknown: "Unknown",
      },
      used_by_idp: {
        all: "All",
        name: "Offered by my institution",
        no: "No",
        yes: "Yes",
      },
      published_edugain: {
        all: "All",
        name: "Published in eduGAIN federation",
        no: "No",
        yes: "Yes",
      }
    }
  },

  apps: {
    detail: {
      application_usage: "Service usage",
      attribute_policy: "Attributes",
      close_screenshot: "Close",
      how_to_connect: "Activate service",
      how_to_disconnect: "Deactivate service",
      idp_usage: "Used by",
      license_info: "License",
      overview: "Overview",
    },
    overview: {
      connect: "",
      connect_button: "Connect",
      connected: "Connected",
      license: "License secured",
      licenseStatus: "License required",
      license_present: {
        na: "n/a",
        no: "No",
        unknown: "Unknown",
        yes: "Yes",
      },
      license_unknown: "Unknown",
      name: "Service",
      no_results: "No services available",
      search: "Search",
      search_hint: "Filter by name",
    },
  },

  app_meta: {
    question: "Got a question?",
    eula: "Terms & Conditions",
    website: "Website",
    support: "Support pages",
    login: "Login page"
  },

  license_info_panel: {
    title: "License information",
    has_license_surfmarket_html: "There is a valid license available via <a href=\"https://www.surfmarket.nl\" target=\"_blank\">SURFmarket</a>.",
    has_license_sp_html: "There is a valid license available via service supplier <a href=\"{{serviceUrl}}\" target=\"_blank\">{{serviceName}}</a>.",
    no_license_html: "Your institution has no valid license available via <a href=\"https://www.surfmarket.nl\" target=\"_blank\">SURFmarket</a>.",
    not_needed_html: "This Service does not require a license.",
    unknown_license: "It is unknown whether a license is required or not.",
    no_license_description_html: "" +
      "<ul>" +
      "   <li>Your institution can obtain a license from <a href=\"https://www.surfmarket.nl\" target=\"_blank\">SURFmarket</a>.</li>" +
      "</ul>" +
      "<br />In some cases this license needs to be obtained directly from the service supplier.",
    unknown_license_description_html: "There could be multiple reasons:" +
      "<ul>" +
      "   <li>SURF or another institution is offering this service for free.</li>" +
      "   <li>The license needs to be obtained directly from the service supplier.</li>" +
      "   <li>The license hasn't been added to <a href=\"https://www.surfmarket.nl\" target=\"_blank\">SURFmarket</a>'s administration yet.</li>" +
      "</ul>" +
      "<p>If necessary, SURFnet will contact the service supplier or <a href=\"https://www.surfmarket.nl\" target=\"_blank\">SURFmarket</a> before activating the connection.</p>"
  },

  license_info: {
    unknown_license: "No license information available",
    has_license_surfmarket: "License available via SURFmarket",
    has_license_sp: "License available via service supplier",
    no_license: "No license available",
    no_license_needed: "No license needed",
    license_info: "Read how to obtain a license",
    license_unknown_info: "Read more",
    valid: "License is valid untill {{date}}"
  },

  overview_panel: {
    wiki_info_html: "Extra information is available for this services in the SURFconext <a href=\"{{link}}\" target=\"_blank\">wiki</a>.",
    no_description: "The description of this service is not available.",
    description: "Description",
    has_connection: "Active connection",
    no_connection: "Inactive connection",
    how_to_connect: "Read how to activate",
    disconnect: "Read how to deactivate the connection",
    normen_kader: "Framework of Legal Standards for Cloud Services in Higher Education",
    normen_kader_html: "{{name}} has published its compliance and possible noncompliance with the Framework of Legal Standards for Cloud Services in Higher Education <a href=\"{{link}}\" target=\"_blank\">online</a>. For more information about the Framework see the <a href=\"https://www.surf.nl/en/knowledge-and-innovation/knowledge-base/2013/framework-of-legal-standards-for-cloud-services-in-higher-education.html\" target=\"_blank\">SURFnet website</a>",
    no_normen_kader_html: "{{name}} has not published its compliance and possible noncompliance with the Framework of Legal Standards for Cloud Services in Higher Education. For more information about the Framework see the <a href=\"https://www.surf.nl/en/knowledge-and-innovation/knowledge-base/2013/framework-of-legal-standards-for-cloud-services-in-higher-education.html\" target=\"_blank\">SURFnet website</a>",
    single_tenant_service: "Single tenant service",
    single_tenant_service_html: "{{name}} is a single tenant service and as such requires a separate instance for each institution that wants to connect to this service. For more information about single tenant services see the <a href=\"https://wiki.surfnet.nl/display/services/(Cloud)services\" target=\"_blank\">SURFnet wiki</a>"
  },

  attributes_policy_panel: {
    arp: {
      noarp: "There is no 'Attribute Release Policy' specified. All known attributes may be exchanged, or a custom 'attribute manipulation script' might be in effect for this Service Provider.",
      noattr: "No attributes will be exchanged with {{name}}.",
    },
    attribute: "Attribute",
    hint: "The attributes and their values for your personal account are displayed. This might not be representative for other accounts within your organization.",
    subtitle: "The following attributes will be exchanged with {{name}}. Please note: All attributes should contain the right value(s). If attributes are missing, additional steps might be needed to ensure a working connection.",
    title: "Attributes",
    your_value: "Your value",
  },
  idp_usage_panel: {
    title: "Used by",
    subtitle: "The following institutions are connected to {{name}}.",
    subtitle_none: "There are no institutions connected to {{name}}.",
    institution: "Institution"
  },

  how_to_connect_panel: {
    accept: "I hereby certify that I have read these terms and that I accept them on behalf of my institution.",
    accept_disconnect: "Yes, I agree that {{app}} will no longer be available to my organization",
    attributes: "attributes",
    attributes_policy: "attribute policy",
    back_to_apps: "Back to all services",
    cancel: "Cancel",
    check: "Check the",
    checklist: "Finish this checklist before activating the connection:",
    comments_description: "Comments will be sent to SURFconext.",
    comments_placeholder: "Enter comments here...",
    comments_title: "Any additional comments?",
    connect: "Activate service",
    connect_title: "Connect {{app}}",
    disconnect: "Deactivate service",
    disconnect_title: "Deactivate connection with {{app}}",
    done_disconnect_subtitle_html: "You will be contacted about the further steps needed to finalize this deactivation. If you have any questions before that, please contact <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a>.",
    done_disconnect_title: "Deactivation requested!",
    done_subtitle_html: "You will be contacted about the further steps needed to finalize this connection. If you have any questions before that, please contact <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a>.",
    done_subtitle_with_jira_html: "You will be contacted about the further steps needed to finalize this connection. If you have any questions before that, please contact <a href=\"mailto:support@surfconext.nl?subject=Question about connection {{jiraKey}}\">support@surfconext.nl</a> and include the following ticket number in the subject: {{jiraKey}}.",
    done_title: "Connection made!",
    forward_permission: {
      after: " to {{app}}.",
      before: "SURFnet has permission to forward the ",
    },
    info_sub_title: "You can activate a connection from this dashboard. We advise you to follow the checklist and check the specific information for this app before you activate.",
    info_title: "Activate connection",
    jira_unreachable: "Something went wrong with your request",
    jira_unreachable_description: "It is currently not possible to do a request. Please try again later.",
    license: "license",
    license_info: "license information",
    obtain_license: {
      after: " for using {{app}}.",
      before: "It is the responsibility of my institution to obtain a ",
    },
    provide_attributes: {
      after: ".",
      before: "It is the responsibility of my institution to provide the correct ",
    },
    read: "Read the",
    single_tenant_service_warning: "Requests for activating a single tenant services take longer to process. SURFnet will contact you to discuss the activation process after it has received your request.",
    terms_title: "By requesting an activation you accept these terms",
    wiki: "wiki for this service",
  },

  application_usage_panel: {
    title: "Service usage",
    download: "Export",
    error_html: "Stats are currently unavailable. <a href=\"mailto:support@surfconext.nl\">Contact support</a> for more information."
  },

  contact: {
    email: "Service support email"
  },

  search_user: {
    switch_identity: "Switch identity",
    search: "Search",
    search_hint: "Filter by name",
    name: "Name",
    switch_to: "Switch to role",
    switch: {
      role_dashboard_viewer: "Viewer",
      role_dashboard_admin: "Admin"
    }
  },

  not_found: {
    title: "The requested page could not be found.",
    description_html: "Please check the spelling of the URL or go to the <a href=\"/\">homepage</a>."
  },

  server_error: {
    title: "Something went wrong when opening this page.",
    description_html: "Please try again later or contact <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a>."
  },

  logout: {
    title: "Logout completed successfully.",
    description_html: "You <strong>MUST</strong> close your browser to complete the logout process."
  },

  footer: {
    surfnet_html: "<a href=\"https://www.surfnet.nl/en\" target=\"_blank\">SURFnet</a>",
    terms_html: "<a href=\"https://wiki.surfnet.nl/display/conextsupport/Terms+of+Service+%28EN%29\" target=\"_blank\">Terms of Service</a>",
    contact_html: "<a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a>"
  },

  notifications: {
    title: "Notifications",
    icon: "Icon",
    name: "Name",
    license: "License",
    connection: "Connection",
    messages: {
      fcp: "The following Services might not be accessible yet because there is not a license available or no active SURFconext connection."
    }
  },

  my_idp: {
    title: "My institute",
    sub_title_html: "The following roles have been assigned (<a target=\"_blank\" href=\"https://wiki.surfnet.nl/display/surfconextdev/Beschikbare+diensten+activeren#Beschikbaredienstenactiveren-HoekunjeopSURFconextaangeslotendienstenactiveren?\">more info</a>):",
    role: "Role",
    users: "User(s)",
    SURFconextverantwoordelijke: "SURFconext owner",
    SURFconextbeheerder: "SURFconext maintainer",
    "Dashboard supergebruiker": "Dashboard Super User",
    services_title: "Services provided by your institute:",
    service_name: "Service name",
    license_contact_html: "Primary License contact person (<a target=\"_blank\" href=\"https://wiki.surfnet.nl/display/surfconextdev/Beschikbare+diensten+activeren#Beschikbaredienstenactiveren-HoekunjeopSURFconextaangeslotendienstenactiveren?\">more info</a>):",
    license_contact_name: "Name",
    license_contact_email: "Email",
    license_contact_phone: "Phonenumber"
  },

  policies: {
    confirmation: "Are your sure you want to remove policy '{{policyName}}?'",
    flash: "Authorization policy '{{policyName}}' was successfully {{action}}",
    flash_created: "created",
    flash_deleted: "deleted",
    flash_first: "This is the first authorization policy for this service. Before it becomes active, the SURFconext Team must manually perform a configuration change. A notification has been sent to the SURFconext Team. They will get in touch with you.",
    flash_updated: "updated",
    new_policy: "New authorization policy",
    how_to: "How-to",
    pdp_unreachable: "PDP unreachable",
    pdp_unreachable_description: "Currently unable to fetch the policies from PDP. Please try again later.",
    overview: {
      active: "Active",
      description: "Description",
      identityProviderNames: "Institution(s)",
      name: "Name",
      numberOfRevisions: "Revisions",
      search: "Search",
      search_hint: "Filter by name",
      serviceProviderName: "Service",
    },
  },

  policy_attributes: {
    attribute: "Attribute",
    attribute_value_placeholder: "Attribute value...",
    group_info: " The value(s) must be fully qualified group IDs e.g. 'urn:collab:group:surfteams.nl:nl:surfnet:diensten:admins'",
    new_attribute: "Add new attribute....",
    new_value: "Add a new value...",
    sab_info: " The value(s) must be valid roles in SAB e.g. 'Instellingsbevoegde'",
    values: "Values(s)",
  },

  policy_detail: {
    access: "Access",
    attribute: "Attribute",
    autoFormat: "AutoFormat policy description",
    cancel: "Cancel",
    confirmation: "Are your sure you want to leave this page?",
    create_policy: "Create new authorization policy",
    deny: "Deny",
    deny_info: "Deny policies are less common to use. If the attributes in the policy match those of the person trying to login then this will result in a Deny. No match will result in a Permit.",
    deny_message: "Deny message",
    deny_message_info: "This is the message displayed to the user if access is denied based on this policy.",
    deny_message_nl: "Deny message in Dutch",
    description: "Description",
    idps_placeholder: "Select the Identity Providers - zero or more",
    institutions: "Institutions",
    isActive: "Active",
    isActiveDescription: "Mark the authorization policy active",
    isActiveInfo: " Inactive authorization policies are not evaluated in enforcement decisions",
    name: "Name",
    permit: "Permit",
    permit_info: "Permit policies enforce that a only a successful match of the attributes defined will result in a Permit. No match will result in a Deny.",
    rule: "Rule",
    rule_and: "AND",
    rule_and_info: "Policies with a logical AND rule enforce that all attributes defined must match those of the person trying to login.",
    rule_info_add: " Note that attribute values with the same attribute name always be evaluated with the logical OR.",
    rule_info_add_2: "Note that a Deny access authorization policy always and implicitly uses the logical AND for different attribute names.",
    rule_or: "OR",
    rule_or_info: "Policies defined with a logical OR only require one of the attributes to match the attributes of the person requesting access.",
    service: "Service",
    spScopeInfo: "The available Services are scoped to your services if you don't select an Institution",
    sp_placeholder: "Select the Service Provider - required",
    sub_title: "Created by {{displayName}} on {{created}}",
    submit: "Submit",
    update_policy: "Update authorization policy",
  },

  revisions: {
    active: "Active",
    allAttributesMustMatch: "Logical OR rule?",
    attributes: "Attributes",
    changes_first_html: "This is the first <span class=\"curr\">initial revision {{currRevisionNbr}}</span> created by {{userDisplayName}} from {{authenticatingAuthorityName}} on {{createdDate}}.",
    changes_info_html: "Showing the changes between <span class=\"prev\"> revision number {{prevRevisionNbr}}</span> and <span class=\"curr\">revision number {{currRevisionNbr}}</span> made by {{userDisplayName}} from {{authenticatingAuthorityName}} on {{createdDate}}.",
    denyAdvice: "Deny message",
    denyAdviceNl: "Deny message in Dutch",
    denyRule: "Access Permit rule?",
    description: "Description",
    identityProviderNames: "Institution(s)",
    name: "Name",
    revision: "Revision number",
    serviceProviderName: "Service",
    title: "Revisions",
  },

  history: {
    title: "History",
    requestDate: "Date",
    type: "Type",
    jiraKey: "Ticket ID",
    status: "Status",
    userName: "By",
    action_types: {
      LINKREQUEST: "Connect to {{serviceName}}",
      UNLINKREQUEST: "Disconnect from {{serviceName}}",
      QUESTION: "Question"
    },
  },

  stats: {
    filters: {
      name: "Filters"
    },
    chart: {
      type: {
        name: "Chart type",
        idpspbar : "Logins for all SPs",
        idpsp: "Logins per SP"
      },
      periodFrom: {
        name: "Date from (yyyy-mm-dd)"
      },
      periodTo: {
        name: "Date to (yyyy-mm-dd)"
      },
      periodDate: {
        name: "Date (yyyy-mm-dd)"
      },
      period: {
        name: "Period",
        day: "Day",
        week: "Week",
        month: "Month",
        quarter: "Quarter",
        year: "Year"
      },
      sp: {
        name: "Service Provider"
      },
    }
  },

  profile: {
    title: "Profile",
    sub_title: "The following profile data has been provided by your home institution. This data as well as your group membership data (e.g.SURFteams) will be stored in SURFconext and shared with services accessed via SURFconext.",
    my_attributes: "My attributes",
    attribute: "Attribute",
    value: "Value",
    my_roles: "My roles",
    my_roles_description: "The following roles have been assigned:",
    role: "Role",
    role_description: "Description",
    roles: {
      ROLE_DASHBOARD_ADMIN: {
        name: "SURFconext owner",
        description: "You are authorized on behalf of your institution to manage the service connections"
      },
      ROLE_DASHBOARD_VIEWER: {
        name: "SURFconext maintainer",
        description: "You are authorized on behalf of your institution to view the information about the services"
      },
      ROLE_DASHBOARD_SUPER_USER: {
        name: "Dashboard Super User",
        description: "You are the super user of the dashboard"
      }
    },
    attribute_map: {
      "Shib-uid": {
        name: "UID",
        description: "your unique username within your organization"
      },
      "Shib-surName": {
        name: "Surname",
        description: "your surname"
      },
      "Shib-givenName": {
        name: "Name",
        description: "your name"
      },
      "Shib-commonName": {
        name: "Full Name",
        description: "your full name"
      },
      "Shib-displayName": {
        name: "Display Name",
        description: "display name as shown in applications"
      },
      "Shib-email": {
        name: "E-mailaddress",
        description: "your e-mailaddress as known within your organization"
      },
      "Shib-eduPersonAffiliation": {
        name: "Relation",
        description: "relation between your and your organization"
      },
      "Shib-eduPersonEntitlement": {
        name: "Entitlement",
        description: "entitlement which decides upon your authorization within the application"
      },
      "Shib-eduPersonPN": {
        name: "Net-ID",
        description: "your unique username within your organization augmented with @organizationname.nl"
      },
      "Shib-preferredLanguage": {
        name: "Preferred Language",
        description: "a two letter abbreviation according to ISO 639; no subcodes"
      },
      "Shib-homeOrg": {
        name: "Organization",
        description: "name for the organization, making use of the domain name of the organization conform RFC 1035"
      },
      "Shib-schacHomeOrganizationType": {
        name: "Type of Organization",
        description: "type of organization to which the user belongs"
      },
      "Shib-nlEduPersonHomeOrganization": {
        name: "Display name of Organization",
        description: "display name of the organization"
      },
      "Shib-nlEduPersonOrgUnit": {
        name: "Unitname",
        description: "unit name"
      },
      "Shib-nlEduPersonStudyBranch": {
        name: "Study Branch",
        description: "study branch; numeric string which contains the CROHOcode. can be empty if the branch is unknown"
      },
      "Shib-nlStudielinkNummer": {
        name: "Studielinknummer",
        description: "studielinknummer of the student as registered at www.studielink.nl"
      },
      "Shib-nlDigitalAuthorIdentifier": {
        name: "DAI",
        description: "Digital Author Identifier (DAI)"
      },
      "Shib-userStatus": {
        name: "Userstatus",
        description: "Status of this user in SURFconext"
      },
      "Shib-accountstatus": {
        name: "Accountstatus",
        description: "Status of this account in SURFconext"
      },
      "name-id": {
        name: "Identifier",
        description: "Status of this account in SURFconext"
      },
      "Shib-voName": {
        name: "Virtual Organisation Name",
        description: "The name of the Virtual Urganisation for which you have authenticated"
      },
      "Shib-user": {
        name: "Identifier",
        description: "Status of this account in SURFconext"
      },
      "Shib-memberOf": {
        name: "Membership",
        description: "Membership of Virtual Organizations and SURFconext."
      }
    }
  }
};
