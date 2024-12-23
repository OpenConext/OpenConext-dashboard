// Interpolation works as follows:
//
// Make a key with the translation and enclose the variable with {{}}
// ie "Hello {{name}}" Do not add any spaces around the variable name.
// Provide the values as: I18n.t("key", {name: "John Doe"})

import I18n from 'i18n-js'

I18n.translations.en = {
    code: 'EN',
    name: 'English',
    select_locale: 'Select English',

    boolean: {
        yes: 'Yes',
        no: 'No',
    },

    browser_not_supported: {
        title: 'Your browser is not supported.',
        description_html:
            'Your version of Internet Explorer is not supported. Please update your browser to a more modern version.',
    },

    header: {
        welcome: 'Welcome,',
        links: {
            logout: 'Logout',
            exit: 'Exit',
        },
        you: 'You',
        profile: 'Profile',
        switch_idp: 'Switch IDP',
        loginRequired: 'Please log in for more information',
        super_user_switch: 'Switch identity',
        welcome_txt:
            'Here you will find all the applications connected to SURFconext. Log in for information tailored to your institution.',
    },
    forms: {
        required: '{{name}} is a required field',
        invalidUrl: '{{url}} is an invalid URL',
        errors: 'There are invalid / missing inputs'
    },
    confirmation_dialog: {
        title: 'Please confirm',
        confirm: 'Confirm',
        cancel: 'Cancel',
        leavePage: 'Do you really want to leave this page?',
        leavePageSub: 'Changes that you made will not be saved.',
        stay: 'Stay',
        leave: 'Leave',
    },

    navigation: {
        apps: 'Applications',
        policies: 'Authorization policies',
        history: 'Tickets',
        stats: 'Statistics',
        my_idp: 'My institution',
        invite_request: 'Invite',
    },

    loader: {
        loading: 'Applications are being loaded',
    },

    facets: {
        title: 'Filters',
        reset: 'Reset',
        refresh: 'Refresh',
        clear_all: '(Clear all)',
        download: 'Export overview as csv',
        unknown: 'Unknown',
        totals: {
            all: 'Showing all {{total}} applications',
            filtered: 'Showing {{count}} of {{total}} applications',
        },
        static: {
            connection: {
                all: 'All',
                has_connection: 'Yes',
                name: 'Application connected',
                no_connection: 'No',
            },
            license: {
                has_license_sp: 'Yes, with SP',
                has_license_surfmarket: 'Yes, with SURF',
                name: 'License',
                not_needed: 'No',
                unknown: 'Unknown',
            },
            used_by_idp: {
                all: 'All',
                name: 'Offered by my institution',
                no: 'No',
                yes: 'Yes',
            },
            authorization_rules: {
                all: 'All',
                name: 'Authorization rules',
                tooltip: 'Has the application an authorization rule limiting the access to the application?',
                no: 'No',
                yes: 'Yes',
            },
            published_edugain: {
                all: 'All',
                name: 'Published in eduGAIN federation',
                no: 'No',
                yes: 'Yes',
            },
            interfed_source: {
                tooltip:
                    "Some applications available through SURFconext have a home federation different from SURFconext. Here you can select on 'home federation'.",
                name: 'Federation source',
                surfconext: 'SURFconext',
                edugain: 'eduGAIN',
                entree: 'Entree',
            },
            entity_category: {
                name: 'eduGAIN Entity Category',
                tooltip:
                    'Applications can comply to an \'entity categories\'.<br>See the <a href="https://support.surfconext.nl/dashboard-help-entitycategories" target="_blank" rel="noopener noreferrer">wiki</a> for more information. Here you can filter on applications adhering to a certain category.',
                code_of_conduct: 'Code of Conduct',
                code_of_conduct2: 'Code of Conduct v2',
                research_and_scholarship: 'Research and Scholarship',
                selectAll: 'Filter on all',
                tooltipAll:
                    'Check this to filter on applications that<br>have all the checked entity categories.<br><br>The default unchecked behaviour is to<br>filter the applications that have one of the <br>checked entity categories.',
            },
            strong_authentication: {
                name: 'SURFsecureID enabled',
                tooltip:
                    'SURFsecureID second factor authentication is required.<br>For more information see the <a href="https://support.surfconext.nl/idp-help-en" target="_blank" rel="noopener noreferrer">wiki</a>.',
                none: 'None',
            },
            mfa: {
                name: 'MFA enabled',
                tooltip:
                    'Multi factor authentication by the IdP is required.<br>For more information see the <a href="https://support.surfconext.nl/mfa-nl" target="_blank" rel="noopener noreferrer">wiki</a>.',
                none: 'None',
                other: 'Other'
            },
            attribute_manipulation: {
                name: 'Custom attribute manipulation script',
                yes: 'Yes',
                no: 'No',
            },
            arp: {
                name: 'Released attributes',
                tooltip:
                    'More info about these attributes can be found in the <a href="https://support.surfconext.nl/attributes-nl" target="_blank" rel="noopener noreferrer">wiki</a>.',
                info_html:
                    'It is possible that more attributes are being released to the application through means of attribute manipulation.',
            },
            type_consent: {
                tooltip:
                    'Which way new users are asked to give consent before using the application.<br>See the <a target="_blank" rel="noopener noreferrer" href="https://support.surfconext.nl/dashboard-help-consent">wiki</a> for more information.',
                name: 'Type of consent',
                no_consent: 'No screen',
                minimal_consent: 'Consent screen',
                default_consent: 'Information screen (default)',
            },
        },
    },

    apps: {
        tabs: {
            about: 'About',
            attributes: 'Attributes & Privacy',
            resource_servers: 'Resource Servers',
            statistics: 'Statistics',
            settings: 'Settings',
        },
        detail: {
            about: 'About this application',
            application_usage: 'Application usage',
            attribute_policy: 'Attributes',
            close_screenshot: 'Close',
            how_to_connect: 'Activate application',
            how_to_disconnect: 'Deactivate application',
            idp_usage: 'Used by',
            license_data: 'License',
            links: 'Links',
            overview: 'Overview',
            policies: {
                one: '1 policy',
                other: '%{count} policies',
            },
            connected_resource_servers: 'Resource servers',
            sirtfi_security: 'Sirtfi Security',
            ssid: 'SURFsecureID',
            privacy: 'Privacy',
            consent: 'Consent',
            back: 'Back',
            outstandingIssue:
                'There is already a ticket with reference {{jiraKey}} of type {{type}} and status {{status}} for this application.',
            inviteAlreadyProcessed: 'The invite for ticket {{jiraKey}} has already been {{action}}.',
            inviteBeingProcessed: 'The invite for ticket {{jiraKey}} is pending to be processed.',
            outstandingIssueLink:
                ' Go to the <a class="link" href="{{link}}">{{linkName}}</a> section to approve / deny the invitation.',
            approved: 'approved',
            denied: 'denied',
            institutions_header: {
                one: 'Used by 1 institution',
                other: 'Used by {{count}} institutions',
                zero: 'Used by no institutions',
            },
            institutions: 'Institutions',
            provided_information: '{{organisation}} provided the following information',
            connect_service: 'Connect this application',
            connect_service_single_tenant: 'Request this application',
            disconnect_service: 'Disconnect this application',
            connected: 'Connected',
            pending_connection: 'Pending connection request...',
            pending_disconnect: 'Pending disconnect request...',
            approve_invite: 'Accept invite',
            deny_invite: 'Deny invite',
            approve_disconnect_invite: 'Accept request to disconnect this application',
            deny_disconnect_invite: 'Deny request to disconnect this application',
        },
        overview: {
            connect: '',
            connect_button: 'Connect',
            connected: 'Connected',
            connected_services: 'Connected applications',
            all_services: 'All applications',
            dashboardConnectOption: 'Automatic connection',
            license: 'License secured',
            licenseStatus: 'License required',
            aansluitovereenkomstRefused: 'Policy signed',
            contractualBase: 'Contractual Base',
            license_present: {
                na: 'n/a',
                no: 'No',
                unknown: 'Unknown',
                yes: 'Yes',
            },
            license_unknown: 'Unknown',
            name: 'Name',
            organisation: 'Vendor',
            no_results: 'No applications available',
            processing_results: 'Retrieving all applications...',
            search: 'Search',
            search_hint: 'Search applications...',
            add_services_hint:
                'Can\'t find the application you\'re looking for? Send your contact at that application an email that you would like to use the application through <a href="http://support.surfconext.nl/getconexted" target="_blank" rel="noopener noreferrer">SSO using SURFconext</a>, so you can authenticate using your institutional account, and that that is both more secure and more efficient for all parties involved. And that it makes the application more attractive for other institutions, also outside of the Netherlands. Advise them to look into it and send an email to support@surfconext.nl requesting contact to discuss connecting the application.',
        },
        settings: {
            title: 'Application settings',
            menu: {
                consent: 'Consent',
                authorization_policy: 'Authorization policy',
                surf_secure_id: 'SURFsecureID',
                mfa: 'MFA'
            },
        },
    },
    app_meta: {
        question: 'Got a question?',
        eula: 'Terms & Conditions',
        website: 'Website',
        support: 'Support pages',
        login: 'Login page',
        registration_info_html:
            'This application provider is available in SURFconext through <a href="https://support.surfconext.nl/edugain" target="_blank" rel="noopener noreferrer">eduGAIN</a>. The application provider is registered by the following federation: <a href="{{url}}" target="_blank" rel="noopener noreferrer">{{url}}</a>.',
        registration_policy: 'Registration policy',
        privacy_statement: 'Privacy statement',
        metadata_link: 'Metadata',
    },

    license_info_panel: {
        title: 'License information',
        has_license_surfmarket_html:
            'License available via <a href="https://mijn.surfmarket.nl" target="_blank" rel="noopener noreferrer">Mijn SURFmarket</a>.',
        has_license_sp_html:
            'A license for <a href="{{serviceUrl}}" target="_blank" rel="noopener noreferrer">{{serviceName}}</a> can be acquired from {{organisation}}, the supplier of this application.',
        has_license_sp_html_no_service_url:
            'A license for {{serviceName}} can be acquired from {{organisation}}, the supplier of this application.',
        no_license_html:
            'Your institution has no valid license available via <a href="https://mijn.surfmarket.nl" target="_blank" rel="noopener noreferrer">Mijn SURFmarket</a>.',
        not_needed_html: 'This application does not require a license.',
        unknown_license: 'It is unknown whether a license is required or not.',
        no_license_description_html:
            '' +
            '<ul>' +
            '   <li>Your institution can obtain a license from <a href="https://mijn.surfmarket.nl" target="_blank" rel="noopener noreferrer">Mijn SURFmarket</a>.</li>' +
            '</ul>' +
            '<br />In some cases this license needs to be obtained directly from the application supplier.',
        unknown_license_description_html:
            'There could be multiple reasons:' +
            '<ul>' +
            '   <li>SURF or another institution is offering this application for free.</li>' +
            '   <li>The license needs to be obtained directly from the application supplier.</li>' +
            '   <li>The license hasn\'t been added to <a href="https://mijn.surfmarket.nl" target="_blank" rel="noopener noreferrer">Mijn SURFmarket</a>\'s administration yet.</li>' +
            '</ul>' +
            '<p>If necessary, SURF will contact the application supplier or <a href="https://mijn.surfmarket.nl" target="_blank" rel="noopener noreferrer">Mijn SURFmarket</a> before activating the connection.</p>',
    },

    license_info: {
        unknown_license: 'No license information available',
        has_license: 'License necessary',
        no_license: 'No license available',
        no_license_needed: 'No license needed',
        license_info: 'Read how to obtain a license',
        license_unknown_info: 'Read more',
        valid: 'License is valid until {{date}}',
    },

    overview_panel: {
        entityID: 'Entity ID',
        rpClientID: 'Client ID',
        wiki_info_html:
            'Extra information is available for this applications in the SURFconext <a href="{{link}}" target="_blank" rel="noopener noreferrer">wiki</a>.',
        no_description: 'The description of this application is not available.',
        description: 'Description',
        has_connection: 'Active connection',
        no_connection: 'Inactive connection',
        how_to_connect: 'Read how to activate',
        disconnect: 'Read how to deactivate the connection',
        normen_kader: 'Information regarding AVG/GDPR',
        normen_kader_html:
            'For this application the supplier has published information stating which data they process and where they process this data. You can find this information on the <a href="https://support.surfconext.nl/dashboard-info-avg" target="_blank" rel="noopener noreferrer">wiki</a>. During 2018 we will incorporate this information in a new version of this Dashboard.',
        no_normen_kader_html:
            'For this application the supplier has not yet provided AVG/GDPR information; information stating which data they process and where they process this data can be requested at the supplier.',
        single_tenant_service: 'Single tenant application',
        single_tenant_service_html:
            '{{name}} is a single-tenant application, which means the supplier needs to create a new instance for every customer before it is ready for use. For more information about single tenant applications see the <a href="https://support.surfconext.nl/dashboard-info-singletenant" target="_blank" rel="noopener noreferrer">SURF wiki</a>.',
        interfed_source: 'Federation source',
        publish_in_edugain_date: 'Published in eduGAIN on:',
        supports_ssa: 'SURFsecureID enabled',
        minimalLoaLevel:
            'For logging in to this service, second factor authentication is required via SURFsecureID. All users are required to use a token with at least the following Level of Assurance (LoA): <code>{{minimalLoaLevel}}</code>. For more information see the <a href="https://support.surfconext.nl/loa-en" target="_blank" rel="noopener noreferrer">wiki</a>.',
        minimalLoaLevelIdp:
            'For logging in to this service, second factor authentication is required via SURFsecureID. All users from your institution are required to use a token with at least the following Level of Assurance (LoA): <code>{{minimalLoaLevel}}</code>. For more information see the <a href="https://support.surfconext.nl/loa-en" target="_blank" rel="noopener noreferrer">wiki</a>.',
        supportsSsaTooltip: 'Services can also dynamically request a specific LoA during authentication.',
        entity_categories: 'Supported Entity Categories',
        entity_category: {
            'http://wwwgeantnet/uri/dataprotection-code-of-conduct/v1': 'GÉANT Data Protection Code of Conduct',
            'https://refedsorg/category/code-of-conduct/v2': 'REFEDS Data Protection Code of Conduct v2',
            'http://refedsorg/category/research-and-scholarship': 'Research and Scholarship',
            'http://clarineu/category/clarin-member': 'Clarin member',
        },
        aansluitovereenkomst: 'Connection agreement',
        aansluitovereenkomstRefused:
            '{{organisation}} has refused to sign the SURFconext connection agreement with SURF. Read more about this policy on the <a href="https://support.surfconext.nl/dashboard-info-trust" target="_blank" rel="noopener noreferrer">SURF wiki</a>.',
        vendorInfo: 'This application is offered by {{organisation}}.',
        privacyInformation: 'Privacy information',
        privacyInformationInfo: '{{organisation}} has not delivered any privacy information.',
        contractualBase: {
            na: 'No info on contractual basis available: for any questions, please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a>.',
            ao: '{{organisation}} has signed the SURFconext connection agreement.',
            ix: 'Application offered by SURFconext member institution.',
            'r&s+coco':
                'eduGAIN application that has agreed to the Data Protection Code of Conduct and belongs to the Research & Scholarship entity category.',
            entree: 'Member of the Kennisnet Entree-federation.',
            clarin: 'Member of the Clarin research federation.',
            none: '{{organisation}} refused to sign the SURFconext connection agreement.',
            'edugain (community)': 'Application offered through the international research and education community via eduGAIN.'
        },
        contractualBaseWiki:
            'See <a href="https://support.surfconext.nl/contract-info-nl" target="_blank" rel="noopener noreferrer">wiki</a>.',
    },

    attributes_policy_panel: {
        arp: {
            noarp: "All attributes will be exchanged with {{name}}.",
            noattr: 'No attributes will be exchanged with {{name}}.',
            manipulation:
                "For this application a custom attribute manipulation is in effect which modifies the released attributes.",
            manipulationNotes: ' The attribute manipulation perfoms the following actions:',
            resourceServers:
                'This application is connected to Resource Servers and therefore all the attributes released are also accessible for the following Resource Servers:',
        },
        attribute: 'Attribute',
        subtitle: '{{name}} needs to receive the following attributes.',
        title: 'Attributes',
        your_value: 'Your values',
        your_values_tooltip: 'The values for these attributes as they are for your own account. This might not be representative for other accounts in your organization. Only values are shown for attributes that originate at your IdP.',
        filter: 'Filter',
        filterTooltip: 'For this attribute the filter shown will only release the matching values.',
        motivation: 'Purpose',
        motivationTooltip:
            'The column ‘purpose‘ contains, to the extent available, the explanation of the supplier why they need this attribute.',
        source: 'Source',
        persistentMotivation: "Primary identifier of the user",
        sources: {
            idp: 'Your IdP',
            surf: 'SURFconext',
            voot: 'SURFconext Teams',
            invite: 'SURFconext Invite',
            eduid: 'eduID',
            sab: 'SURF Autorisatie Beheer',
            ala: 'Your IdP',
            pseudo_email:'Your IdP',
            manage: 'SURFconext',
            sabrest: 'SAB'
        },
        sourceTooltip: "Which authority supplies the values for this attribute. Your IdP only needs to provide the items marked with 'Your IdP'.",
        attributes: {
            "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified": "Persistent identifier",
            "urn:mace:dir:attribute-def:eduPersonTargetedID": "Unique ID",
            "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent": "Persistent identifier",
            "urn:mace:dir:attribute-def:eduPersonPrincipalName": "Unique identifier",
            "urn:mace:dir:attribute-def:displayName": "Display name",
            "urn:mace:dir:attribute-def:cn": "Common name",
            "urn:mace:dir:attribute-def:givenName": "Given name",
            "urn:mace:dir:attribute-def:sn": "Surname",
            "urn:mace:dir:attribute-def:mail": "Email",
            "urn:mace:dir:attribute-def:ou": "Organizational unit",
            "urn:mace:terena.org:attribute-def:schacHomeOrganization": "Your IdP abbrevation",
            "urn:mace:terena.org:attribute-def:schacHomeOrganizationType": "Type of IdP",
            "urn:schac:attribute-def:schacPersonalUniqueCode": "Unique code",
            "urn:mace:dir:attribute-def:eduPersonAffiliation": "Affiliations",
            "urn:mace:dir:attribute-def:eduPersonScopedAffiliation": "Scoped affiliations",
            "urn:mace:dir:attribute-def:eduPersonEntitlement": "Entitlements",
            "urn:mace:dir:attribute-def:eduPersonOrcid": "ORCID iD",
            "urn:mace:dir:attribute-def:eduPersonAssurance": "Assurances",
            "urn:mace:surf.nl:attribute-def:eckid": "ECK iD",
            "urn:mace:eduid.nl:1.1": "eduID identifier",
            "urn:mace:surf.nl:attribute-def:surf-crm-id": "SURF CRM organization identifier",
            "urn:mace:dir:attribute-def:isMemberOf": "Memberships",
            "urn:mace:dir:attribute-def:uid": "Login name",
            "urn:mace:dir:attribute-def:preferredLanguage": "Preferred language",
            "urn:mace:surffederatie.nl:attribute-def:nlEduPersonOrgUnit": "eduPerson organisation"
        }
    },
    connected_resource_servers_panel: {
        title: 'Connected Resource Servers',
        subtitle: "{{name}} is an OIDC Relying Party and is allowed to query the API's of the following Resource Servers",
        clientId: 'Client ID',
        name: 'Name',
        description: 'Description',
    },
    idp_usage_panel: {
        title: 'Used by',
        subtitle: 'The following institutions are connected to {{name}}.',
        subtitle_none: 'There are no institutions connected to {{name}}.',
        subtitle_single_tenant:
            'When you want to know which institutes use {{name}} through SURFconext, please send an email with your question to support@surfconext.nl.',
        institution: 'Institution',
    },
    sirtfi_panel: {
        title: 'The Sirtfi contact persons for {{name}}',
        subtitle:
            'The Security Incident Response Trust Framework for Federated Identity <a href=" https://refeds.org/sirtfi" target="_blank" rel="noopener noreferrer">(Sirtfi) </a> aims to enable the coordination of incident response across federated organisations. This assurance framework comprises a list of assertions which an organisation can attest in order to be declared Sirtfi compliant.',
        contactPersons: 'In case of a security incident, this application can best be contacted in the following way:',
        cp_name: 'Name',
        cp_email: 'Email',
        cp_telephoneNumber: 'Telephone number',
        cp_type: 'Type',
        cp_type_translate_technical: 'Technical',
        cp_type_translate_administrative: 'Administrative',
        cp_type_translate_help: 'Support',
        cp_type_translate_support: 'Support',
    },
    privacy_panel: {
        title: 'Privacy Information',
        subtitle:
            "SURF provides new connecting applications the opportunity to share information concerning AVG policies. If available, you'll find it below. For any missing info, you can contact the supplier.",
        subtitle2: 'The provider of the application {{name}} has supplied the following information (if any):',
        question: 'Question',
        answer: 'Answer',
        whatData: 'What (kind of) data is processed?',
        country: 'In what country is the data stored?',
        accessData: 'Who can access the data?',
        securityMeasures: 'What security measures has the supplier taken?',
        privacyStatementURLen: 'What is the privacy statement url?',
        privacyStatementURLnl: 'What is the privacy statement url?',
        dpaType: 'How are processing agreements arranged with the institution/customer?',
        otherInfo: 'Other data privacy and security information',
        noInformation: 'No info supplied by provider',
        dpaTypeEnum: {
            dpa_not_applicable: 'The application is not a processor according to GDPR',
            dpa_in_surf_agreement: 'Agreements are dealt with through SURF',
            dpa_model_surf: 'We are open to signing the SURF model contract/we have signed the SURF model contract',
            dpa_supplied_by_service: 'We have arranged the company\'s data processing agreement',
            other: 'Other',
        }
    },
    consent_panel: {
        title: 'Consent new users',
        subtitle: 'New users will be asked permission for sending personal data to this application.',
        subtitle2:
            'You can add an optional message/warning to the information/consent screen, for example to indicate the current application is <i>not an official application</i> of the institution and thus the user should not expect any support from the institution. For more information, please refer to our <a target="_blank" rel="noopener noreferrer" href="https://support.surfconext.nl/dashboard-help-consent">wiki</a> (in Dutch).',
        subtitle2Viewer:
            ' On this page you can view in which way users will be asked for consent before they are sent to {{name}}. The different settings for consent are explained on <a target="_blank" rel="noopener noreferrer" href="https://support.surfconext.nl/dashboard-help-consent">this wiki page</a>.',
        no_consent: 'Do not display information/consent screen about user attributes',
        minimal_consent: 'Display information screen (do not ask users for explicit consent)',
        default_consent: 'Explicitly ask users for consent to release their attributes',
        consent_value: 'Type of consent required',
        consent_value_tooltip: 'The type of consent determines how and if the user will be asked for consent.',
        explanationNl: 'Dutch message',
        explanationNl_tooltip: 'This custom message will be appended to the Dutch consent screen for new users.',
        explanationEn: 'English message',
        explanationEn_tooltip: 'This custom message will be appended to the English consent screen for new users.',
        explanationPt: 'Portuguese message',
        explanationPt_tooltip: 'This custom message will be appended to the Portuguese consent screen for new users.',
        save: 'Submit changes',
        request: 'Request change',
        loa_level: 'SURFsecureID Level of Assurance (LoA)',
        defaultLoa: 'LoA 1: Password authentication through SURFconext at the users home IdP',
        loa1_5: 'LoA 1.5 (see the wiki for more info)',
        loa2: 'LoA 2 (see the wiki for more info)',
        loa3: 'LoA 3 (see the wiki for more info)',
    },
    ssid_panel: {
        title: 'SURFsecureID',
        subtitle:
            'With <a href="https://support.surfconext.nl/secureid-dev-en" target="_blank" rel="noopener noreferrer">SURFsecureID</a> you can better secure access to services with strong authentication. ',
        subtitle2:
            'A user logs in with username and password (the first factor) and SURFsecureID takes care of the second factor authentication like via a mobile app or USB key.',
        subtitle3:
            'By choosing a higher <a href="https://support.surfconext.nl/loa-en" target="_blank" rel="noopener noreferrer">Level of Assurance (LoA)</a> you can add additional protection to your service by adding a second factor to the user\'s login.',
        highestLoaReached:
            'You already have the highest LoA setting. For security reasons you can not request a lower LoA in this form. Please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a> if you want to lower the LoA for this application.',
        appHasLoaLevel:
            'You can not request a Loa setting for this application. This application already has a Loa setting configured to be applied for all institutions. ',
    },
    how_to_connect_panel: {
        accept: 'I hereby certify that I have read these terms and that I accept them on behalf of my institution.',
        accept_disconnect: 'Yes, I agree that {{app}} will no longer be available to my organization',
        attributes: 'attributes',
        attributes_policy: 'attribute policy',
        privacy_policy: 'privacy policy',
        back_to_apps: 'Back to all applications',
        cancel: 'Cancel',
        close: 'Close',
        check: 'Check the',
        checklist: 'Finish this checklist before activating the connection:',
        processing_agreements:
            'Check whether your institution needs a <a href="https://support.surfconext.nl/dashboard-help-vwo" target="_blank" rel="noopener noreferrer">processing agreement</a> for this application, and if so, has signed one.',
        comments_description: 'Comments will be sent to SURFconext.',
        comments_placeholder: 'Enter comments here...',
        comments_title: 'Any additional comments?',
        automatic_connect: 'Activate connection immediately',
        connect: 'Activate application',
        connect_title: 'Connect {{app}}',
        connect_invite_title: 'Accept invite to connect {{app}}',
        disconnect: 'Deactivate application',
        disconnect_title: 'Deactivate connection with {{app}}',
        done_disconnect_subtitle_html:
            'You will be contacted about the further steps needed to finalize this deactivation. If you have any questions before that, please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a>.',
        done_disconnect_subtitle_html_with_jira_html:
            'You will be contacted about the further steps needed to finalize this deactivation. If you have any questions before that, please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a> and include the following ticket number in the subject: {{jiraKey}}.',
        done_disconnect_title: 'Deactivation requested!',
        done_disconnect_subtitle_html_with_jira_html_after_invite:
            'You will be contacted if there are further steps needed to finalize this deactivation. If you have any questions before that, please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a> and include the following ticket number in the subject: {{jiraKey}}.',
        done_disconnect_title_after_invite: 'Request for deactivation accepted!',
        done_subtitle_html:
            'You will be contacted about the further steps needed to finalize this connection. If you have any questions before that, please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a>.',
        done_subtitle_with_jira_html:
            'You will be contacted about the further steps needed to finalize this connection. If you have any questions before that, please contact <a href="mailto:support@surfconext.nl?subject=Question about connection {{jiraKey}}">support@surfconext.nl</a> and include the following ticket number in the subject: {{jiraKey}}.',
        done_title: 'Connection requested!',
        done_subtitle_with_jira_html_after_invite:
            'You will be contacted about the further steps needed to finalize this connection. If you have any questions before that, please contact <a href="mailto:support@surfconext.nl?subject=Question about connection {{jiraKey}}">support@surfconext.nl</a> and include the following ticket number in the subject: {{jiraKey}}.',
        done_title_after_invite: 'Request for connection accepted!',
        rejected_without_interaction_title: 'Connection failed!',
        rejected_without_interaction_subtitle: 'Something went wrong while connecting.', // TODO: change text
        done_without_interaction_title: 'Connection established!', // TODO: check text
        done_without_interaction_subtitle: 'You can make use of it now.', // TODO: check text
        forward_permission: {
            after: ' to {{app}}.',
            before: 'SURF has permission to forward the ',
        },
        info_connection_without_interaction:
            'This application provider allows institutions to connect immediately. There is no need to wait for this connection request to be processed, you can use the application right away!',
        info_connection_share_institution:
            'This application provider is an application offered by your Institution and therefore the connection can be made directly: you can use the application immediately!',
        info_sub_title:
            'You can activate a connection from this dashboard. We advise you to follow the checklist and check the specific information for this app before you activate.',
        info_sub_invite_title:
            'You can accept the invite to connect. We advise you to follow the checklist and check the specific information for this app before you activate.',
        info_title: 'Activate connection',
        jira_unreachable: 'Something went wrong with your request',
        jira_unreachable_description: 'It is currently not possible to do a request. Please try again later.',
        jira_down: 'Ticketing system not available',
        jira_down_description: 'The ticketing system is not available so change requests can not be processed right now. Please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a> to make a change.',
        license: 'license',
        license_info: 'license information',
        obtain_license: {
            after: ' for using {{app}}.',
            before: 'It is the responsibility of my institution to obtain a ',
        },
        provide_attributes: {
            after: '.',
            before: 'It is the responsibility of my institution to provide the correct ',
        },
        read: 'Read the',
        single_tenant_service_warning:
            'Requests for activating a single tenant applications take longer to process. SURF will contact you to discuss the activation process after it has received your request.',
        terms_title: 'By requesting an activation you accept these terms',
        wiki: 'wiki for this application',
        aansluitovereenkomst_accept:
            "I agree with connecting an application which has not signed the SURFconext connection agreement.",
        not_published_in_edugain_idp: 'eduGAIN application',
        not_published_in_edugain_idp_info:
            "The application {{name}} can not be connected because your institution is not published in eduGAIN. To publish your institution in eduGAIN, please tick 'Published in eduGAIN' under 'My Institute' and create a change request.",
        edit_my_idp_link: "Create change request in 'My Institute'",
        disconnect_jira_info:
            'If you want more information about the progress on this issue please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a> and include the ticket number in the subject: {{jiraKey}}',
        invite_denied: 'Ticket {{jiraKey}} was successfully updated with your rejection.',
        invite_accepted: 'Ticket {{jiraKey}} was successfully updated with your approval.',
        deny: 'Deny invition',
        approve: 'Accept invitation',
        deny_invitation: 'Are you  sure you want to deny the invitation to connect to {{app}}',
        deny_invitation_info: 'After you deny the invitation you can always activate the connection from this dashboard.',
        invite_action_collision_title: 'Application {{app}} is already connected.',
        invite_action_collision_subtitle: 'Mid-air collision detected.',
        invite_action_collision:
            'The invitation to connect to {{app}} was already accepted. Perhaps a colleague has already accepted the invite? If you have any question please contact <a href="mailto:support@surfconext.nl?subject={{jiraKey}}">support@surfconext.nl</a> and include the ticket number in the subject: {{jiraKey}}.',
        test_connected_no_connection_title: 'Application {{app}} can not be connected.',
        test_connected_no_connection_subtitle:
            'The status of your institution is staging and therefore no applications can connect.',
        test_connected_no_connection:
            'If you want to change the status of your institution please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a>.',
        activate_with_email: {
            title: 'Contact at institution for this application',
            subTitle: 'In case the application provider wants to contact someone at your institution about this connection, who can they contact?',
            emailPlaceholder: 'Contact person at your institution',
            invalidEmail: 'Invalid email',
            emailRequired: 'Email required',
            disclaimer: 'I opt not to share any name with the application provider',
        },
    },
    application_usage_panel: {
        title: 'Application usage',
        download: 'Export',
        error_html:
            'Stats are currently unavailable. <a href="mailto:support@surfconext.nl">Contact support</a> for more information.',
    },
    contact: {
        email: 'Application support email',
    },
    export: {
        downloadCSV: 'Download as CSV',
        downloadPNG: 'Download as PNG',
        downloadPDF: 'Download as PDF',
    },
    search_user: {
        switch_identity: 'Switch identity',
        search: 'Search',
        search_hint: 'Filter by name',
        name: 'Name',
        switch_to: 'Switch to role',
        switch: {
            role_dashboard_viewer: 'Viewer',
            role_dashboard_admin: 'Admin',
        },
    },
    stats: {
        filters: {
            name: 'Filters',
            allServiceProviders: 'All applications',
        },
        state: 'Status',
        timeScale: 'Period',
        date: 'Date',
        from: 'From',
        to: 'Up to and including',
        today: 'Today',
        sp: 'Application',
        period: {
            year: 'Year',
        },
        displayDetailPerSP: 'Display details per application',
        scale: {
            year: 'Year',
            quarter: 'Quarter',
            month: 'Month',
            week: 'Week',
            day: 'Day',
            hour: 'Hour',
            minute: 'Minute',
            all: 'Entire period: from ⇨ until',
        },
        helpLink: 'https://support.surfconext.nl/dashboard-help-nl#Beschikbaredienstenactiveren-Statistieken',
    },
    chart: {
        title: 'Logins and users per day',
        chart: 'Number of logins per {{scale}}',
        chartAll: 'Number of logins',
        userCount: 'Total logins',
        uniqueUserCount: 'Unique users',
        loading: 'Fetching logins...',
        noResults: 'No logins are recorded for the given period.',
        date: 'Date',
        logins: 'Logins per {{scale}}',
        allLogins: '# Logins',
        uniqueLogins: 'Unique logins',
        sp: 'Application',
        idp: 'Institution',
    },
    clipboard: {
        copied: 'Copied!',
        copy: 'Copy to clipboard',
    },
    live: {
        chartTitle: 'Logins per {{scale}}',
        aggregatedChartTitlePeriod: 'Logins in the period {{period}} per {{group}}',
        noTimeFrameChart: 'Logins from {{from}} until {{to}}',
    },
    server_error: {
        title: "You don't have sufficient access right to access the Dashboard application.",
        description_html:
            'Please contact <a href="mailto:support@surfconext.nl">support@surfconext.nl</a> if you think this is incorrect.',
    },
    not_found: {
        title: 'OOPS, I currently can’t show you that page.',
        subTitle: 'This can be due to, and may be fixed by:',
        reasonLoginPre: 'You’re trying to access a page where you need to log in for. Please press ',
        reasonLoginPost: ' and see if that takes you to the page you tried to access.',
        reasonHelp:
            'You don’t have the right authorisation to access that URL. Please check the <a href="https://support.surfconext.nl/idp-help-en" target="_blank" rel="noopener noreferrer">Help pages</a> to see who should be able to access what.',
        reasonRemoved: 'The URL you tried to access does not exist (anymore). Sorry.',
        reasonUnknown:
            'You ran into something else, need help and/or maybe we have to fix this. Send us a mail at <a href="mailto:support@surfconext.nl">support@surfconext.nl</a> so we can have a look.',
    },
    logout: {
        title: 'Logout completed successfully.',
        description_html: 'You <strong>MUST</strong> close your browser to complete the logout process.',
    },

    footer: {
        tips_or_info: 'Need tips or info?',
        help_html:
            '<a href="https://support.surfconext.nl/idp-help-en" target="_blank" rel="noopener noreferrer">Help</a>',
        surf_html: '<a href="https://www.surf.nl/en" target="_blank" rel="noopener noreferrer">SURF</a>',
        terms_html:
            '<a href="https://support.surfconext.nl/terms-en" target="_blank" rel="noopener noreferrer">Terms of Service</a>',
        contact_html: '<a href="mailto:support@surfconext.nl">support@surfconext.nl</a>',
    },

    my_idp: {
        title: 'My institution',
        general_information: 'General information',
        english: 'English',
        dutch: 'Dutch',
        roles: 'Roles',
        sub_title_html:
            'The following roles have been assigned (<a target="_blank" rel="noopener noreferrer" href="https://support.surfconext.nl/rolverdeling">more info</a>):',
        role: 'Role',
        users: 'User(s)',
        settings: 'Settings',
        settings_edit: 'Settings for my own institute',
        settings_text:
            "This section contains several settings of your institute and the application provider(s) provided to SURFconext by your institute. These settings are used in SURFconext, for instance in the Where Are You From page. If you would like to change something, please press 'Create change request'.",
        settings_text_viewer:
            'This section contains several settings of your institute and the application provider(s) provided to SURFconext by your institute. These settings are used in SURFconext, for instance in the Where Are You From page.',
        SURFconextverantwoordelijke: 'SURFconext owner',
        SURFconextbeheerder: 'SURFconext maintainer',
        'Dashboard supergebruiker': 'Dashboard Super User',
        services_title: 'Applications provided by your institute:',
        services_title_none: 'None',
        service_name: 'Application name',
        license_contact_html:
            'Primary License contact person (<a target="_blank" rel="noopener noreferrer" href="https://support.surfconext.nl/dashboard-help-nl#Beschikbaredienstenactiveren-HoekunjeopSURFconextaangeslotendienstenactiveren?">more info</a>):',
        license_contact_name: 'Name',
        license_contact_email: 'Email',
        license_contact_phone: 'Phonenumber',
        institution: 'Institution',
        services: 'Applications',
        edit: 'Create change request',
        entity_id: 'Entity ID',
        state: 'Status',
        prodaccepted: 'Production',
        testaccepted: 'Staging',
        all: 'All',
        name: {
            general: 'Name',
            en: 'Name (en)',
            nl: 'Name (nl)',
            pt: 'Name (pt)',
        },
        displayName: {
            general: 'Display name',
            en: 'Display name (en)',
            nl: 'Display name (nl)',
            pt: 'Display name (pt)',
        },
        organizationURL: {
            general: 'Organization URL',
            en: 'Organization URL (en)',
            nl: 'Organization URL (nl)',
            pt: 'Organization URL (pt)',
        },
        organizationURL_nl_tooltip: 'A URL the end user can access for more information in Dutch about the organization.',
        organizationURL_en_tooltip: 'A URL the end user can access for more information in English about the organization.',
        organizationURL_pt_tooltip: 'A URL the end user can access for more information in Portuguese about the organization.',
        organizationName: {
            general: 'Organization name',
            en: 'Organization name (en)',
            nl: 'Organization name (nl)',
            pt: 'Organization name (pt)',
        },
        organizationName_nl_tooltip: 'The official Dutch name of the organization.',
        organizationName_en_tooltip: 'The official English name of the organization.',
        organizationName_pt_tooltip: 'The official Portuguese name of the organization.',
        organizationDisplayName: {
            general: 'Organization display name',
            en: 'Organization display name (en)',
            nl: 'Organization display name (nl)',
            pt: 'Organization display name (pt)',
        },
        organizationDisplayName_nl_tooltip: 'The Dutch display name of the organization.',
        organizationDisplayName_en_tooltip: 'The English display name of the organization.',
        organizationDisplayName_pt_tooltip: 'The Portuguese display name of the organization.',
        keywords: {
            general: 'Keywords',
            en: 'Keywords (en)',
            nl: 'Keywords (nl)',
            pt: 'Keywords (pt)',
        },
        published_in_edugain: 'Published in eduGAIN',
        date_published_in_edugain: 'Date published in eduGAIN',
        logo_url: 'Logo',
        new_logo_url: 'New logo URL',
        contact: 'Contact persons',
        contact_name: {
            title: 'Contact name',
        },
        contact_email: {
            title: 'Contact email',
            tooltip:
                "Attention: you are advised to use<br>a functional email address <br><br><ul><li>- admin@your-institution.nl</li><li>- tech@your-institution.nl</li><li>- helpdesk@your-institution.nl</li></ul><br>which doesn't change when<br>someone leaves your institution.",
        },
        contact_telephone: {
            title: 'Contact telephone',
        },
        contact_type: {
            title: 'Contact type',
        },
        contact_types: {
            technical: {
                title: 'Technical:<br>',
                display: 'Technical',
                tooltip:
                    'The technical contact person of the IdP. First contact for down times, changes, and other technical affairs.<br><br>',
                alttooltip: 'suggestion: technical person for down times and changes.',
            },
            support: {
                title: 'Support:<br>',
                display: 'Support',
                tooltip:
                    'This address is referred to when end users are having difficulty logging in. Generally this is the help desk of the institution.<br><br>',
                alttooltip: 'suggestion: application desk of the institution.',
            },
            help: {
                title: 'Support:<br>',
                display: 'Support',
                tooltip:
                    'This address is referred to when end users are having difficulty logging in. Generally this is the help desk of the institution.<br><br>',
                alttooltip: 'suggestion: application desk of the institution.',
            },
            administrative: {
                title: 'Administrative:<br>',
                display: 'Administrative',
                tooltip:
                    "The administrative contact person of the IdP. This tends to be the person filling the role of 'SURFconext-verantwoordelijke'<br><br>",
                alttooltip: "suggestion: person with the role 'SURFconext-verantwoordelijke'",
            },
            other: {
                title: 'Other:<br>',
                display: 'Other',
                tooltip: 'Unclassified other contact person<br><br>',
                alttooltip: 'suggestion: None',
            },
            billing: {
                title: 'Billing:<br>',
                display: 'Billing',
                tooltip:
                    'The billing contact person of the IdP. This tends to be the person of the financial department<br><br>',
                alttooltip: 'suggestion: financial department of the institution',
            },
        },
        description: {
            general: 'Description',
            en: 'Description (en)',
            nl: 'Description (nl)',
            pt: 'Description (pt)',
        },
        edit_message: 'You are able to edit the following fields.',
        save: 'Create change request',
        change_request_created:
            'Change request sent to the SURFconext-team. The ticket number of the change request is {{jiraKey}}',
        no_change_request_created: 'No change request is created as you did not change anything.',
        change_request_failed: 'Failed to create your change request.',
        comments: 'Comments',
    },

    policies: {
        confirmation: "Are your sure you want to remove policy '{{policyName}}?'",
        flash: "Authorization policy '{{policyName}}' was successfully {{action}}",
        flash_created: 'created',
        flash_deleted: 'deleted',
        flash_first:
            'Authorization policies are not yet applied for this application. Before policies for this application will be applied, the SURFconext Team must manually perform a configuration change. A notification has been sent to the SURFconext Team. They will get in touch with you.',
        flash_updated: 'updated',
        new_policy: 'New authorization policy',
        no_policies: 'There are no policies for this application.',
        how_to: 'How-to',
        policy_name_not_unique_exception: 'This policy name is already in use',
        pdp_unreachable: 'PDP unreachable',
        pdp_unreachable_description: 'Currently unable to fetch the policies from PDP. Please try again later.',
        pdp_active_info: 'Click to read more about when your rule is active.',
        pdp_active_link: 'https://support.surfconext.nl/pdp-rule-active-after',
        overview: {
            active: 'Active',
            description: 'Description',
            header: 'Authorization policies',
            identityProviderNames: 'Institution(s)',
            inactive: 'Inactive',
            name: 'Name',
            numberOfRevisions: 'Revisions',
            search: 'Search',
            search_hint: 'Filter by name',
            serviceProviderName: 'Application',
        },
        showMore: "Show deny messages",
        showLess: "Hide deny messages"
    },

    policy_attributes: {
        attribute: 'Attribute',
        attribute_value_placeholder: 'Attribute value...',
        group_info:
            " The value(s) must be fully qualified group IDs e.g. 'urn:collab:group:surfteams.nl:nl:surfnet:diensten:admins'",
        new_attribute: 'Add attribute',
        new_value: '+ Add value',
        sab_info: " The value(s) must be valid roles in SAB e.g. 'Instellingsbevoegde'",
        values: 'Value(s)',
        help_link: 'https://support.surfconext.nl/dashboard-help-attributes',
        attributeTooltip: 'Click to read more about attributes.',
    },

    policy_detail: {
        about: 'About',
        access: 'Policy type',
        access_denied_message: '"Access Denied" message',
        activate_policy: 'Activate this policy',
        deactivate_policy: 'Deactivate this policy',
        active: 'Active policy',
        inactive: 'Inactive policy',
        attribute: 'Attribute(s)',
        autoFormat: 'Auto generate',
        cancel: 'Cancel',
        confirmation: 'Are your sure you want to leave this page?',
        create_policy: 'Create new authorization policy',
        deny: 'Deny',
        deny_info:
            'Deny policies are less common to use. If the attributes in the policy match those of the person trying to log in then this will result in a Deny. No match will result in a Permit.',
        deny_message: 'English message',
        deny_message_info: 'This is the message displayed to the user if access is denied based on this policy.',
        deny_message_nl: 'Dutch message',
        deny_message_pt: 'Portuguese message',
        description: 'Rule description',
        idps_placeholder: 'Select the Identity Providers - zero or more',
        institutions: 'Institutions',
        intro:
            'Define who can access this application. Need help? <a href="https://support.surfconext.nl/dashboard-help-pdp" target="_blank" rel="noopener noreferrer">Read our manual.</a>',
        isActive: 'Active',
        isActiveDescription: 'Mark the authorization policy active',
        isActiveInfo: ' Inactive authorization policies are not evaluated in enforcement decisions',
        name: 'Name',
        permit: 'Permit',
        permit_info:
            'Permit policies enforce that a only a successful match of the attributes defined will result in a Permit. No match will result in a Deny.',
        rule: 'Rule',
        rules: 'Rules',
        rule_and: 'AND',
        rule_and_info: 'Policies with a logical AND rule enforce that all attributes defined must match those of the person trying to log in.',
        rule_info_add: ' Note that attribute values with the same attribute name always be evaluated with the logical OR.',
        rule_info_add_2:
            'Note that a Deny access authorization policy always and implicitly uses the logical AND for different attribute names.',
        rule_or: 'OR',
        rule_or_info:
            'Policies defined with a logical OR only require one of the attributes to match the attributes of the person requesting access.',
        save_changes: 'Save changes',
        service: 'Application',
        spScopeInfo: "The available applications are scoped to your applications if you don't select an Institution",
        sp_placeholder: 'Select the application provider - required',
        sub_title: 'Created by {{displayName}} on {{created}}',
        submit: 'Submit',
        update_policy: 'Update authorization policy',
    },
    revisions: {
        active: 'Active',
        allAttributesMustMatch: 'Logical OR rule?',
        attributes: 'Attributes',
        changes_first_html:
            'This is the first <span class="curr">initial revision {{currRevisionNbr}}</span> created by {{userDisplayName}} from {{authenticatingAuthorityName}} on {{createdDate}}.',
        changes_info_html:
            'Showing the changes between <span class="prev"> revision number {{prevRevisionNbr}}</span> and <span class="curr">revision number {{currRevisionNbr}}</span> made by {{userDisplayName}} from {{authenticatingAuthorityName}} on {{createdDate}}.',
        denyAdvice: 'Deny message in English',
        denyAdviceNl: 'Deny message in Dutch',
        denyAdvicePt: 'Deny message in Portuguese',
        denyRule: 'Access Permit rule?',
        description: 'Description',
        identityProviderNames: 'Institution(s)',
        name: 'Name',
        revision: 'Revision number',
        serviceProviderName: 'Application',
        serviceProviderNames: 'Application(s)',
        title: 'Revisions',
        intro_1:
            'Every time a policy gets updated a copy of the previous state is stored as a revision of the new policy. By comparing revisions of a policy with each other and with the most current policy we are able to display an audit log of all changes made to a policy.',
        intro_2: 'When a policy is deleted then all of the revisions of that policy - if any - are also deleted.',
    },

    history: {
        header: 'Tickets',
        filter: 'Filter',
        last_updated: 'Last updated:',
        no_results: 'No results',
        info: 'On this page you find all tickets related to (dis)connecting applications and change requests.',
        moreAwaitingTickets:
            "Not all 'Waiting for customer' tickets are shown because the period in the search filter is not broad enough.",
        requestDate: 'Created',
        updateDate: 'Updated',
        type: 'Type',
        jiraKey: 'Id',
        status: 'Status',
        message: 'Message',
        userName: 'By',
        spName: 'Application',
        action_types_name: {
            LINKREQUEST: 'New Connection',
            UNLINKREQUEST: 'Disconnect',
            QUESTION: 'Question',
            CHANGE: 'Change request',
            LINKINVITE: 'Connection Invite',
            UNLINKINVITE: 'Disconnect Invite',
        },
        from: 'From',
        to: 'To',
        typeIssue: 'Type',
        spEntityId: 'Application',
        statuses: {
            all: 'All tickets',
            'Open': 'Open',
            'In Progress': 'In progress',
            'On Hold': 'On Hold',
            'Waiting for customer': 'Pending input',
            'Waiting for Acceptance': 'Waiting for Acceptance',
            Resolved: 'Resolved',
            Reopened: 'Reopened',
            Closed: 'Closed',
            undefined: 'Undefined',
        },
        resolution: {
            no_change_required: 'No Change required',
            no_change_requiredTooltip: 'The ticket needed no change.',
            incomplete: 'Incomplete',
            incompleteTooltip: 'The ticket is incomplete.',
            done: 'Done',
            doneTooltip: 'The ticket is resolved.',
            wont_do: "Won't be fixed",
            wont_doTooltip: 'The ticket will not be fixed.',
            cancelled: 'Cancelled',
            cancelledTooltip:
                'The ticket was cancelled. If the ticket was an invite for a connection the Institution has denied the invite.',
            wont_fix: "Won't be fixed",
            wont_fixTooltip: 'The ticket will not be fixed.',
            resolved: 'Resolved',
            resolvedTooltip: 'The ticket was successfully resolved.',
            duplicate: 'Duplicate',
            duplicateTooltip: 'The ticket was a duplicate of another ticket.',
            not_completed: 'Not completed',
            not_completedTooltip: 'The ticket was not completed.',
            cannot_reproduce: 'Can not be reproduced',
            cannot_reproduceTooltip: 'The issue described in the ticket could not reproduced',
            suspended: 'Suspended',
            suspendedTooltip: 'The ticket was suspended.',
        },
        servicePlaceHolder: 'Search and select an application...',
        noTicketsFound: 'No tickets were found for the given filters.',
        viewInvitation: 'Approve / Deny',
        resendInvitation: 'Resend invitation mail',
        resendInvitationConfirmation: 'Are you sure you want to resend the invitation mail?',
        resendInvitationFlash: 'Invitation mail for {{jiraKey}} was resent',
        serviceDetails: 'View application details',
    },
    service_filter: {
        title: 'Filter applications',
        state: {
            tooltip: 'The status of the application determines if the application is visible on the production platform.',
        },
        search: 'Search applications...',
    },
    invite_request: {
        info: 'An invite request results in a mail send to all selected contactpersons of an Institution with an invite to connect to the selected application or disconnect from the selected application. A <span class="emphasize">Connection Invite</span> or <span class="emphasize">Disconnection Invite</span> Jira ticket is created with the status <span class="emphasize">Waiting for customer</span>.',
        selectIdp: 'Search and select an Institution...',
        selectSpDisabled: 'First select an Institution',
        selectSp: 'Now search and select the application...',
        idp: 'Institution',
        sp: 'Application',
        connectionRequestQuestion: "Invite for a connection request or disconnection request?",
        connectionRequest: "Connection request",
        disConnectionRequest: "Disconnection request",
        contactPersons: 'Select to which contact persons of {{name}} the invite will be sent.',
        sourcePersons: 'Contact persons from {{source}}',
        additionalPersons: 'Additional contact persons',
        selectContact: 'Select',
        sendRequest: 'Submit',
        reset: 'Reset',
        message: 'An - optional - message for the invite recipients.',
        jiraFlash:
            'A Jira ticket has been created with key {{jiraKey}}. When one of the recipients accepts the invite then it will be logged in the comments of {{jiraKey}}.',
        jiraError: 'Jira responded with an error. Please check the logs or try again later',
        resend: "Ticket was created at {{date}} and emails were send to {{emailTo}}. Current status is '{{status}}'.",
    },
    profile: {
        title: 'Profile',
        sub_title:
            'The following profile data has been provided by your home institution. This data as well as your group membership data (e.g.SURFconext Teams) will be stored in SURFconext and shared with applications accessed via SURFconext.',
        my_attributes: 'My attributes',
        attribute: 'Attribute',
        value: 'Value',
        my_roles: 'My roles',
        my_roles_description: 'The following roles have been assigned:',
        role: 'Role',
        role_description: 'Description',
        roles: {
            ROLE_DASHBOARD_ADMIN: {
                name: 'SURFconext owner',
                description: 'You are authorized on behalf of your institution to manage the application connections',
            },
            ROLE_DASHBOARD_VIEWER: {
                name: 'SURFconext maintainer',
                description: 'You are authorized on behalf of your institution to view the information about the applications',
            },
            ROLE_DASHBOARD_MEMBER: {
                name: 'Institution member',
                description: 'You are a regular member of the dashboard',
            },
            ROLE_DASHBOARD_SUPER_USER: {
                name: 'Dashboard Super User',
                description: 'You are the super user of the dashboard',
            },
        },
        attribute_map: {
            uid: {
                name: 'UID',
                description: 'your unique username within your organization',
            },
            'Shib-surName': {
                name: 'Surname',
                description: 'your surname',
            },
            'Shib-givenName': {
                name: 'Name',
                description: 'your name',
            },
            'Shib-commonName': {
                name: 'Full Name',
                description: 'your full name',
            },
            'Shib-orgUnit': {
                name: 'Organizational Unit',
                description: 'department or unit name',
            },
            displayName: {
                name: 'Display Name',
                description: 'display name as shown in applications',
            },
            'Shib-InetOrgPerson-mail': {
                name: 'E-mailaddress',
                description: 'your e-mailaddress as known within your organization',
            },
            'Shib-eduPersonAffiliation': {
                name: 'Relation',
                description: 'relation between your and your organization',
            },
            'Shib-eduPersonScopedAffiliation': {
                name: 'Scoped relation',
                description: 'scoped relation between your and your organization',
            },
            eduPersonEntitlement: {
                name: 'Entitlement',
                description: 'entitlement which decides upon your authorization within the application',
            },
            'Shib-eduPersonPN': {
                name: 'Net-ID',
                description: 'your unique username within your organization augmented with @organizationname.nl',
            },
            'Shib-preferredLanguage': {
                name: 'Preferred Language',
                description: 'a two letter abbreviation according to ISO 639; no subcodes',
            },
            schacHomeOrganization: {
                name: 'Organization',
                description: 'name for the organization, making use of the domain name of the organization conform RFC 1035',
            },
            'Shib-schacHomeOrganizationType': {
                name: 'Type of Organization',
                description: 'type of organization to which the user belongs',
            },
            'Shib-schacPersonalUniqueCode': {
                name: 'Personal unique code',
                description: 'these values are used to express specific types of identification number',
            },
            'Shib-nlEduPersonHomeOrganization': {
                name: 'Display name of Organization',
                description: 'display name of the organization',
            },
            'Shib-nlEduPersonOrgUnit': {
                name: 'Unitname',
                description: 'unit name',
            },
            'Shib-nlEduPersonStudyBranch': {
                name: 'Study Branch',
                description: 'study branch; numeric string which contains the CROHOcode. can be empty if the branch is unknown',
            },
            'Shib-nlStudielinkNummer': {
                name: 'Studielinknummer',
                description: 'studielinknummer of the student as registered at www.studielink.nl',
            },
            'Shib-nlDigitalAuthorIdentifier': {
                name: 'DAI',
                description: 'Digital Author Identifier (DAI)',
            },
            'Shib-userStatus': {
                name: 'Userstatus',
                description: 'Status of this user in SURFconext',
            },
            'Shib-accountstatus': {
                name: 'Accountstatus',
                description: 'Status of this account in SURFconext',
            },
            'name-id': {
                name: 'Identifier',
                description: 'Status of this account in SURFconext',
            },
            'Shib-voName': {
                name: 'Virtual Organisation Name',
                description: 'The name of the Virtual Organisation for which you have authenticated',
            },
            'Shib-user': {
                name: 'Identifier',
                description: 'Status of this account in SURFconext',
            },
            'is-member-of': {
                name: 'Membership',
                description: 'Membership of Virtual Organizations and SURFconext.',
            },
            'Shib-surfEckid': {
                name: 'SURF EDU-K',
                description: 'Educatieve Content Keten Identifier (ECK ID) is a pseudonymous identifier.',
            },
            'Shib-surf-autorisaties': {
                name: 'Authorizations',
                description: 'The authorizations from SURF.',
            },
        },
    },
    stepup: {
        title: 'Multi-factor authentication',
        info: 'In order to perform this action it is required to add a second factor to your authentication. When you proceed you will be asked for your token and then you will return here again.',
        cancel: 'Cancel',
        go: 'Proceed'
    },
    mfa_panel: {
        title: 'MFA',
        subtitle:
            'With <a href="https://support.surfconext.nl/mfa-nl" target="_blank" rel="noopener noreferrer">MFA</a> you can enforce the MultiFactor Authentication of your own IdP for this service if your IdP supports this.',
        subtitle2:
            'You can either choose <span>Generic multi-factor (REFEDS MFA)</span>, an international standard, you can choose <span>Multi-factor on ADFS or Azure AD</span> which is specific for Microsoft products.',
        subtitle3:
            'If your IdP does not support requesting a second factor, then consider using SURFsecureID.',
        authn_context_level: 'MFA Setting',
        defaultAuthnContextLevel: 'Do not force MFA at my IdP for this application',
        multipleauthn: 'Multi-factor on ADFS or Azure AD',
        mfa: 'Generic multi-factor (REFEDS MFA)',
        transparent_authn_context: "Transparent Authn context",
        mobileonefactorcontract: "Mobile one factor",
        multipleauthn_short: 'Microsoft IdP',
        mfa_short: 'Generic IdP',
        mobileonefactorunregistered: 'Mobile one unregistered',
        password: 'Password',
        not_allowed: "You can not change the MFA for this application. Please contact <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a> if you want to change the MFA for this application."
    },

}
