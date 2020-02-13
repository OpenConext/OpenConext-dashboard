// Interpolation works as follows:
//
// Make a key with the translation and enclose the variable with {{}}
// ie "Hello {{name}}" Do not add any spaces around the variable name.
// Provide the values as: I18n.t("key", {name: "John Doe"})

import I18n from "i18n-js";

I18n.translations.nl = {
    code: "NL",
    name: "Nederlands",
    select_locale: "Selecteer Nederlands",

    boolean: {
        yes: "Ja",
        no: "Nee"
    },

    browser_not_supported: {
        title: "Uw browser wordt niet ondersteund.",
        description_html: "Uw versie van Internet Explorer wordt niet ondersteund. Update uw browser naar een modernere versie."
    },

    header: {
        welcome: "Welkom,",
        links: {
            help_html: "<a href=\"https://support.surfconext.nl/dashboard-help-nl#Beschikbaredienstenactiveren-HandleidingSURFconextDashboard\" target=\"_blank\" rel=\"noopener noreferrer\">Help</a>",
            logout: "Uitloggen",
            exit: "Exit"
        },
        you: "Jij",
        profile: "Profiel",
        switch_idp: "Kies IDP",
        super_user_switch: "Switch identiteit",
        welcome_txt: "Welkom bij het SURFconext dashboard. Op deze pagina zie je welke diensten er op SURFconext gekoppeld zijn: als je instelling dat ook toestaat, kun je op die diensten met je instellingsaccount inloggen. Na inloggen op dit dashboard (rechtsbovenin) zie je informatie meer gericht op je instelling. Meer info? Klik op 'Help' rechtsbovenin."
    },
    confirmation_dialog: {
        title: "Bevestig",
        confirm: "Bevestig",
        cancel: "Annuleer",
        leavePage: "Weet u zeker dat u deze pagina wilt verlaten?",
        leavePageSub: "Veranderingen die u heeft gemaakt zullen niet worden opgeslagen.",
        stay: "Blijf",
        leave: "Annulleer"
    },

    navigation: {
        apps: "Services",
        policies: "Autorisatieregels",
        history: "Tickets",
        stats: "Statistieken",
        my_idp: "Mijn instelling",
        invite_request: "Uitnodiging"
    },

    loader: {
        loading: "Alle services worden ingeladen"
    },

    facets: {
        title: "Filters",
        reset: "Reset",
        refresh: "Verversen",
        download: "Export overzicht",
        unknown: "Unknown",
        totals: {
            all: "Alle {{total}} services worden weergegeven",
            filtered: "{{count}} van de {{total}} services worden weergegeven"
        },
        static: {
            connection: {
                all: "Alle",
                has_connection: "Ja",
                name: "Dienst gekoppeld",
                no_connection: "Nee",
            },
            license: {
                name: "Licentie",
                unknown: "Onbekend",
                not_needed: "Niet nodig",
                has_license_sp: "Ja, bij service provider",
                has_license_surfmarket: "Ja, bij SURFmarket"
            },
            used_by_idp: {
                all: "Alle",
                name: "Aangeboden door mijn instelling",
                no: "Nee",
                yes: "Ja",
            },
            published_edugain: {
                all: "Alle",
                name: "Gepubliceerd in eduGAIN federatie",
                no: "Nee",
                yes: "Ja",
            },
            interfed_source: {
                tooltip: "Sommige via SURFconext beschikbare diensten zijn primair aangesloten op een met SURFconext gekoppelde federatie. Hier kunt u evt selecteren per federatie.",
                name: "Federatie bron",
                surfconext: "SURFconext",
                edugain: "eduGAIN",
                entree: "Entree"
            },
            entity_category: {
                name: "eduGAIN Entity Categorie",
                tooltip: "Diensten kunnen voldoen aan 'entity categories'.<br>Zie de <a href=\"https://support.surfconext.nl/dashboard-help-entitycategories\" target=\"_blank\" rel=\"noopener noreferrer\">wiki</a> voor meer informatie. Hier kunt u evt diensten filteren die voldoen aan een bepaalde entity category.",
                code_of_conduct: "Code of Conduct",
                research_and_scholarship: "Research and Scholarship"
            },
            strong_authentication: {
                name: "SURFsecureID ingeschakeld",
                tooltip: "SURFsecureID second factor authentication is vereist.<br>Zie de <a href=\"https://edu.nl/p4um4\" target=\"_blank\" rel=\"noopener noreferrer\">wiki</a> voor meer informatie.",
                none: "Geen",
            },
            attribute_manipulation: {
                name: "Attribuut manipulatie script",
                yes: "Ja",
                no: "Nee"
            },
            arp: {
                name: "Vrijgegeven attributen",
                tooltip: "Meer info over deze attributen vindt u in de <a href=\"https://wiki.surfnet.nl/display/surfconextdev/Attributes+in+SURFconext \" target=\"_blank\" rel=\"noopener noreferrer\">wiki</a>.",
                info_html: "Mogelijkerwijs worden er meer attributen aan de Dienst geleverd door zogeheten attribuut manipulatie."
            },
            type_consent: {
                tooltip: "Op welke manier wordt aan nieuwe gebruikers toestemming gevraagd voordat ze toegang krijgen. Zie de <a target=\"_blank\" rel=\"noopener noreferrer\" href=\"https://support.surfconext.nl/dashboard-help-consent\">wiki</a> voor meer informatie.",
                name: "Type toestemming",
                no_consent: "Geen toestemming",
                minimal_consent: "Minimale toestemming",
                default_consent: "Default toestemming",
            }
        }
    },

    apps: {
        detail: {
            application_usage: "Service gebruik",
            attribute_policy: "Attributen",
            close_screenshot: "Sluiten",
            how_to_connect: "Dienst koppelen",
            how_to_disconnect: "Dienst ontkoppelen",
            idp_usage: "Gebruikt door",
            license_data: "Licentie",
            overview: "Overzicht",
            sirtfi_security: "Sirtfi contacten",
            ssid: "SURFsecureID",
            privacy: "Privacy",
            consent: "Consent",
            back: "Terug",
            outstandingIssue: "Er is al een openstaand ticket {{jiraKey}} van het type {{type}} en status {{status}} voor deze dienst.",
            inviteAlreadyProcessed: "De uitnodiging voor ticket {{jiraKey}} is reeds {{action}}.",
            outstandingIssueLink: " Ga naar de <a class=\"link\" href=\"{{link}}\">{{linkName}}</a> sectie om de uitnodiging te accepteren / weigeren.",
            approved: "goedgekeurd",
            denied: "afgewezen"

        },
        overview: {
            connect: "",
            connect_button: "Activeren",
            connected: "Dienst gekoppeld",
            dashboardConnectOption: "Automatisch koppelen",
            license: "Licentie afgesloten",
            licenseStatus: "Licentie vereist",
            aansluitovereenkomstRefused: "Aansluitovereenkomst",
            contractualBase: "Contractuele basis",
            license_present: {
                na: "n.v.t.",
                no: "Nee",
                unknown: "Onbekend",
                yes: "Ja",
            },
            license_unknown: "Onbekend",
            name: "Service",
            no_results: "Geen services beschikbaar",
            processing_results: "Alle services worden opgehaald...",
            search: "Zoek",
            search_hint: "Zoeken",
            add_services_hint: "Is de dienst die je zoekt niet te vinden? Stuur dan je contact bij die dienst een mail dat je de dienst graag wil gebruiken, maar dan wel via <a href=\"http://support.surfconext.nl/getconexted\" target=\"_blank\" rel=\"noopener noreferrer\">SSO van SURFconext</a> zodat je in kunt loggen met je instellingsaccount, en dat dat veiliger en handiger voor iedereen is. Je kunt vermelden dat de dienst daarmee ook aantrekkelijker wordt voor andere instellingen, ook buiten Nederland. Met het verzoek of de dienst contact op kan nemen via support@surfconext.nl om de koppeling met SURFconext te realiseren."
        },
    },

    app_meta: {
        question: "Heb je een vraag?",
        eula: "Algemene voorwaarden",
        website: "Website",
        support: "Support pagina",
        login: "Login pagina",
        registration_info_html: "Deze Service Provider is beschikbaar in SURFconext via <a href=\"https://support.surfconext.nl/edugain\" target=\"_blank\" rel=\"noopener noreferrer\">eduGAIN</a>. De Service Provider is door de volgende federatie geregistreerd: <a href=\"{{url}}\" target=\"_blank\" rel=\"noopener noreferrer\">{{url}}</a>.",
        registration_policy: "Registratie beleid",
        privacy_statement: "Privacyverklaring",
        metadata_link: "Metadata"
    },

    license_info_panel: {
        title: "Licentie-informatie",
        has_license_surfmarket_html: "Er is via <a href=\"https://www.surfmarket.nl\" target=\"_blank\" rel=\"noopener noreferrer\">SURFmarket</a> een licentie beschikbaar voor deze service.",
        has_license_sp_html: "Een licentie voor <a href=\"{{serviceUrl}}\" target=\"_blank\" rel=\"noopener noreferrer\">{{serviceName}}</a> kan via de aanbieder van deze dienst worden afgesloten.",
        has_license_sp_html_no_service_url: "Een licentie voor {{serviceName}}</a> kan via de aanbieder van deze dienst worden afgesloten.",
        no_license_html: "Jouw instelling heeft voor deze service geen licentie via <a href=\"https://www.surfmarket.nl\" target=\"_blank\" rel=\"noopener noreferrer\">SURFmarket</a>.",
        not_needed_html: "Voor deze dienst is geen licentie nodig",
        unknown_license: "Het is onbekend welke licentie voor deze service geldt.",
        no_license_description_html: "" +
            "<ul>" +
            "   <li>Laat de licentiecontactpersoon van jouw instelling een licentie afsluiten bij <a href=\"https://www.surfmarket.nl\" target=\"_blank\" rel=\"noopener noreferrer\">SURFmarket</a></li>" +
            "</ul>" +
            "<br />In sommige gevallen is de licentie direct bij de aanbieder van de service afgesloten.",
        unknown_license_description_html: "Er zijn verschillende opties:" +
            "<ul>" +
            "   <li>SURF of een andere instelling biedt deze service gratis aan.</li>" +
            "   <li>De licentie moet direct bij de aanbieder van de service worden afgesloten.</li>" +
            "   <li>De licentie is nog niet bijgewerkt in de administratie van <a href=\"https://www.surfmarket.nl\" target=\"_blank\" rel=\"noopener noreferrer\">SURFmarket</a>.</li>" +
            "</ul>" +
            "<p>SURFnet zal, indien nodig, contact opnemen met de aanbieder of <a href=\"https://www.surfmarket.nl\" target=\"_blank\" rel=\"noopener noreferrer\">SURFmarket</a> alvorens de koppeling te activeren.</p>"
    },

    license_info: {
        unknown_license: "Geen licentie-informatie beschikbaar",
        has_license_surfmarket: "Licentie beschikbaar via SURFmarket",
        has_license_sp: "Licentie nodig (via service supplier)",
        no_license: "Licentie is niet aanwezig",
        no_license_needed: "Geen licentie nodig",
        license_info: "Lees hoe je een licentie kunt verkrijgen",
        license_unknown_info: "Lees meer",
        valid: "Licentie is geldig t/m {{date}}"
    },

    overview_panel: {
        wiki_info_html: "Voor deze service is extra informatie beschikbaar in de SURFconext <a href=\"{{link}}\" target=\"_blank\" rel=\"noopener noreferrer\">wiki</a>.",
        no_description: "Er is geen beschijving voor deze service.",
        description: "Beschrijving",
        has_connection: "Dienst gekoppeld",
        no_connection: "Dienst niet gekoppeld",
        how_to_connect: "Lees hoe je een dienst koppelt",
        disconnect: "Lees hoe je een dienst ontkoppelt",
        normen_kader: "Informatie inzake AVG/GDPR",
        normen_kader_html: "Voor deze dienst heeft de leverancier informatie verstrekt over welke data ze verwerkt, waar ze dat doet etc. De informatie vindt u op de <a href=\"https://support.surfconext.nl/dashboard-info-avg\" target=\"_blank\" rel=\"noopener noreferrer\">wiki</a>. In de loop van 2018 gaan we die informatie ook verwerken in een nieuwe versie van dit dashboard.",
        no_normen_kader_html: "Voor deze dienst heeft de leverancier nog geen AVG/GDPR informatie aangeleverd; informatie over welke data ze verwerken, waar ze dat doen etc. kunt u opvragen bij de leverancier.",
        single_tenant_service: "Single tenant dienst",
        single_tenant_service_html: "{{name}} is een single-tenant-dienst, wat wil zeggen dat de leverancier een aparte instantie moet aanmaken voor elke klant voordat deze de dienst kan gebruiken. Zie de <a href=\"https://support.surfconext.nl/dashboard-info-singletenant\" target=\"_blank\" rel=\"noopener noreferrer\">SURFnet wiki</a> voor meer informatie over single tenant diensten.",
        interfed_source: "Federatie bron:",
        publish_in_edugain_date: "Gepubliceerd in eduGAIN op:",
        supports_ssa: "SURFsecureID aangezet",
        minimalLoaLevel: "Voor het inloggen op deze dienst is authenticatie met een tweede factor middels SURFsecureID vereist. Alle gebruikers moeten een token gebruiken van minimaal zekerheidsniveau (Level of Assurance / LoA): <code>{{minimalLoaLevel}}</code>. Voor meer informatie zie de <a href=\"https://edu.nl/8nm6h\" target=\"_blank\" rel=\"noopener noreferrer\">wiki</a>",
        minimalLoaLevelIdp: "Voor het inloggen op deze dienst is authenticatie met een tweede factor middels SURFsecureID vereist. Alle gebruikers van uw instelling moeten een token gebruiken van minimaal zekerheidsniveau (Level of Assurance / LoA): <code>{{minimalLoaLevel}}</code>. Voor meer informatie zie de <a href=\"https://edu.nl/8nm6h\" target=\"_blank\" rel=\"noopener noreferrer\">wiki</a>",
        supportsSsaTooltip: "Diensten kunnen ook dynamisch een LOA veroeken tijdens authenticatie.",
        entity_categories: "Ondersteunde Entity Categories",
        entity_category: {
            "http://wwwgeantnet/uri/dataprotection-code-of-conduct/v1": "GÉANT Data Protection Code of Conduct",
            "http://refedsorg/category/research-and-scholarship": "Research and Scholarship"
        },
        aansluitovereenkomst: "Aansluitovereenkomst",
        aansluitovereenkomstRefused: "Deze dienst heeft geweigerd om de 'SURFconext aansluitovereenkomst' te tekenen. Lees meer over deze overeenkomst op de <a href=\"https://support.surfconext.nl/dashboard-info-trust\" target=\"_blank\" rel=\"noopener noreferrer\">SURF wiki</a>.",
        privacyInformation: "Privacy-informatie",
        privacyInformationInfo: "De leverancier heeft geen privacy-informatie aangeleverd.",
        contractualBase: {
            na: "Voor deze SP is geen informatie beschikbaar over de contractuele basis : mail naar <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a> als u daarover vragen heeft.",
            ao: "Aanbieder heeft de SURFconext aansluitovereenkomst getekend.",
            ix: "Dienst aangeboden door op SURFconext aangesloten instelling.",
            "r&s+coco" :"eduGAIN-dienst die zich akkoord heeft verklaard met de Data Protection Code of Conduct en valt in de Research & Scholarship entity category.",
            entree: "Aangsloten op de Kennisnet Entree-federatie.",
            clarin: "Onderdeel van de Clarin-onderzoeksfederatie."
        },
        contractualBaseWiki: " Voor meer informatie zie de <a href=\"https://edu.nl/c83yx\" target=\"_blank\" rel=\"noopener noreferrer\">wiki</a>."
    },

    attributes_policy_panel: {
        arp: {
            noarp: "Er is geen 'Attribute Release Policy' bekend. Alle bekende attributen worden uitgewisseld.",
            noattr: "Er zullen geen attributen worden uitgewisseld met {{name}}.",
            manipulation: "Voor deze Service Provider is er een aangepast 'attribuut manipulatie script' actief. SURFconext voert het script uit bij elke authenticatie van een gebruiker, voordat attributen aan die service worden vrijgegeven. Om u te helpen begrijpen welke informatie zal worden vrijgegeven, vindt u hieronder een beschrijving van wat het script doet:",
            resourceServers: "Deze Service Provider is gekoppeld aan Resource Servers en daarom zijn alle attributen die worden vrijgegeven ook opvraagbaar voor de volgende Resource Servers:"
        },
        attribute: "Attribuut",
        hint: "De attributen en hun waarden voor jouw persoonlijke account worden getoond. Dit is misschien niet representatief voor andere accounts binnen jouw instelling.",
        subtitle: "{{name}} wil de volgende attributen ontvangen.",
        title: "Attributen",
        your_value: "Jouw waarde",
        filter: "Voor dit attribuut zijn de volgende filters toegepast:",
        motivationInfo: "De kolom ‘motivatie‘ bevat, voor zover beschikbaar, de uitleg van de leverancier over waarom ze dat attribuut willen ontvangen.",
        motivation: "Motivatie",
        no_attribute_value: "<geen waarde ontvangen>",
        attribute_value_generated: "<wordt door SURFconext gegenereerd>",
        filterInfo: "Om te zorgen dat een leverancier alleen de voor de dienst noodzakelijke gegevens ontvangt, gebruikt SURFconext soms filters op de waardes van de IdP ontvangen attributen.",
        warning: "Let op:",
        nameIdInfo: "De identiteit van de gebruikers is doorgegeven als een NameID element van het type '%{type}' - <a href='https://support.surfconext.nl/uids' target='_blank'>gegenereerd door SURFconext</a>"
    },

    idp_usage_panel: {
        title: "Gebruikt door",
        subtitle: "De volgende instellingen zijn gekoppeld aan {{name}}.",
        subtitle_none: "Er zijn geen instellingen gekoppeld aan {{name}}.",
        subtitle_single_tenant: "Als u wil weten door welke andere instellingen {{name}} via SURFconext wordt gebruikt, kunt u dat opvragen via support@surfconext.nl.",
        institution: "Instelling"
    },

    sirtfi_panel: {
        title: "De Sirtfi contact personen voor {{name}}",
        subtitle: "Het Security Incident Response Trust Framework voor Federated Identity <a href=\" https://refeds.org/sirtfi\" target=\"_blank\" rel=\"noopener noreferrer\">(Sirtfi)</a> heeft als doel om de coördinatie van incidenten in federatieve organisaties te faciliteren. Dit vertrouwensraamwerk bestaat uit een lijst van maatregelen die een organisatie kan implementeren teneinde Sirtfi compliant te zijn.",
        contactPersons: "In het geval van een beveiligingsincident kan deze service het beste op de volgende manier worden gecontacteerd:",
        cp_name: "Naam",
        cp_email: "Email",
        cp_telephoneNumber: "Telefoonnummer",
        cp_type: "Type",
        cp_type_translate_technical: "Technical",
        cp_type_translate_administrative: "Administrative",
        cp_type_translate_help: "Support",
        cp_type_translate_support: "Support"
    },
    privacy_panel: {
        title: "Privacy informatie",
        subtitle: "SURF geeft leveranciers de kans informatie te delen die van belang is in het kader van de AVG. Indien beschikbaar, vind je deze informatie hieronder. Voor ontbrekende informatie kun je contact opnemen met de leverancier.",
        subtitle2: "De leverancier van de service {{name}} heeft SURFnet voorzien van de volgende informatie:",
        question: "Vraag",
        answer: "Antwoord",
        accessData: "WIE HEEFT TOEGANG TOT DE DATA?",
        certification: "KAN DE LEVERANCIER EEN MEMORANDUM VAN DERDEN OVERLEGGEN?",
        certificationLocation: "WAAR KAN EEN INSTELLING DEZE VINDEN / AANVRAGEN?",
        country: "IN WELK LAND WORDEN DE GEGEVENS OPGESLAGEN?",
        otherInfo: "OVERIGE PRIVACYGEGEVENS EN BEVEILIGINGSINFORMATIE",
        privacyPolicy: "HEEFT DE LEVERANCIER EEN PRIVACYBELEID GEPUBLICEERD?",
        privacyPolicyUrl: "WAT IS DE URL VAN HET PRIVACYBELEID?",
        securityMeasures: "WELKE BEVEILIGINGSMAATREGELEN HEEFT DE LEVERANCIER GENOMEN?",
        snDpaWhyNot: "ZO NEE, WELKE SECTIES ZIJN PROBLEMATISCH EN WAAROM?",
        surfmarketDpaAgreement: "IS DE LEVERANCIER AKKOORD GEGAAN MET DE DPA VAN SURFMARKET?",
        surfnetDpaAgreement: "IS DE LEVERANCIER BEREID OM HET SURF-MODEL DPA TE ONDERTEKENEN?",
        whatData: "WELKE (SOORT) GEGEVENS WORDEN VERWERKT?",
        certificationValidFrom: "CERTIFICERING GELDIG VAN",
        certificationValidTo: "CERTIFICERING GELDIG TOT",
        noInformation: "Geen informatie van de leverancier"
    },
    consent_panel: {
        title: "Toestemming",
        subtitle: "Nieuwe gebruikers wordt standaard om toestemming gevraagd voor het doorgeven van hun persoonlijke gegevens.",
        subtitle2: "Optioneel kun je een extra bericht/waarschuwing aan het informatie/consent-scherm toevoegen, bijvoorbeeld om de gebruiker te wijzen op het feit dat de desbetreffende dienst <i>geen officiële</i> dienst van de instelling is en er dus geen ondersteuning wordt geboden. Lees meer informatie hierover op onze <a target=\"_blank\" rel=\"noopener noreferrer\" href=\"https://support.surfconext.nl/dashboard-help-consent\">wiki</a>.",
        subtitle2Viewer: "Op deze pagina zie je op welke manier toestemming wordt gevraagd voordat ze toegang krijgen tot {{name}}. De verschillende settings worden uitgelegd <a target=\"_blank\" rel=\"noopener noreferrer\" href=\"https://support.surfconext.nl/dashboard-help-consent\">op de wiki</a>.",
        no_consent: "Laat scherm met informatie/toestemming over attributen niet zien",
        minimal_consent: "Toon informatiescherm (vraag de gebruiker niet expliciet om toestemming)",
        default_consent: "Vraag de gebruiker expliciet om toestemming (consent) voor het vrijgeven van attributen",
        consent_value: "Type toestemming",
        consent_value_tooltip: "Het type toestemming bepaald hoe en of de gebruiker voor toestemming wordt gevraagd.",
        explanationNl: "Nederlands bericht",
        explanationNl_tooltip: "Dit bericht wordt toegevoegd onderaan in het Nederlandse toestemmings-scherm voor nieuwe gebruikers",
        explanationEn: "Engels bericht",
        explanationEn_tooltip: "Dit bericht wordt toegevoegd onderaan in het Engelse toestemmings-scherm voor nieuwe gebruikers",
        explanationPt: "Portugees bericht",
        explanationPt_tooltip: "Dit bericht wordt toegevoegd onderaan in het Portugese toestemmings-scherm voor nieuwe gebruikers",
        save: "Verstuur verzoek",
        loa_level: "SURFsecureID Level of Assurance (LoA)",
        defaultLoa: "LoA 1: Wachtwoord authenticatie door SURFconext bij de instelling van de gebruiker",
        loa2: "LoA 2 (see the wiki for more info)",
        loa3: "LoA 3 (see the wiki for more info)"
    },
    ssid_panel: {
        title: "SURFsecureID",
        subtitle: "Met <a href=\"https://wiki.surfnet.nl/display/SsID\" target=\"_blank\" rel=\"noopener noreferrer\">SURFsecureID</a> kun je de toegang tot diensten extra beveiligen met sterke authenticatie. ",
        subtitle2: "Een gebruiker logt in met een gebruikersnaam en wachtwoord (de eerste factor) en SURFsecureID zorgt vervolgens voor de tweede factor authenticatie via bijvoorbeeld een mobiele app of USB sleutel. ",
        subtitle3: "Door het kiezen van een hoger <a href=\"https://edu.nl/8nm6h\" target=\"_blank\" rel=\"noopener noreferrer\">Level of Assurance (LoA)</a> kun je de dienst extra beveiligen en voeg je een tweede factor toe aan de login van de gebruikers.",
        highestLoaReached: "Deze dienst heeft al het hoogste LoA level. Vanuit security overwegingen kunt u via dit formulier geen verzoek doen voor een lagere LoA. Neem contact op met <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a> als u een lagere LoA wenst voor deze service."
    },
    how_to_connect_panel: {
        accept: "Ik bevestig dat ik de voorwaarden heb gelezen en deze in naam van mijn instelling accepteer.",
        accept_disconnect: "Ja, ik ga akkoord dat {{app}} niet meer beschikbaar zal zijn voor mijn organisatie",
        attributes: "attributen",
        attributes_policy: "het attribuutbeleid",
        privacy_policy: "privacy-informatie",
        back_to_apps: "Terug naar alle services",
        cancel: "Annuleren",
        check: "Controleer",
        checklist: "Loop deze checklist na voordat je een connectie activeert:",
        processing_agreements: "Controleer of uw instelling voor deze dienst een <a href=\"https://support.surfconext.nl/dashboard-help-vwo\" target=\"_blank\" rel=\"noopener noreferrer\">verwerkersovereenkomst</a> nodig heeft, en zo ja, of die geregeld is.",
        comments_description: "Opmerkingen worden verstuurd naar SURFconext.",
        comments_placeholder: "Voer hier je opmerkingen in...",
        comments_title: "Eventuele opmerkingen?",
        automatic_connect: "Activeer connectie meteen",
        connect: "Activeer connectie",
        connect_title: "Activeer {{app}}",
        connect_invite_title: "Accepteer de uitnodiging om {{app}} te activeren",
        disconnect: "Verbinding deactiveren",
        disconnect_title: "Deactiveer connectie met {{app}}",
        done_disconnect_subtitle_html: "Er zal contact worden opgenomen om deze aanvraag af te ronden. Als je voor die tijd nog vragen hebt, neem dan contact op met <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a>.",
        done_disconnect_subtitle_html_with_jira_html: "Er zal contact worden opgenomen om deze aanvraag af te ronden. Als je voor die tijd nog vragen hebt, neem dan contact op met <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a>. Stuur a.u.b. het ticketnummer mee in het onderwerp ({{jiraKey}}).",
        done_disconnect_title: "Verzoek om verbinding te deactiveren is aangevraagd!",
        done_subtitle_html: "Er zal contact worden opgenomen om deze aanvraag af te ronden. Als je voor die tijd nog vragen hebt, neem dan contact op met <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a>.",
        done_subtitle_with_jira_html: "Er zal contact worden opgenomen om deze aanvraag af te ronden. Als je voor die tijd nog vragen hebt, neem dan contact op met <a href=\"mailto:support@surfconext.nl?subject=Vraag over koppeling {{jiraKey}}\">support@surfconext.nl</a>. Stuur a.u.b. het ticketnummer mee in het onderwerp ({{jiraKey}}).",
        done_title: "Verbinding aangevraagd!",
        rejected_without_interaction_title: "Koppeling niet geslaagd!",
        rejected_without_interaction_subtitle: "Er is iets fout gegaan bij de koppeling.", // TODO: change text
        done_without_interaction_title: "Service gekoppeld!", // TODO: check text
        done_without_interaction_subtitle: "Er kan meteen gebruik van worden gemaakt.", // TODO: check text
        forward_permission: {
            after: " door te sturen naar {{app}}.",
            before: "SURFnet heeft toestemming om de ",
        },
        info_connection_without_interaction: "Deze dienstverlener staat instellingen toe om meteen te koppelen. U hoeft dus niet te wachten tot een verzoek tot kopppeling wordt geaccepteerd.",
        info_connection_share_institution: "Deze dienst wordt aangeboden vanuit uw instelling en daarom kan de koppeling direct worden gemaakt.",
        info_sub_title: "Je kunt een verbinding activeren vanuit dit dashboard. We adviseren je om de checklist na te lopen en de specifieke informatie over deze service door te nemen voordat je een verbinding activeert.",
        info_sub_invite_title: "Je kunt de uitnodiging accepteren. We adviseren je om de checklist na te lopen en de specifieke informatie over deze service door te nemen voordat je een verbinding activeert.",
        info_title: "Verbinding activeren",
        jira_unreachable: "Er is iets mis gegaan bij de aanvraag",
        jira_unreachable_description: "Het is op dit moment niet mogelijk om een aanvraag te doen. Probeer laten opnieuw.",
        license: "licentie",
        license_info: "de licentie-informatie",
        obtain_license: {
            after: " aan te schaffen voor het gebruik van {{app}}.",
            before: "Het is de verantwoordelijkheid van mijn instelling om eventueel een ",
        },
        provide_attributes: {
            after: " aan te leveren.",
            before: "Het is de verantwoordelijkheid van mijn instelling om de juiste ",
        },
        read: "Lees de",
        single_tenant_service_warning: "Verzoeken voor activatie van single tenant diensten duren langer om te verwerken. SURFnet zal contact opnemen zodra het dit verzoek heeft ontvangen.",
        terms_title: "Met het activeren van de connectie ga je akkoord met de volgende voorwaarden:",
        wiki: "wiki voor deze service",
        aansluitovereenkomst_accept: "Ik bevestig dat ik instem met het activeren van een service die geweigerd heeft de 'SURFconext aansluitovereenkomst' te ondertekenen.",
        not_published_in_edugain_idp: "eduGAIN dienst",
        not_published_in_edugain_idp_info: "De dienst {{name}} kan niet worden gekoppeld omdat uw instelling niet is gepubliceerd is in eduGAIN. Om uw instelling te publiceren in eduGAIN kunt u een aanvraag doen in 'Mijn Instellling' en daar 'Gepubliceerd in eduGAIN' selecteren.",
        edit_my_idp_link: "Wijzigingsverzoek aanmaken in 'Mijn instelling'",
        disconnect_jira_info: "Voor meer informatie over dit ticket kunt u contact opnemen met <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a>. Stuur a.u.b. het ticketnummer mee in het onderwerp ({{jiraKey}})",
        invite_denied: "Ticket {{jiraKey}} is succesvol bijgewerkt met uw afwijzing.",
        invite_accepted: "Ticket {{jiraKey}} is succesvol bijgewerkt met uw goedkeuring.",
        deny: "Uitnodiging afwijzen",
        approve: "Uitnodiging Goedkeuren",
        deny_invitation: "Weet u zeker dat u de uitnodiging om the koppelen met {{app}} wilt afwijzen?",
        deny_invitation_info: "Nadat u de uitnodiging heeft afgewezen kunt u altijd nog de koppeling activeren in dit dashboard.",
        invite_action_collision_title: "Dienst {{app}} is al gekoppeld.",
        invite_action_collision_subtitle: "Mid-air botsing gedetecteerd.",
        invite_action_collision: "De uitnodiging om een koppeling te maken met {{app}} is al geaccepteerd. Misschien heeft een collega de uitnodiging al geaccepteerd? Neem bij vragen contact op met <a href=\"mailto:support@surfconext.nl?subject={{jiraKey}}\">support@surfconext.nl</a> en voeg het ticketnummer toe aan het onderwerp: {{jiraKey}}.",
        test_connected_no_connection_title: "Dienst {{app}} kan niet worden gekoppeld.",
        test_connected_no_connection_subtitle: "De status van je instelling is staging en daarom kunnen er geen diensten aan worden gekoppeld.",
        test_connected_no_connection: "Als je de status van je instelling wilt veranderen neem dan contact op met <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a>."

    },

    application_usage_panel: {
        title: "Service gebruik",
        download: "Export",
        error_html: "Op dit moment zijn de statistieken niet beschikbaar. <a href=\"mailto:support@surfconext.nl\">Neem contact op</a> met de supportafdeling, voor meer informatie."
    },

    contact: {
        email: "Service support e-mail"
    },
    export: {
        downloadCSV: "Download als CSV",
        downloadPNG: "Download als PNG",
        downloadPDF: "Download als PDF"
    },
    search_user: {
        switch_identity: "Switch identiteit",
        search: "Zoek",
        search_hint: "Filter op naam",
        name: "Naam",
        switch_to: "Switch naar rol",
        switch: {
            role_dashboard_viewer: "Viewer",
            role_dashboard_admin: "Admin"
        }
    },

    stats: {
        filters: {
            name: "Filters",
            allServiceProviders: "Alle diensten"
        },
        state: "Status",
        timeScale: "Periode",
        date: "Datum",
        from: "Van",
        to: "Tot en met",
        today: "Vandaag",
        sp: "Dienst",
        period: {
            year: "Jaar"
        },
        displayDetailPerSP: "Toon details per dienst",
        scale: {
            year: "Jaar",
            quarter: "Kwartaal",
            month: "Maand",
            week: "Week",
            day: "Dag",
            hour: "Uur",
            minute: "Minuut",
            all: "Totale periode: van ⇨ tot en met"
        },
        helpLink: "https://support.surfconext.nl/dashboard-help-nl#Beschikbaredienstenactiveren-Statistieken"
    },
    chart: {
        title: "Logins en gebruikers per dag",
        chart: "Aantal logins per {{scale}}",
        chartAll: "Aantal logins",
        userCount: "Totale logins",
        uniqueUserCount: "Unieke gebruikers",
        loading: "Ophalen logins....",
        noResults: "Geen login data voor de opgegeven periode.",
        date: "Datum",
        logins: "Logins per {{scale}}",
        allLogins: "# Logins",
        uniqueLogins: "Unieke gebruikers",
        sp: "Dienst",
        idp: "Instelling"
    },
    clipboard: {
        copied: "Gekopieerd!",
        copy: "Kopie naar clipboard"
    },
    live: {
        chartTitle: "Logins per {{scale}}",
        aggregatedChartTitlePeriod: "Logins in de periode {{period}} per {{group}}",
        noTimeFrameChart: "Logins van {{from}} tot en met {{to}}"
    },

    server_error: {
        title: "Je hebt onvoldoende rechten om de Dashboard applicatie te gebruikem.",
        description_html: "Neem contract op met <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a> in het geval dat je denkt dat dit niet juist is."
    },

    not_found: {
        title: "OEPS, deze pagina kan momenteel niet worden getoond.",
        subTitle: "Mogelijke oorzaken en oplossingen zijn:",
        reasonLoginPre: "Je probeert een pagina te bezoeken waarvoor je ingelogd moet zijn. Klik op ",
        reasonLoginPost: " om te kijken of je op de pagina komt die je bedoelde.",
        reasonHelp: "Je hebt niet de juiste rechten om deze pagina te bezoeken. Kijk op de <a href=\"https://edu.nl/p4um4\" target=\"_blank\" rel=\"noopener noreferrer\">Help</a> sectie van onze wiki om te lezen wie wat mag zien.",
        reasonRemoved: "De URL die je probeert te bekijken bestaat niet (meer). Sorry.",
        reasonUnknown: "Je bent ergens anders tegen aangelopen, hebt hulp nodig en/of misschien moeten wij dit oplossen. Stuur ons een mail via <a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a> en we zullen het onderzoeken."
    },

    logout: {
        title: "Succesvol uitgelogd.",
        description_html: "Je <strong>MOET</strong> de browser afsluiten om het uitlogproces af te ronden."
    },

    footer: {
        surfnet_html: "<a href=\"https://www.surfnet.nl/\" target=\"_blank\" rel=\"noopener noreferrer\">SURFnet</a>",
        terms_html: "<a href=\"https://support.surfconext.nl/terms-nl\" target=\"_blank\" rel=\"noopener noreferrer\">Gebruikersvoorwaarden</a>",
        contact_html: "<a href=\"mailto:support@surfconext.nl\">support@surfconext.nl</a>"
    },

    my_idp: {
        title: "Mijn instelling",
        roles: "Rollen",
        sub_title_html: "De volgende rollen zijn toegekend (<a target=\"_blank\" rel=\"noopener noreferrer\" href=\"https://wiki.surfnet.nl/display/surfconextdev/Rolverdeling+contactpersonen\">toelichting</a>):",
        role: "Rol",
        users: "Gebruiker(s)",
        settings: "Gegevens van mijn eigen instelling",
        settings_edit: "Gegevens van mijn eigen instelling en diensten",
        settings_text: "Hieronder staan enkele gegevens van jouw instelling en van Service Providers die door jouw instelling in SURFconext worden aangeboden. Deze gegevens worden in SURFconext gebruikt, bijvoorbeeld in de Where Are You From-pagina. Je kunt deze gegevens laten wijzigen door te klikken op 'Wijzigingsverzoek aanmaken'.",
        settings_text_viewer: "Hieronder staan enkele gegevens van jouw instelling en van Service Providers die door jouw instelling in SURFconext worden aangeboden. Deze gegevens worden in SURFconext gebruikt, bijvoorbeeld in de Where Are You From-pagina.",
        SURFconextverantwoordelijke: "SURFconextverantwoordelijke",
        SURFconextbeheerder: "SURFconextbeheerder",
        "Dashboard supergebruiker": "Dashboard supergebruiker",
        services_title: "Diensten aangeboden door jouw instelling",
        services_title_none: "Geen",
        service_name: "Naam service",
        license_contact_html: "Primaire licentiecontactpersoon (<a target=\"_blank\" rel=\"noopener noreferrer\" href=\"https://support.surfconext.nl/dashboard-help-nl#Beschikbaredienstenactiveren-HoekunjeopSURFconextaangeslotendienstenactiveren?\">toelichting</a>):",
        license_contact_name: "Naam",
        license_contact_email: "Email",
        license_contact_phone: "Telefoonnummer",
        institution: "Instelling",
        services: "Services",
        edit: "Wijzigingsverzoek aanmaken",
        entity_id: "Entity ID",
        state: "Status",
        prodaccepted: "Productie",
        testaccepted: "Staging",
        all: "Alle",
        name: {
            en: "Naam (en)",
            nl: "Naam (nl)",
            pt: "Naam (pt)"
        },
        displayName: {
            en: "Weergave naam (en)",
            nl: "Weergave naam (nl)",
            pt: "Weergave naam (pt)"
        },
        organizationURL: {
            en: "Organisatie URL (en)",
            nl: "Organisatie URL (nl)",
            pt: "Organisatie URL (pt)"
        },
        organizationURL_nl_tooltip: "Een URL waar een eindgebruiker Nederlands informatie kan lezen over de organisatie.",
        organizationURL_en_tooltip: "Een URL waar een eindgebruiker Engelse informatie kan lezen over de organisatie.",
        organizationURL_pt_tooltip: "Een URL waar een eindgebruiker Portugese informatie kan lezen over de organisatie.",
        organizationName: {
            en: "Organisatie naam (en)",
            nl: "Organisatie naam (nl)",
            pt: "Organisatie naam (pt)"
        },
        organizationName_nl_tooltip: "De officiële Nederlandse naam van de organisatie.",
        organizationName_en_tooltip: "De officiële Engelse naam van de organisatie.",
        organizationName_pt_tooltip: "De officiële Portugese naam van de organisatie.",
        organizationDisplayName: {
            en: "Organisatie weergave naam (en)",
            nl: "Organisatie weergave naam name (nl)",
            pt: "Organisatie weergave naam (pt)"
        },
        organizationDisplayName_nl_tooltip: "De Nederlandse weergave naam van de organisatie.",
        organizationDisplayName_en_tooltip: "De Engelse weergave naam van de organisatie.",
        organizationDisplayName_pt_tooltip: "De Portugese weergave naam van de organisatie.",
        keywords: {
            en: "Trefwoorden (en)",
            nl: "Trefwoorden (nl)",
            pt: "Trefwoorden (pt)"
        },
        published_in_edugain: "Gepubliceerd in eduGAIN",
        date_published_in_edugain: "Datum gepubliceerd in eduGAIN",
        logo_url: "Logo",
        new_logo_url: "Nieuwe logo URL",
        research_and_scholarship_info: "Koppel met SP's die aan CoCo en R&S voldoen",
        research_and_scholarship_tooltip: "Dit betekent dat uw IdP automatisch aan alle huidige en toekomstige SP's<br>in SURFconext wordt gekoppeld die voldoen aan zowel ‘Research & Scholarship Entity Category’ en de ‘GEANT Data Protection Code of Conduct ’,<br>waarbij de R&S attributen aan de SP worden vrijgegeven.<br/>Zie de <a href=\"https://support.surfconext.nl/dashboard-help-rns\" target=\"_blank\" rel=\"noopener noreferrer\">wiki</a> vooor meer informatie.",
        allow_maintainers_to_manage_authz_rules: "SURFconextbeheerders mogen autorisatie regels beheren",
        allow_maintainers_to_manage_authz_rules_tooltip: "Dit betekent dat de SURFconextbeheerders van uw instelling autorisatie regels mogen aanmaken, bewerken en verwijderen.",
        displayAdminEmailsInDashboard: "Leden kunnen admin email-adres zien",
        displayAdminEmailsInDashboardTooltip:"Dit betekent dat de email adressen van de<br>SURFconextbeheerders worden getoond aan reguliere gebruikers<br>van uw instelling.",
        displayStatsInDashboard: "Leden kunnen statistieken zien",
        displayStatsInDashboardTooltip:"Dit betekent dat het gebruik / statistieken van alle diensten gekoppeld<br>aan uw instelling kunnen worden bekeken door reguliere gebruikers<br>van uw instelling.",
        contact: "Contactpersonen voor {{name}}",
        contact_name: {
            title: "Contact naam",
        },
        contact_email: {
            title: "Contact email",
            tooltip: "Let op: gebruik liefst<br>een roladres:<br><br><ul><li>- admin@your-instution.nl</li><li>- tech@your-instution.nl</li><li>- helpdesk@your-instution.nl</li></ul><br>die niet verandert bij een personeelswisseling."
        },
        contact_telephone: {
            title: "Contact telefoon",
        },
        contact_type: {
            title: "Contact type",
        },
        contact_types: {
            technical: {
                title: "Technisch Contact:<br>",
                tooltip: "De technische contactpersoon van de IdP. Eerste contactpersoon voor storingen, changes en andere technische zaken met betrekking tot de IdP.<br><br>",
                alttooltip: "suggestie: technisch persoon voor storingen en changes."
            },
            support: {
                title: "Support Contact:<br>",
                tooltip: "Naar dit adres verwijzen wij eindgebruikers die problemen hebben met inloggen, en waarbij wij vermoeden dat het aan de IdP ligt. Dit is dus in de regel een servicedesk van de instelling.<br><br>",
                alttooltip: "suggestie: servicedesk van de instelling."
            },
            help: {
                title: "Support Contact:<br>",
                tooltip: "Naar dit adres verwijzen wij eindgebruikers die problemen hebben met inloggen, en waarbij wij vermoeden dat het aan de IdP ligt. Dit is dus in de regel een servicedesk van de instelling.<br><br>",
                alttooltip: "suggestie: servicedesk van de instelling."
            },
            administrative: {
                title: "Administratief Contact:<br>",
                tooltip: "De administratieve contactpersoon van de IdP. In de praktijk is dit vaak dezelfde contactpersoon als de rol 'SURFconext-verantwoordelijke'.<br><br>",
                alttooltip: "suggestie: persoon met rol 'SURFconext-verantwoordelijke'"
            }
        },
        description: {
            en: "Beschrijving (en)",
            nl: "Beschrijving (nl)",
            pt: "Beschrijving (pt)"
        },
        guest_enabled: "Gast-toegang ingeschakeld",
        guest_enabled_tooltip: "Als gast toegang is ingeschakeld betekent dat gebruikers<br>van de gast IdPgebruik kunnen maken van deze dienst.<br>Zie de <a href=\"https://edu.nl/48ftk\" target=\"_blank\" rel=\"noopener noreferrer\">wiki</a> voor meer informatie.",
        edit_message: "De volgende velden kunnen worden aangepast.",
        save: "Maak wijzigingsverzoek aan",
        change_request_created: "Wijzigingsverzoek ingediend bij het SURFnet SURFconext-team. Het ticket nummer van het wijzigingsverzoek is {{jiraKey}}",
        no_change_request_created: "Er is geen wijzigingsverzoek aangemaakt aangezien er geen wijzigingen zijn gemaakt.",
        change_request_failed: "Er ging iets mis bij het aanmaken van het wijzigingsverzoek.",
        comments: "Opmerkingen",
    },

    policies: {
        confirmation: "Weet je zeker dat je autorisatieregel {{policyName}} wilt verwijderen?",
        flash: "Autorisatieregel '{{policyName}}' is succesvol {{action}}",
        flash_created: "aangemaakt",
        flash_deleted: "verwijderd",
        flash_first: "Autorisatieregel worden nog niet toegepast voor deze dienst. Voordat autorisatieregels wel worden toegepast voor deze dienst moet het SURFconext Team een configuratie verandering maken. Er is een notificatie gestuurd naar het SURFconext Team. Er wordt contact met u opgenomen.",
        flash_updated: "bijgewerkt",
        new_policy: "Nieuwe autorisatieregel",
        how_to: "Uitleg",
        policy_name_not_unique_exception: "Deze autorisatieregel naam is al in gebruik",
        pdp_unreachable: "PDP niet bereikbaar",
        pdp_unreachable_description: "Het is op dit moment niet mogelijk om policies op te halen vanuit PDP. Probeer het later nog eens.",
        pdp_active_info: "Klik om meer te lezen over wanneer de regel actief is.",
        pdp_active_link: "https://support.surfconext.nl/pdp-rule-active-after",
        overview: {
            active: "Actief",
            description: "Omschrijving",
            identityProviderNames: "Instelling(en)",
            name: "Naam",
            numberOfRevisions: "Revisies",
            search: "Zoek autorisatieregels...",
            search_hint: "Filter op naam",
            serviceProviderName: "Dienst",
        }
    },

    policy_attributes: {
        attribute: "Attribuut",
        attribute_value_placeholder: "Attribuut waarde...",
        group_info: " De waarde(s) moeten volledige unieke groep ID zijn e.g. 'urn:collab:group:surfteams.nl:nl:surfnet:diensten:admins'",
        new_attribute: "Voeg een nieuw attribuut toe...",
        new_value: "Voeg een nieuwe waarde toe...",
        sab_info: "De waarde(s) moet geldige rollen in SAB zijn e.g. 'Instellingsbevoegde'",
        values: "Waarde(s)",
        help_link: "https://support.surfconext.nl/dashboard-help-attributes",
        attributeTooltip: "Klik om meer te lezen over attributen."
    },

    policy_detail: {
        access: "Toegang",
        attribute: "Attribute",
        autoFormat: "AutoFormat regel omschrijving",
        cancel: "Annuleer",
        confirmation: "Weet je zeker dat je deze pagina wilt sluiten?",
        create_policy: "Nieuwe autorisatieregel",
        deny: "Deny",
        deny_info: "Deny regels zijn minder gebruikelijk. Als de attributen matchen dan mag de gebruiker niet naar de dienst. Als de attributen niet matchen dan wel.",
        deny_message: "Ongeautoriseerd melding in het Engels",
        deny_message_info: "Dit is de melding die de gebruiker ziet bij een 'Deny' op basis van deze regel.",
        deny_message_nl: "Ongeautoriseerd melding in het Nederlands",
		deny_message_pt: "Ongeautoriseerd melding in het Portugees",
        description: "Omschrijving",
        idps_placeholder: "Selecteer de instellingen - 0 of meer",
        institutions: "Instelling(en)",
        isActive: "Actief",
        isActiveDescription: "Markeer de autorisatieregel actief",
        isActiveInfo: " Inactieve autorisatieregels worden niet geevalueerd in access beslissingen",
        name: "Naam",
        permit: "Permit",
        permit_info: "Permit regels dwingen af dat de gebruiker alleen wordt geautoriseerd als de attributen matchen. Als er geen match is dan wordt de gebruiker niet toegelaten tot de dienst.",
        rule: "Regel",
        rule_and: "EN",
        rule_and_info: "Autorisatieregels met een logische EN dwingen af dat alle gedefinieerde attributen moeten matchen.",
        rule_info_add: " Attribuut waardes van hetzelfde attribuut zullen altijd worden geevalueerd met de logische OF.",
        rule_info_add_2: "Een 'Deny' autorisatieregel wordt altijd geevalueerd met de logische EN voor attribuut waarden van verschillende attributen.",
        rule_or: "OF",
        rule_or_info: "Voor autorisatieregels met een logische OF is het slechts vereist dat 1 attribuut matched.",
        service: "Dienst",
        spScopeInfo: "De beschikbare diensten zijn beperkt tot je eigen diensten zolang er geen Instelling is gekozen",
        sp_placeholder: "Selecteer de dienst - verplicht",
        sub_title: "Aangemaakt door {{displayName}} op {{created}}",
        submit: "Verstuur",
        update_policy: "Bijwerken autorisatieregel",
    },

    revisions: {
        active: "Actief",
        allAttributesMustMatch: "Logische OF regel?",
        attributes: "Attributen",
        changes_first_html: "Dit is de <span class=\"curr\">eerste revisie {{currRevisionNbr}}</span> aangemaakt door {{userDisplayName}} van {{authenticatingAuthorityName}} op {{createdDate}}.",
        changes_info_html: "Veranderingen tussen <span class=\"prev\"> revisie nummer {{prevRevisionNbr}}</span> en <span class=\"curr\">revisie nummer {{currRevisionNbr}}</span> gemaakt door {{userDisplayName}} van {{authenticatingAuthorityName}} op {{createdDate}}.",
        denyAdvice: "Ongeautoriseerd melding in het Engels",
        denyAdviceNl: "Ongeautoriseerd melding in het Nederlands",
		denyAdvicePt: "Ongeautoriseerd melding in het Portugees",
        denyRule: "Toegang Permit regel?",
        description: "Omschrijving",
        identityProviderNames: "Instelling(en)",
        name: "Naam",
        revision: "Revisie nummer",
        serviceProviderName: "Dienst",
        title: "Revisies",
    },

    history: {
        info: "Hieronder alle tickets met betrekking tot (ont)koppelen van diensten of wijzigingsverzoeken. In het menu links kan je de lijst van tickets filteren.",
        moreAwaitingTickets: "Niet alle 'Wachtende op goedkeuring' tickets worden getoond omdat als filter een periode is geselecteerd waarin niet al deze tickets vallen",
        requestDate: "Aangemaakt",
        updateDate: "Gewijzigd",
        type: "Type",
        jiraKey: "Id",
        status: "Status",
        userName: "Door",
        spName: "Dienst",
        action_types_name: {
            LINKREQUEST: "Nieuwe koppeling",
            UNLINKREQUEST: "Ontkoppeling",
            QUESTION: "Vraag",
            CHANGE: "Wijziging",
            LINKINVITE: "Koppeling uitnodiging"
        },
        from: "Van",
        to: "Tot",
        typeIssue: "Type",
        spEntityId: "Dienst",
        statuses: {
            "To Do":"Open",
            "In Progress": "Bezig",
            "Awaiting Input": "Wachtende op goedkeuring",
            "Resolved": "Opgeleverd",
            "Closed": "Gesloten",
            "undefined": "Onbepaald"
        },
        resolution: {
            cancelled: "Geannuleerd",
            cancelledTooltip: "Het ticket was geannuleerd. Als het een uitnodiging voor een koppeling betrof dan heeft de instelling de uitnodiging afgewezen.",
            no_change_required: "Geen verandering nodig",
            no_change_requiredTooltip: "Het ticket had geen verandering nodig.",
            incomplete: "Incompleet",
            incompleteTooltip: "Het ticket is incompleet.",
            done: "Klaar",
            doneTooltip: "Het ticket is opgelost.",
            wont_do: "Zal niet worden opgelost",
            wont_doTooltip: "Het ticket zal niet worden opgelost.",
            wont_fix: "Zal niet worden opgelost",
            wont_fixTooltip: "Het ticket zal niet worden opgelost.",
            resolved: "Opgelost",
            resolvedTooltip: "Het ticket is opgelost.",
            duplicate: "Duplicaat",
            duplicateTooltip: "Het ticket is een duplicaat van een ander ticket.",
            not_completed: "Niet compleet completed",
            not_completedTooltip: "Het ticket is niet compleet.",
            cannot_reproduce: "Kan niet worden gereproduceerd",
            cannot_reproduceTooltip: "De situatie beschreven in het ticket kon niet worden gereproduceerd",
            suspended: "Gepauzeerd",
            suspendedTooltip: "Het ticket is tijdelijk op pauze gezet."
        },
        servicePlaceHolder: "Zoek en selecteer een dienst...",
        noTicketsFound: "Er zijn geen tickets gevonden met de gegeven filters.",
        viewInvitation: "Goedkeuren / Afwijzen",
        resendInvitation: "Herstuur uitnodiging",
        resendInvitationConfirmation: "Weet je zeker dat je de uitnodigings mail nogmaals wilt versturen?",
        resendInvitationFlash: "Uitnodigings mail voor {{jiraKey}} is opnieuw verstuurd",
    },
    service_filter: {
        title: "Filter diensten",
        state: {
            tooltip: "De status van een dienst bepaalt of deze dienst zichtbaar is op het productie platform."
        },
        search: "Zoek diensten..."
    },
    invite_request: {
        info: 'Een koppelings uitnodiging zal een email versturen aan alle geselecteerde contact personen met daarin een uitnodiging om hun instelling te koppelen aan de geselecteerde dienst. Daarnaast zal er een <span class="emphasize">Connection Invite</span> Jira ticket worden aangemaakt met de status <span class="emphasize">Awaiting Input</span>.',
        selectIdp: "Zoek en selecteer een instelling...",
        selectSpDisabled: "Selecteer eerst een instelling",
        selectSp: "Zoek en selecteer nu een dienst...",
        idp: "Instelling",
        sp: "Dienst",
        contactPersons: "Selecteer naar welke contact personen van {{name}} de uitnodiging zal worden versuurd.",
        sourcePersons: "Contact personen uit {{source}}",
        additionalPersons: "Extra contact personen",
        selectContact: "Selecteer",
        sendRequest: "Verstuur",
        reset: "Reset",
        message: "Een - optioneel - bericht voor de uitgenodigden.",
        jiraFlash: "Een Jira ticket is aangemaakt met key {{jiraKey}}. Zodra één van de genodigden de uitnodiging heeft geaccepteerd dan zal dit worden gelogged in het commentaar van {{jiraKey}}."
    },
    profile: {
        title: "Profiel",
        sub_title: "Van uw instelling hebben wij de volgende gegevens ontvangen. Deze gegevens, alsmede uw groepsrelaties, worden opgeslagen in (en gebruikt door) SURFconext. Tevens is het mogelijk dat deze gegevens worden verstrekt aan diensten die u via SURFconext benadert.",
        my_attributes: "Mijn attributen",
        attribute: "Attribuut",
        value: "Waarde",
        my_roles: "Mijn rollen",
        my_roles_description: "De volgende rollen zijn toegekend",
        role: "Rol",
        role_description: "Omschrijving",
        roles: {
            ROLE_DASHBOARD_ADMIN: {
                name: "SURFconextverantwoordelijke",
                description: "U bent gemachtigd om voor uw instelling de connecties met Service Providers te beheren"
            },
            ROLE_DASHBOARD_VIEWER: {
                name: "SURFconextbeheerder",
                description: "U bent gemachtigd om voor uw instelling de connecties met Service Providers in te zien"
            },
            ROLE_DASHBOARD_MEMBER: {
                name: "Instellings gebruiker",
                description: "U bent een reguliere gebruiker binnen het dashboard"
            },
            ROLE_DASHBOARD_SUPER_USER: {
                name: "Dashboard supergebruiker",
                description: "U bent een super gebruiker binnen het dashboard"
            }
        },
        attribute_map: {
            "uid": {
                name: "UID",
                description: "jouw unieke gebruikersnaam binnen jouw instelling"
            },
            "Shib-surName": {
                name: "Achternaam",
                description: "jouw achternaam"
            },
            "Shib-givenName": {
                name: "Voornaam",
                description: "voornaam/roepnaam"
            },
            "Shib-commonName": {
                name: "Volledige persoonsnaam",
                description: "volledige persoonsnaam"
            },
            "displayName": {
                name: "Weergavenaam",
                description: "weergave naam zoals getoond in applicaties"
            },
            "Shib-InetOrgPerson-mail": {
                name: "E-mailadres",
                description: "jouw e-mailadres zoals bekend binnen jouw instelling"
            },
            "Shib-eduPersonAffiliation": {
                name: "Relatie",
                description: "geeft de relatie aan tussen jou en jouw instelling"
            },
            "Shib-eduPersonScopedAffiliation": {
                name: "Scoped relation",
                description: "scoped relatie tussen jou en jouw instelling"
            },
            "eduPersonEntitlement": {
                name: "Rechtaanduiding",
                description: "rechtaanduiding; URI (URL of URN) dat een recht op iets aangeeft; wordt bepaald door een contract tussen dienstaanbieder en instelling"
            },
            "Shib-eduPersonPN": {
                name: "Net-ID",
                description: "jouw unieke gebruikersnaam binnen jouw instelling aangevuld met @instellingsnaam.nl"
            },
            "Shib-preferredLanguage": {
                name: "Voorkeurstaal",
                description: "een tweeletterige afkorting van de voorkeurstaal volgens de ISO 639 taalafkortings codetabel; geen subcodes"
            },
            "schacHomeOrganization": {
                name: "Organisatie",
                description: "aanduiding voor de organisatie van een persoon gebruikmakend van de domeinnaam van de organisatie; syntax conform RFC 1035"
            },
            "Shib-schacHomeOrganizationType": {
                name: "Type Organisatie",
                description: "aanduiding voor het type organisatie waartoe een persoon behoort, gebruikmakend van de waarden zoals geregisteerd door Terena op: http://www.terena.org/registry/terena.org/schac/homeOrganizationType"
            },
            "Shib-schacPersonalUniqueCode": {
                name: "Persoonlijke unieke code",
                description: "deze waardes worden gebruikt voor specifieke identificaties"
            },
            "Shib-nlEduPersonHomeOrganization": {
                name: "Weergavenaam van de Instelling",
                description: "weergavenaam van de instelling"
            },
            "Shib-nlEduPersonOrgUnit": {
                name: "Afdelingsnaam",
                description: "naam van de afdeling"
            },
            "Shib-nlEduPersonStudyBranch": {
                name: "Opleiding",
                description: "opleiding; numerieke string die de CROHOcode bevat. leeg als het een niet reguliere opleiding betreft"
            },
            "Shib-nlStudielinkNummer": {
                name: "Studielinknummer",
                description: "studielinknummer van student zoals geregistreerd bij www.studielink.nl"
            },
            "Shib-nlDigitalAuthorIdentifier": {
                name: "DAI",
                description: "Digital Author Identifier (DAI)"
            },
            "Shib-userStatus": {
                name: "Gebruikersstatus",
                description: "Status van deze gebruiker in SURFconext"
            },
            "Shib-accountstatus": {
                name: "Accountstatus",
                description: "Status van deze account in SURFconext"
            },
            "name-id": {
                name: "Identifier",
                description: "Status van deze account in SURFconext"
            },
            "Shib-voName": {
                name: "Naam Virtuele Organisatie",
                description: "De naam van de Virtuele Organisatie waarvoor je bent ingelogd."
            },
            "Shib-user": {
                name: "Identifier",
                description: "Status van deze account in SURFconext"
            },
            "is-member-of": {
                name: "Lidmaatschap",
                description: "Lidmaatschap van virtuele organisaties en SURFconext"
            },
            "Shib-surfEckid": {
                name: "SURF EDU-K",
                description: "Educatieve Content Keten Identifier (ECK ID) is een pseudonymous identifier."
            }
        }
    }
};
