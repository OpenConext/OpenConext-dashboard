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
        switch_idp: "Switch IDP",
        super_user_switch: "Switch identity"
    },

    navigation: {
        apps: "Services",
        policies: "Authorization policies",
        history: "History",
        stats: "Statistics",
        my_idp: "My institute"
    },

    loader: {
        loading: "All services are being loaded"
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
            },
            interfed_source: {
                tooltip: "Some services available through SURFconext have a home federation different from SURFconext. Here you can select on 'home federation'.",
                name: "Federation source",
                surfconext: "SURFconext",
                edugain: "eduGAIN",
                entree: "Entree"
            },
            entity_category: {
                name: "eduGAIN Entity Category",
                tooltip: "Services can comply to an 'entity categories'.<br>See the <a href=\"https://wiki.surfnet.nl/display/surfconextdev/Entity+categories\" target=\"_blank\">wiki</a> for more information. Here you can filter on services adhering to a certain category.",
                code_of_conduct: "Code of Conduct",
                research_and_scholarship: "Research and Scholarship"
            },
            strong_authentication: {
                name: "Supports SURFsecureID",
                yes: "Yes",
                no: "No"
            },
            attribute_manipulation: {
                name: "Custom attribute manipulation script",
                yes: "Yes",
                no: "No"
            },
            arp: {
                name: "Released attributes",
                info_html: "It is possible that more attributes are being released to the Service through means of attribute manipulation."
            },
            type_consent: {
                tooltip: "Which way new users are asked to give consent before using the service.<br>See the <a target=\"_blank\" href=\"https://wiki.surfnet.nl/display/conextsupport/Het+Consent-scherm\">wiki</a> for more information.",
                name: "Type of consent",
                no_consent: "No consent",
                minimal_consent: "Minimal consent",
                default_consent: "Default consent",
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
            sirtfi_security: "Sirtfi Security",
            privacy: "Privacy",
            consent: "Consent",
            back: "Back"
        },
        overview: {
            connect: "",
            connect_button: "Connect",
            connected: "Connected",
            license: "License secured",
            licenseStatus: "License required",
            aansluitovereenkomstRefused: "Policy signed",
            license_present: {
                na: "n/a",
                no: "No",
                unknown: "Unknown",
                yes: "Yes",
            },
            license_unknown: "Unknown",
            name: "Service",
            no_results: "No services available",
            processing_results: "Processing all services...",
            search: "Search",
            search_hint: "Filter by name",
        },
    },

    app_meta: {
        question: "Got a question?",
        eula: "Terms & Conditions",
        website: "Website",
        support: "Support pages",
        login: "Login page",
        registration_info_html: "This Service Provider is available in SURFconext through <a href=\"https://support.surfconext.nl/edugain\" target=\"_blank\">eduGAIN</a>. The Service Provider is registered by the following federation: <a href=\"{{url}}\" target=\"_blank\">{{url}}</a>.",
        registration_policy: "Registration policy",
        privacy_statement: "Privacy statement",
        metadata_link: "Metadata"
    },

    license_info_panel: {
        title: "License information",
        has_license_surfmarket_html: "There is a valid license available via <a href=\"https://www.surfmarket.nl\" target=\"_blank\">SURFmarket</a>.",
        has_license_sp_html: "A license for <a href=\"{{serviceUrl}}\" target=\"_blank\">{{serviceName}}</a> can be acquired from the supplier of this service.",
        has_license_sp_html_no_service_url: "A license for {{serviceName}} can be acquired from the supplier of this service.",
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
        has_license_sp: "License necessary (via service supplier)",
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
        normen_kader: "Information regarding AVG/GDPR",
        normen_kader_html: "For this service the supplier has published information stating which data they process and where they process this data. You can find this information on the <a href=\"https://wiki.surfnet.nl/pages/viewpage.action?pageId=60689334\" target=\"_blank\">wiki</a>. During 2018 we will incorporate this information in a new version of this Dashboard.",
        no_normen_kader_html: "For this service the supplier has not yet provided AVG/GDPR information; information stating which data they process and where they process this data can be requested at the supplier.",
        single_tenant_service: "Single tenant service",
        single_tenant_service_html: "{{name}} is a single tenant service and as such requires a separate instance for each institution that wants to connect to this service. For more information about single tenant services see the <a href=\"https://wiki.surfnet.nl/display/services/(Cloud)services\" target=\"_blank\">SURFnet wiki</a>",
        interfed_source: "Federation source:",
        publish_in_edugain_date: "Published in eduGAIN on:",
        supports_ssa: "Supports SURFsecureID",
        entity_categories: "Supported Entity Categories",
        entity_category: {
            "http://wwwgeantnet/uri/dataprotection-code-of-conduct/v1": "GÉANT Data Protection Code of Conduct",
            "http://refedsorg/category/research-and-scholarship": "Research and Scholarship"
        },
        aansluitovereenkomst: "Connection Policy",
        aansluitovereenkomstRefused: "This service has refused to sign the 'SURFconext connection agreement' with SURF. Read more about the SURF policy on the <a href=\"https://wiki.surfnet.nl/display/surfconextdev/Afspraken+-+contracten+-+trustframework\" target=\"_blank\">SURF wiki</a>."
    },

    attributes_policy_panel: {
        arp: {
            noarp: "There is no 'Attribute Release Policy' specified. All known attributes are exchanged.",
            noattr: "No attributes will be exchanged with {{name}}.",
            manipulation: "For this Service Provider there is a custom 'attribute manipulation script' in effect. SURFconext will execute the script for every authentication of a user, before releasing attributes to that service. In order for you to understand what information will be released, please find below a description of what the script does:",
        },
        attribute: "Attribute",
        hint: "The attributes and their values for your personal account are displayed. This might not be representative for other accounts within your organization.",
        subtitle: "{{name}} wants to receive the following attributes",
        title: "Attributes",
        your_value: "Your value",
        filter: "For this attribute the following filters have been applied:",
        motivationInfo: "The colomn ‘motivation‘ contains, to the extent available, the explanation of the supplier why they need this attribute.",
        motivation: "Motivation",
        no_attribute_value: "<no value received>",
        filterInfo: "To minimize the data passed on from institution to the service, SURFconext sometimes filters the values of attributes.",
        warning: "Remarks:"
    },
    idp_usage_panel: {
        title: "Used by",
        subtitle: "The following institutions are connected to {{name}}.",
        subtitle_none: "There are no institutions connected to {{name}}.",
        subtitle_single_tenant: "When you want to know which institutes use {{name}} through SURFconext, please send an email with your question to support@surfconext.nl.",
        institution: "Institution"
    },
    sirtfi_panel: {
        title: "The Sirtfi contact persons for {{name}}",
        subtitle: "The Security Incident Response Trust Framework for Federated Identity <a href=\" https://refeds.org/sirtfi\" target=\"_blank\">(Sirtfi) </a> aims to enable the coordination of incident response across federated organisations. This assurance framework comprises a list of assertions which an organisation can attest in order to be declared Sirtfi compliant.",
        contactPersons: "The Sirtfi contact persons for this service:",
        cp_name: "Name",
        cp_email: "Email",
        cp_telephoneNumber: "Telephone number",
        cp_type: "Type",
        cp_type_translate_technical: "Technical",
        cp_type_translate_administrative: "Administrative",
        cp_type_translate_help: "Support",
        cp_type_translate_support: "Support"
    },
    privacy_panel: {
        title: "AVG Information",
        subtitle: "SURFnet requests new connecting SP's to supply some AVG information. If available, you'll find it below. For any missing info, you can contact the supplier.",
        subtitle2: "The provider of the service {{name}} has supplied the following information (if any):",
        question: "Question",
        answer: "Answer",
        accessData: "WHO CAN ACCESS THE DATA?",
        certification: "CAN THE SUPPLIER SUPPLY A THIRD PARTY MEMORANDUM?",
        certificationLocation: "WHERE CAN AN INSTITUTION FIND/REQUEST IT?",
        country: "IN WHAT COUNTRY IS THE DATA STORED",
        otherInfo: "OTHER DATA PRIVACY AND SECURITY INFORMATION",
        privacyPolicy: "DOES THE SUPPLIER PUBLISH A PRIVACY POLICY?",
        privacyPolicyUrl: "WHAT IS THE PRIVACY POLICY URL?",
        securityMeasures: "WHAT SECURITY MEASURES HAS THE SUPPLIER TAKEN?",
        snDpaWhyNot: "IF NO, WHAT ARTICLES POSE A PROBLEM & WHY?",
        surfmarketDpaAgreement: "DID THE SUPPLIER AGREE A DPA WITH SURFMARKET?",
        surfnetDpaAgreement: "IS THE SUPPLIER WILLING TO SIGN THE SURF MODEL DPA?",
        whatData: "WHAT (KIND OF) DATA IS PROCESSED?",
        certificationValidFrom: "CERTIFICATION VALID FROM",
        certificationValidTo: "CERTIFICATION VALID TO",
        noInformation: "No info supplied by provider"
    },
    consent_panel: {
        title: "Consent new users",
        subtitle: "New users will be asked permission for sending personal data to this service.",
        subtitle2: " On this page you can configure in which way users will be asked for consent before they are sent to {{name}}. You can configure to skip consent, ask for minimal consent and add a custom consent message for users of this service. The different settings for consent are explained on <a target=\"_blank\" href=\"https://wiki.surfnet.nl/display/conextsupport/Het+Consent-scherm\">this wiki page</a>.",
        subtitle2Viewer: " On this page you can view in which way users will be asked for consent before they are sent to {{name}}. The different settings for consent are explained on <a target=\"_blank\" href=\"https://wiki.surfnet.nl/display/conextsupport/Het+Consent-scherm\">this wiki page</a>.",
        no_consent: "No consent is required",
        minimal_consent: "Minimal consent is required",
        default_consent: "Default consent with an optional custom message",
        consent_value: "Type of consent required",
        consent_value_tooltip: "The type of consent determines how and if the user will be asked for consent.",
        explanationNl: "Dutch message",
        explanationNl_tooltip: "This custom message will be appended to the English consent screen for new users.",
        explanationEn: "English message",
        explanationEn_tooltip: "This custom message will be appended to the Dutch consent screen for new users.",
        save: "Submit changes",
        change_request_created: "Change request sent to the SURFnet SURFconext-team.",
        no_change_request_created: "No change request is created as you did not change anything.",
        change_request_failed: "Failed to create your change request.",
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
        processing_agreements: "Check whether your institution needs a <a href=\"https://wiki.surfnet.nl/display/surfconextdev/Data+processing+agreement\" target=\"_blank\">processing agreement</a> for this service, and if so, has signed one.",
        comments_description: "Comments will be sent to SURFconext.",
        comments_placeholder: "Enter comments here...",
        comments_title: "Any additional comments?",
        connect: "Activate service",
        connect_title: "Connect {{app}}",
        disconnect: "Deactivate service",
        disconnect_title: "Deactivate connection with {{app}}",
        done_disconnect_subtitle_html: "You will be contacted about the further steps needed to finalize this deactivation. If you have any questions before that, please contact <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a>.",
        done_disconnect_subtitle_html_with_jira_html: "You will be contacted about the further steps needed to finalize this deactivation. If you have any questions before that, please contact <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a> and include the following ticket number in the subject: {{jiraKey}}.",
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
        aansluitovereenkomst_accept: "I hereby certify that I agree with connecting to a service which has not signed the SURF 'aansluitovereenkomst'.",
        not_published_in_edugain_idp: "eduGAIN service",
        not_published_in_edugain_idp_info: "The service {{name}} can not be connected because your institution is not published in eduGAIN. To publish your institution in eduGAIN, please tick 'Published in eduGAIN' under 'My Institute' and create a change request.",
        edit_my_idp_link: "Create change request in 'My Institute'"

    },

    application_usage_panel: {
        title: "Service usage",
        download: "Export",
        error_html: "Stats are currently unavailable. <a href=\"mailto:support@surfconext.nl\">Contact support</a> for more information."
    },

    contact: {
        email: "Service support email"
    },
    export: {
        downloadCSV: "Download as CSV",
        downloadPNG: "Download as PNG",
        downloadPDF: "Download as PDF"
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
        title: "You don't have sufficient access right to access the Dashboard application.",
        description_html: "Please contact <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a> if you think this is incorrect."
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

    my_idp: {
        title: "My institute",
        roles: "Roles",
        sub_title_html: "The following roles have been assigned (<a target=\"_blank\" href=\"https://wiki.surfnet.nl/display/surfconextdev/Beschikbare+diensten+activeren#Beschikbaredienstenactiveren-HoekunjeopSURFconextaangeslotendienstenactiveren?\">more info</a>):",
        role: "Role",
        users: "User(s)",
        settings: "Settings for my own institute",
        settings_edit: "Settings for my own institute and services",
        settings_text: "This section contains several settings of your institute and the Service Provider(s) provided to SURFconext by your institute. These settings are used in SURFconext, for instance in the Where Are You From page. If you would like to change something, please press 'Create change request'.",
        settings_text_viewer: "This section contains several settings of your institute and the Service Provider(s) provided to SURFconext by your institute. These settings are used in SURFconext, for instance in the Where Are You From page.",
        SURFconextverantwoordelijke: "SURFconext owner",
        SURFconextbeheerder: "SURFconext maintainer",
        "Dashboard supergebruiker": "Dashboard Super User",
        services_title: "Services provided by your institute:",
        services_title_none: "None",
        service_name: "Service name",
        license_contact_html: "Primary License contact person (<a target=\"_blank\" href=\"https://wiki.surfnet.nl/display/surfconextdev/Beschikbare+diensten+activeren#Beschikbaredienstenactiveren-HoekunjeopSURFconextaangeslotendienstenactiveren?\">more info</a>):",
        license_contact_name: "Name",
        license_contact_email: "Email",
        license_contact_phone: "Phonenumber",
        institution: "Institution",
        services: "Services",
        edit: "Create change request",
        entity_id: "Entity ID",
        state: "Status",
        prodaccepted: "Production",
        testaccepted: "Staging",
        all: "All",
        name: {
            en: "Name (en)",
            nl: "Name (nl)"
        },
        displayName: {
            en: "Display name (en)",
            nl: "Display name (nl)"
        },
        keywords: {
            en: "Keywords (en)",
            nl: "Keywords (nl)"
        },
        published_in_edugain: "Published in eduGAIN",
        date_published_in_edugain: "Date published in eduGAIN",
        logo_url: "Logo",
        new_logo_url: "New logo URL",
        research_and_scholarship_info: "Connect to CoCo R&S SP’s automatically",
        research_and_scholarship_tooltip: "This means your IdP will be automatically connected to all SPs in<br>SURFconext adhering to both ‘Research & Scholarship Entity Category’<br>and the ‘GEANT Data Protection Code of Conduct’, releasing the R&S attributes. <br>See the <a href=\"https://wiki.surfnet.nl/pages/viewpage.action?pageId=86769882\" target=\"_blank\">wiki</a> for more information.",
        contact: "Contact persons for {{name}}",
        contact_name: "Contact name",
        contact_email: "Contact email",
        contact_type: "Contact type",
        contact_telephone: "Contact telephone",
        contact_types: {
            technical: "Technical",
            support: "Support",
            help: "Support",
            administrative: "Administrative"
        },
        description: {
            en: "Description (en)",
            nl: "Description (nl)"
        },
        guest_enabled: "Guest access enabled",
        edit_message: "You are able to edit the following fields.",
        save: "Create change request",
        change_request_created: "Change request sent to the SURFnet SURFconext-team.",
        no_change_request_created: "No change request is created as you did not change anything.",
        change_request_failed: "Failed to create your change request.",
        comments: "Comments"
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
        policy_name_not_unique_exception: "This policy name is already in use",
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
            QUESTION: "Question",
            CHANGE: "Change"
        },
    },

    stats: {
        filters: {
            name: "Filters",
            allServiceProviders: "All Services"
        },
        state: "Status",
        timeScale: "Period",
        date: "Date",
        from: "From",
        to: "Up to and including",
        today: "Today",
        sp: "Service",
        period: {
            year: "Year"
        },
        displayDetailPerSP: "Display details per Service",
        scale: {
            year: "Year",
            quarter: "Quarter",
            month: "Month",
            week: "Week",
            day: "Day",
            hour: "Hour",
            minute: "Minute",
            all: "Entire Period: from ⇨ to"
        },
        helpLink: "https://wiki.surfnet.nl/display/surfconextdev/Beschikbare+diensten+activeren#Beschikbaredienstenactiveren-Statistieken"
    },
    chart: {
        title: "Logins and users per day",
        chart: "Number of logins per {{scale}}",
        chartAll: "Number of logins",
        userCount: "Total logins",
        uniqueUserCount: "Unique users",
        loading: "Fetching logins....",
        noResults: "No logins are recorded for the given period.",
        date: "Date",
        logins: "Logins per {{scale}}",
        allLogins: "# Logins",
        uniqueLogins: "Unique logins",
        sp: "Service",
        idp: "Institution"
    },
    clipboard: {
        copied: "Copied!",
        copy: "Copy to clipboard"
    },
    live: {
        chartTitle: "Logins per {{scale}}",
        aggregatedChartTitlePeriod: "Logins in the period {{period}} per {{group}}",
        noTimeFrameChart: "Logins from {{from}} until {{to}}"
    },
    service_filter: {
        title: "Filter services",
        state: {
            tooltip: "The status of the Service determines if the Service is visible on the production platform."
        },
        search: "Search services..."
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
            "uid": {
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
            "displayName": {
                name: "Display Name",
                description: "display name as shown in applications"
            },
            "Shib-InetOrgPerson-mail": {
                name: "E-mailaddress",
                description: "your e-mailaddress as known within your organization"
            },
            "Shib-eduPersonAffiliation": {
                name: "Relation",
                description: "relation between your and your organization"
            },
            "Shib-eduPersonScopedAffiliation": {
                name: "Scoped relation",
                description: "scoped relation between your and your organization"
            },
            "eduPersonEntitlement": {
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
            "schacHomeOrganization": {
                name: "Organization",
                description: "name for the organization, making use of the domain name of the organization conform RFC 1035"
            },
            "Shib-schacHomeOrganizationType": {
                name: "Type of Organization",
                description: "type of organization to which the user belongs"
            },
            "Shib-schacPersonalUniqueCode": {
                name: "Personal unique code",
                description: "these values are used to express specific types of identification number"
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
            "is-member-of": {
                name: "Membership",
                description: "Membership of Virtual Organizations and SURFconext."
            }
        }
    }
};
