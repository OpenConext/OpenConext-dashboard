// Interpolation works as follows:
//
// Make a key with the translation and enclose the variable with {{}}
// ie "Hello {{name}}" Do not add any spaces around the variable name.
// Provide the values as: I18n.t("key", {name: "John Doe"})


I18n.translations.en = {
  code: "EN",
  name: "English",
  select_locale: "Select English",

  boolean: {
    yes: "Yes",
    no: "No"
  },

  date: {
    month_names: [null, "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
  },

  header: {
    title: "SURFconext Dashboard",
    welcome: "Welcome,",
    links: {
      help_html: "<a href=\"https://wiki.surfnetlabs.nl/display/conextsupport/Dashboard+van+SURFconext+%28EN%29\" target=\"_blank\">Help</a>",
      logout: "Logout",
      exit: "Exit"
    },
    you: "You",
    profile: "Profile",
    switch_idp: "Switch IDP"
  },

  navigation: {
    apps: "Services",
    notifications: "Notifications",
    history: "History",
    stats: "Statistics",
    my_idp: "My institute"
  },

  facets: {
    title: "Filters",
    reset: "reset",
    download: "Download overview",
    totals: {
      all: "Showing all {{total}} services",
      filtered: "Showing {{count}} of {{total}} services"
    },
    static: {
      connection: {
        name: "Connection",
        has_connection: "Has connection",
        no_connection: "No connection"
      },
      license: {
        name: "License",
        has_license: "Has license",
        no_license: "No license",
        unknown_license: "License unknown"
      }
    }
  },

  apps: {
    overview: {
      name: "Service",
      license: "License",
      license_unknown: "Unknown",
      connected: "Connection",
      search_hint: "Filter by name",
      search: "Search",
      connect: "",
      connect_button: "Connect"
    },
    detail: {
      overview: "Overview",
      license_info: "License info",
      application_usage: "Service usage",
      attribute_policy: "Attribute policy",
      how_to_connect: "How to connect"
    }
  },

  app_meta: {
    question: "Got a question?",
    eula: "Terms & Conditions",
    website: "Website",
    support: "Support pages"
  },

  overview_panel: {
    description: "Description",
    unknown_license: "License information unknown",
    has_license: "License available",
    no_license: "No license",
    license_info: "Read how to obtain a license",
    has_connection: "Has connection",
    no_connection: "No connection",
    how_to_connect: "Read how to connect",
    disconnect: "Read how to disconnect"
  },

  attributes_policy_panel: {
    title: "Attributes",
    subtitle: "The following attributes will be exchanged with {{name}}. Please note: If keys are missing, additional steps might be needed to ensure a working connection.",
    attribute: "Attribute",
    your_value: "Your value",
    hint: "We show you an example of this key for your own person account so you get an idea of that this actually is. This might not be representative for other accounts within your organization.",
    arp: {
      noarp: "There is no 'Attribute Release Policy' specified. All known attributes will be exchanged with {{name}}.",
      noattr: "No attributes will be exchanged with {{name}}."
    }
  },

  how_to_connect_panel: {
    info_title: "How to connect",
    info_sub_title: "You can establish a connection from this dashboard. We advise you to follow the checklist and check the specific information for this app before you connect.",
    connect_title: "Connect {{app}}",
    checklist: "General checklist",
    check: "Check the",
    read: "Read the",
    license_info: "license information",
    attributes_policy: "attribute policy",
    wiki: "wiki for this service",
    wiki_link: "http://www.google.com/",
    specific: {
      title: "Specific information",
      description: "some text"
    },
    connect: "Connect service",
    connect_hint: "(will take you to the form to establish the connection)",
    cancel: "Cancel",
    terms_title: "By requesting a connection you accept these terms",
    comments_title: "Any additional comments?",
    comments_description: "Comments will be sent to SURFconext and the service provider.",
    comments_placeholder: "Enter comments here...",
    provide_attributes: {
      before: "It is the responsibility of my institution to provide the required ",
      after: "."
    },
    forward_permission: {
      before: "SURFnet has permission to forward the ",
      after: " to {{app}}."
    },
    obtain_license: {
      before: "It is the responsibility of my institution to obtain a ",
      after: " for using {{app}}."
    },
    attributes: "attributes",
    license: "license",
    accept: "I hereby certify that I have read these terms and that I accept them on behalf of my institution.",
    back_to_apps: "Back to all services",
    done_title: "Connection made!",
    done_subtitle_html: "You will be contacted about the further steps needed to finalize this connection. If you have any questions before that, please contact <a href=\"mailto:help@surfconext.nl\">help@surfconext.nl</a>.",
    disconnect_title: "Disconnect {{app}}",
    accept_disconnect: "Yes, I agree that {{app}} will no longer be available to my organization",
    disconnect: "Disconnect service",
    done_disconnect_title: "Disconnect requested!",
    done_disconnect_subtitle_html: "You will be contacted about the further steps needed to finalize this disconnect. If you have any questions before that, please contact <a href=\"mailto:help@surfconext.nl\">help@surfconext.nl</a>."
  },

  application_usage_panel: {
    title: "Service usage",
    description: "Number of logins",
    logins: "Logins",
    last_week: "Last week",
    last_month: "Last month",
    last_three_months: "Last 3 months",
    last_year: "Last year",
    download: "Download",
    error_html: "Stats are currently unavailable. <a href=\"mailto:support@surfconext.nl\">Contact support</a> for more information."
  },

  contact: {
    email: "Service support email"
  },

  search_user: {
    switch_identity: "Switch identity",
    search: "Filter by name",
    name: "Name",
    switch_to: "Switch to role",
    switch: {
      role_dashboard_viewer: "Viewer",
      role_dashboard_admin: "Admin"
    }
  },

  stats: {
    logins_for: "Logins for {{service}}",
    legend: "Legend"
  },

  not_found: {
    title: "The requested page could not be found.",
    description_html: "Please check the spelling of the URL or go to the <a href=\"/\">homepage</a>."
  },

  logout: {
    title: "Logout completed successfully.",
    description_html: "You <strong>MUST</strong> close your browser to complete the logout process."
  },

  footer: {
    surfnet_html: "<a href=\"http://www.surfnet.nl/en\" target=\"_blank\">SURFnet</a>",
    terms_html: "<a href=\"https://wiki.surfnetlabs.nl/display/conextsupport/Terms+of+Service+%28EN%29\" target=\"_blank\">Terms of Service</a>",
    contact_html: "<a href=\"mailto:help@surfconext.nl\">help@surfconext.nl</a>"
  },

  notifications: {
    title: "Notifications",
    icon: "Icon",
    name: "Name",
    license: "License",
    connection: "Connection",
    messages: {
      fcp: "The following Services might not be accessible yet because there is not a license available or no active SURFconext connection. Please contact the license contactperson of your institute."
    }
  },

  my_idp: {
    title: "My institute",
    sub_title_html: "The following roles have been assigned: (<a target=\"_blank\" href=\"https://wiki.surfnet.nl/pages/viewpage.action?pageId=25198606\">Roles info</a>)",
    role: "Role",
    users: "User(s)",
    SURFconextverantwoordelijke: "SURFconext owner",
    SURFconextbeheerder: "SURFconext maintainer",
    "Dashboard supergebruiker": "Dashboard Super User",
    services_title: "Services provided by your institute:",
    service_name: "Service name"
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
    statusses: {
      OPEN: "The request is pending",
      CLOSED: "The request is closed"
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
        description: "You are authorized on behalf of your institution to manage the various service connections"
      },
      ROLE_DASHBOARD_VIEWER: {
        name: "SURFconext maintainer",
        description: "You are authorized on behalf of your institution to view the information for the various service connections"
      },
      ROLE_DASHBOARD_SUPER_USER: {
        name: "Dashboard Super User",
        description: "You are the super user of the dashboard"
      }
    },
    attribute_map: {
      "urn:mace:dir:attribute-def:uid": {
        name: "UID",
        description: "your unique username within your organization"
      },
      "urn:mace:dir:attribute-def:sn": {
        name: "Surname",
        description: "your surname"
      },
      "urn:mace:dir:attribute-def:givenName": {
        name: "Name",
        description: "your name"
      },
      "urn:mace:dir:attribute-def:cn": {
        name: "Full Name",
        description: "your full name"
      },
      "urn:mace:dir:attribute-def:displayName": {
        name: "Display Name",
        description: "display name as shown in applications"
      },
      "urn:mace:dir:attribute-def:mail": {
        name: "E-mailaddress",
        description: "your e-mailaddress as known within your organization"
      },
      "urn:mace:dir:attribute-def:eduPersonAffiliation": {
        name: "Relation",
        description: "relation between your and your organization"
      },
      "urn:mace:dir:attribute-def:eduPersonEntitlement": {
        name: "Entitlement",
        description: "entitlement which decides upon your authorization within the application"
      },
      "urn:mace:dir:attribute-def:eduPersonPrincipalName": {
        name: "Net-ID",
        description: "your unique username within your organization augmented with @organizationname.nl"
      },
      "urn:mace:dir:attribute-def:preferredLanguage": {
        name: "Preferred Language",
        description: "a two letter abbreviation according to ISO 639; no subcodes"
      },
      "urn:mace:terena.org:attribute-def:schacHomeOrganization": {
        name: "Organization",
        description: "name for the organization, making use of the domain name of the organization conform RFC 1035"
      },
      "urn:mace:terena.org:attribute-def:schacHomeOrganizationType": {
        name: "Type of Organization",
        description: "type of organization to which the user belongs"
      },
      "urn:mace:surffederatie.nl:attribute-def:nlEduPersonHomeOrganization": {
        name: "Display name of Organization",
        description: "display name of the organization"
      },
      "urn:mace:surffederatie.nl:attribute-def:nlEduPersonOrgUnit": {
        name: "Unitname",
        description: "unit name"
      },
      "urn:mace:surffederatie.nl:attribute-def:nlEduPersonStudyBranch": {
        name: "Study Branch",
        description: "study branch; numeric string which contains the CROHOcode. can be empty if the branch is unknown"
      },
      "urn:mace:surffederatie.nl:attribute-def:nlStudielinkNummer": {
        name: "Studielinknummer",
        description: "studielinknummer of the student as registered at www.studielink.nl"
      },
      "urn:mace:surffederatie.nl:attribute-def:nlDigitalAuthorIdentifier": {
        name: "DAI",
        description: "Digital Author Identifier (DAI) as described at: http://www.surffoundation.nl/smartsite.dws?ch=eng&id=13480"
      },
      "urn:mace:surffederatie_nl:attribute-def:nlEduPersonHomeOrganization": {
        name: "Display name of Organization",
        description: "display name of the organization"
      },
      "urn:mace:surffederatie_nl:attribute-def:nlEduPersonOrgUnit": {
        name: "Unitname",
        description: "unit name"
      },
      "urn:mace:surffederatie_nl:attribute-def:nlEduPersonStudyBranch": {
        name: "Study Branch",
        description: "study branch; numeric string which contains the CROHOcode. can be empty if the branch is unknown"
      },
      "urn:mace:surffederatie_nl:attribute-def:nlStudielinkNummer": {
        name: "Studielinknummer",
        description: "studielinknummer of the student as registered at www.studielink.nl"
      },
      "urn:mace:surffederatie_nl:attribute-def:nlDigitalAuthorIdentifier": {
        name: "DAI",
        description: "Digital Author Identifier (DAI) as described at: http://www.surffoundation.nl/smartsite.dws?ch=eng&id=13480"
      },
      "urn:oid:1.3.6.1.4.1.1076.20.100.10.10.1": {
        name: "Accountstatus",
        description: "Status of this account in the SURFfederation"
      },
      "urn:oid:1.3.6.1.4.1.5923.1.1.1.1": {
        name: "Accountstatus",
        description: "Status of this account in the SURFfederation"
      },
      "nameid": {
        name: "Identifier",
        description: "Status of this account in the SURFfederation"
      },
      "urn:oid:1.3.6.1.4.1.1076.20.100.10.10.2": {
        name: "Virtual Organisation Name",
        description: "The name of the Virtual Urganisation for which you have authenticated"
      },
      "urn:oid:1.3.6.1.4.1.1076.20.40.40.1": {
        name: "Identifier",
        description: "Status of this account in the SURFfederation"
      },
      "urn:oid:1.3.6.1.4.1.5923.1.1.1.10": {
        name: "Identifier",
        description: "Status of this account in the SURFfederation"
      },
      "urn:nl.surfconext.licenseInfo": {
        name: "License information",
        description: "License information for the current service"
      },
      "urn:oid:1.3.6.1.4.1.5923.1.5.1.1": {
        name: "Membership",
        description: "Membership of Virtual Organizations and the SURFfederation."
      }
    }
  }
};
