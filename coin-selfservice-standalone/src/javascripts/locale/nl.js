// Interpolation works as follows:
//
// Make a key with the translation and enclose the variable with {{}}
// ie "Hello {{name}}" Do not add any spaces around the variable name.
// Provide the values as: I18n.t("key", {name: "John Doe"})


I18n.translations.nl = {
  code: "NL",
  name: "Nederlands",
  select_locale: "Selecteer Nederlands",

  boolean: {
    yes: "Ja",
    no: "Nee"
  },

  date: {
    month_names: [null, "januari", "fabruari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "ocktober", "november", "december"]
  },

  header: {
    title: "Dashboard",
    welcome: "Welkom,",
    links: {
      help_html: "<a href=\"https://wiki.surfnetlabs.nl/display/conextsupport/Dashboard+van+SURFconext+%28NL%29\" target=\"_blank\">Help</a>",
      logout: "Uitloggen",
      exit: "Exit"
    },
    you: "Jij",
    profile: "Profiel",
    switch_idp: "Kies IDP"
  },

  navigation: {
    apps: "Services",
    notifications: "Notificaties",
    history: "Logboek",
    stats: "Statistieken",
    my_idp: "Mijn instelling"
  },

  facets: {
    title: "Filters",
    reset: "reset",
    download: "Download overzicht",
    totals: {
      all: "Alle {{total}} services worden weergegeven",
      filtered: "{{count}} uit {{total}} services worden weergegeven"
    },
    static: {
      connection: {
        name: "Connectie actief",
        has_connection: "Ja",
        no_connection: "Nee"
      },
      license: {
        name: "Licentie",
        has_license: "Ja",
        no_license: "Nee",
        unknown_license: "Onbekend"
      }
    }
  },

  apps: {
    overview: {
      name: "Service",
      license: "Licentie",
      license_unknown: "Onbekend",
      connected: "Connectie actief",
      search_hint: "Zoeken",
      search: "Zoek",
      connect: "",
      connect_button: "Activeren"
    },
    detail: {
      overview: "Overzicht",
      license_info: "Licentie informatie",
      attribute_policy: "Attribuut beleid",
      how_to_connect: "Connectie activeren",
      how_to_disconnect: "Connectie deactiveren",
      application_usage: "Service gebruik"
    }
  },

  app_meta: {
    question: "Heb je een vraag?",
    eula: "Algemene voorwaarden",
    website: "Website",
    support: "Support pagina",
    login: "Login pagina"
  },

  license_info_panel: {
    title: "Licentie informatie",
    has_license_html: "Er is via <a href=\"https://www.surfmarket.nl\" target=\"_blank\">SURFmarket</a> een licentie afgesloten voor deze service.",
    no_license_html: "Jouw instelling heeft voor deze service geen licentie via <a href=\"https://www.surfmarket.nl\" target=\"_blank\">SURFmarket</a>.",
    unknown_license: "Het is onbekend welke licentie voor deze dienst geldt.",
    no_license_description_html: "" +
      "<ul>" +
      "   <li>Laat de licentiecontactpersoon van jouw instelling een licentie afsluiten bij <a href=\"https://www.surfmarket.nl\" target=\"_blank\">SURFmarket</a> of</li>" +
      "   <li>In sommige gevallen is de licentie direct bij de aanbieder van de service afgesloten. Geef dit alsjeblieft aan bij het activeren van de connectie.</li>" +
      "</ul>",
    unknown_license_description_html: "Er zijn verschillende opties:" +
      "<ul>" +
      "   <li>SURF of een andere instelling biedt deze dienst gratis aan.</li>" +
      "   <li>De licentie moet direct bij de aanbieder van de service worden afgesloten.</li>" +
      "   <li>De service is nog niet opgevoerd in de administratie van <a href=\"https://www.surfmarket.nl\" target=\"_blank\">SURFmarket</a></li>" +
      "</ul>" +
      "<p>SURFnet zal waar nodig contact opnemen met de aanbieder of <a href=\"https://www.surfmarket.nl\" target=\"_blank\">SURFmarket</a> alvorens de koppeling te activeren.</p>"
  },

  license_info: {
    unknown_license: "Geen licentieinformatie beschikbaar",
    has_license: "Licentie beschikbaar",
    no_license: "Licentie is niet aanwezig",
    license_info: "Lees hoe je een licentie kunt verkrijgen",
    license_unknown_info: "Lees meer",
    valid: "Licentie is geldig t/m {{date}}"
  },

  overview_panel: {
    wiki_info_html: "Voor deze service is extra informatie beschikbaar in de SURFconext <a href=\"{{link}}\" target=\"_blank\">wiki</a>.",
    description: "Beschrijving",
    has_connection: "Connectie actief",
    no_connection: "Connectie niet actief",
    how_to_connect: "Lees hoe je een connectie maakt",
    disconnect: "Lees hoe je een verbinding verbreekt"
  },

  attributes_policy_panel: {
    title: "Attributen",
    subtitle: "De volgende attributen worden uitgewisseld met {{name}}. Let wel: alle attributen moeten met de juiste waarden gevuld zijn, anders zijn er extra stappen nodig om de connectie te kunnen activeren.",
    attribute: "Attribuut",
    your_value: "Jouw waarde",
    hint: "Dit is een voorbeeld waarde van dit attribuut uit je eigen account, zodat je een idee krijgt wat voor waarde dit zal zijn. Dit is misschien niet representatief voor andere accounts binnen jouw intstelling.",
    arp: {
      noarp: "Er is geen 'Attribute Release Policy' bekend. Alle bekende attributen zullen worden uitgewisseld met {{name}}.",
      noattr: "Er zullen geen attributen worden uitgewisseld met {{name}}."
    }
  },

  how_to_connect_panel: {
    info_title: "Verbinding activeren",
    info_sub_title: "Je kunt een verbinding activeren vanuit dit dashboard. We adviseren je om de checklist na te lopen en de specifieke informatie over deze service door te nemen voordat je een verbinding activeert.",
    connect_title: "Activeer {{app}}",
    checklist: "Algemene checklist",
    check: "Controleer",
    read: "Lees de",
    license_info: "de licentie informatie",
    attributes_policy: "het attribuut beleid",
    wiki: "wiki voor deze service",
    wiki_link: "http://www.google.com/",
    specific: {
      title: "Specifieke informatie",
      description: "bla bla bla"
    },
    connect: "Activeer application",
    connect_hint: "(opent het formulier om een activatie aan te vragen)",
    cancel: "Annuleren",
    terms_title: "Door het maken van een verbinding ga je akkoord met de volgende voorwaarden",
    comments_title: "Eventuele opmerkingen?",
    comments_description: "Opmerkingen worden verstuurd naar SURFconext.",
    comments_placeholder: "Voer hier je opmerkingen in...",
    provide_attributes: {
      before: "Het is de verantwoordelijkheid van mijn instelling om de vereiste ",
      after: " aan te leveren."
    },
    forward_permission: {
      before: "SURFnet heeft toestemming om de ",
      after: " door te sturen naar {{app}}."
    },
    obtain_license: {
      before: "Het is de verantwoordelijkheid van mijn instelling om een ",
      after: " aan te schaffen voor het gebruik van {{app}}."
    },
    attributes: "attributen",
    license: "licentie",
    accept: "Bij deze bevestig ik dat ik de voorwaarden heb gelezen en deze in naam van mijn instelling, accepteer.",
    back_to_apps: "Terug naar alle services",
    done_title: "Verbinding gemaakt!",
    done_subtitle_html: "Er zal contact worden opgenomen om deze aanvraag af te ronden. Als je voor die tijd nog vragen hebt, neem dan contact op met <a href=\"mailto:help@surfconext.nl\">help@surfconext.nl</a>.",
    disconnect_title: "Verbreek verbinding met {{app}}",
    accept_disconnect: "Ja, ik ga akkoord dat {{app}} niet meer beschikbaar zal zijn voor mijn organisatie",
    disconnect: "Verbinding deactiveren",
    done_disconnect_title: "Verzoek om verbinding te deactiveren is aangevraagd!",
    done_disconnect_subtitle_html: "Er zal contact worden opgenomen om deze aanvraag af te ronden. Als je voor die tijd nog vragen hebt, neem dan contact op met <a href=\"mailto:help@surfconext.nl\">help@surfconext.nl</a>."
  },

  application_usage_panel: {
    title: "Service gebruik",
    description: "Aantal logins",
    logins: "Logins",
    last_week: "Afgelopen week",
    last_month: "Afgelopen maand",
    last_three_months: "Afgelopen 3 maanden",
    last_year: "Afgelopen jaar",
    download: "Download",
    error_html: "Op dit moment zijn de statistieken niet beschikbaar. <a href=\"mailto:support@surfconext.nl\">Neem contact op</a> met de support afdeling, voor meer informatie."
  },

  contact: {
    email: "Service support e-mail"
  },

  search_user: {
    switch_identity: "Switch identiteit",
    search: "Filter op naam",
    name: "Naam",
    switch_to: "Switch naar rol",
    switch: {
      role_dashboard_viewer: "Viewer",
      role_dashboard_admin: "Admin"
    }
  },

  stats: {
    logins_for: "Logins voor {{service}}",
    legend: "Legenda"
  },

  not_found: {
    title: "Deze pagina kon niet worden gevonden.",
    description_html: "Controleer of het adres correct gespeld is of ga terug naar de <a href=\"/\">homepage</a>."
  },

  logout: {
    title: "Succesvul uitgelogd.",
    description_html: "Je <strong>MOET</strong> de browser afsluiten om het uitlogproces af te ronden."
  },

  footer: {
    surfnet_html: "<a href=\"http://www.surfnet.nl/\" target=\"_blank\">SURFnet</a>",
    terms_html: "<a href=\"https://wiki.surfnetlabs.nl/display/conextsupport/Terms+of+Service+%28NL%29\" target=\"_blank\">Gebruikersvoorwaarden</a>",
    contact_html: "<a href=\"mailto:help@surfconext.nl\">help@surfconext.nl</a>"
  },

  notifications: {
    title: "Notificaties",
    icon: "Icoon",
    name: "Naam",
    license: "Licentie",
    connection: "Connectie",
    messages: {
      fcp: "Onderstaande diensten zijn mogelijkerwijs nog niet beschikbaar, omdat de licentie of de technische connectie nog niet aanwezig is. Neem contact op met de licentie contactpersoon van uw instelling."
    }
  },

  my_idp: {
    title: "Mijn instelling",
    sub_title_html: "De volgende rollen zijn toegekend: (<a target=\"_blank\" href=\"https://wiki.surfnet.nl/pages/viewpage.action?pageId=25198606\">Uitleg rollen</a>)",
    role: "Rol",
    users: "Gebruiker(s)",
    SURFconextverantwoordelijke: "SURFconextverantwoordelijke",
    SURFconextbeheerder: "SURFconextbeheerder",
    "Dashboard supergebruiker": "Dashboard supergebruiker",
    services_title: "Deze Services worden aangeboden door uw instelling:",
    service_name: "Naam service"
  },

  history: {
    title: "Logboek",
    requestDate: "Datum",
    type: "Type",
    jiraKey: "Ticket ID",
    status: "Status",
    userName: "Door",
    action_types: {
      LINKREQUEST: "Verbinden met {{serviceName}}",
      UNLINKREQUEST: "Ontbinden met {{serviceName}}",
      QUESTION: "Vraag"
    },
    statusses: {
      OPEN: "Het verzoek staat uit",
      CLOSED: "Het verzoek is gesloten"
    }
  },

  profile: {
    title: "Profiel",
    sub_title: "Van uw instelling hebben wij de volgende gegevens ontvangen. Deze gegevens, alsmede uw groepsrelaties, worden opgeslagen in (en gebruikt door) SURFconext. Tevens is het mogelijk dat deze gegevens worden verstrekt aan diensten die u via SURFconext benadert.",
    my_attributes: "Mijn attributen",
    attribute: "Attribuut",
    value: "Waarde",
    my_roles: "Mijn rollen",
    my_roles_description: "De volgende rollen zijn toegekend:",
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
      ROLE_DASHBOARD_SUPER_USER: {
        name: "Dashboard supergebruiker",
        description: "U bent een super gebruiker binnen het dashboard"
      }
    },
    attribute_map: {
      "urn:mace:dir:attribute-def:uid": {
        name: "UID",
        description: "jouw unieke gebruikersnaam binnen jouw instelling"
      },
      "urn:mace:dir:attribute-def:sn": {
        name: "Achternaam",
        description: "jouw achternaam"
      },
      "urn:mace:dir:attribute-def:givenName": {
        name: "Voornaam",
        description: "voornaam/roepnaam"
      },
      "urn:mace:dir:attribute-def:cn": {
        name: "Volledige persoonsnaam",
        description: "volledige persoonsnaam"
      },
      "urn:mace:dir:attribute-def:displayName": {
        name: "Weergavenaam",
        description: "weergave naam zoals getoond in applicaties"
      },
      "urn:mace:dir:attribute-def:mail": {
        name: "E-mailadres",
        description: "jouw e-mailadres zoals bekend binnen jouw instelling"
      },
      "urn:mace:dir:attribute-def:eduPersonAffiliation": {
        name: "Relatie",
        description: "geeft de relatie aan tussen jou en jouw instelling"
      },
      "urn:mace:dir:attribute-def:eduPersonEntitlement": {
        name: "Rechtaanduiding",
        description: "rechtaanduiding; URI (URL of URN) dat een recht op iets aangeeft; wordt bepaald door een contract tussen dienstaanbieder en instelling"
      },
      "urn:mace:dir:attribute-def:eduPersonPrincipalName": {
        name: "Net-ID",
        description: "jouw unieke gebruikersnaam binnen jouw instelling aangevuld met @instellingsnaam.nl"
      },
      "urn:mace:dir:attribute-def:preferredLanguage": {
        name: "Voorkeurstaal",
        description: "een tweeletterige afkorting van de voorkeurstaal volgens de ISO 639 taalafkortings codetabel; geen subcodes"
      },
      "urn:mace:terena.org:attribute-def:schacHomeOrganization": {
        name: "Organisatie",
        description: "aanduiding voor de organisatie van een persoon gebruikmakend van de domeinnaam van de organisatie; syntax conform RFC 1035"
      },
      "urn:mace:terena.org:attribute-def:schacHomeOrganizationType": {
        name: "Type Organisatie",
        description: "aanduiding voor het type organisatie waartoe een persoon behoort, gebruikmakend van de waarden zoals geregisteerd door Terena op: http://www.terena.org/registry/terena.org/schac/homeOrganizationType"
      },
      "urn:mace:surffederatie.nl:attribute-def:nlEduPersonHomeOrganization": {
        name: "Weergavenaam van de Instelling",
        description: "weergavenaam van de instelling"
      },
      "urn:mace:surffederatie.nl:attribute-def:nlEduPersonOrgUnit": {
        name: "Afdelingsnaam",
        description: "naam van de afdeling"
      },
      "urn:mace:surffederatie.nl:attribute-def:nlEduPersonStudyBranch": {
        name: "Opleiding",
        description: "opleiding; numerieke string die de CROHOcode bevat. leeg als het een niet reguliere opleiding betreft"
      },
      "urn:mace:surffederatie.nl:attribute-def:nlStudielinkNummer": {
        name: "Studielinknummer",
        description: "studielinknummer van student zoals geregistreerd bij www.studielink.nl"
      },
      "urn:mace:surffederatie.nl:attribute-def:nlDigitalAuthorIdentifier": {
        name: "DAI",
        description: "Digital Author Identifier (DAI) zoals beschreven op: http://www.surffoundation.nl/smartsite.dws?ch=eng&id=13480"
      },
      "urn:mace:surffederatie_nl:attribute-def:nlEduPersonHomeOrganization": {
        name: "Weergavenaam van de Instelling",
        description: "weergavenaam van de instelling"
      },
      "urn:mace:surffederatie_nl:attribute-def:nlEduPersonOrgUnit": {
        name: "Afdelingsnaam",
        description: "naam van de afdeling"
      },
      "urn:mace:surffederatie_nl:attribute-def:nlEduPersonStudyBranch": {
        name: "Opleiding",
        description: "opleiding; numerieke string die de CROHOcode bevat. leeg als het een niet reguliere opleiding betreft"
      },
      "urn:mace:surffederatie_nl:attribute-def:nlStudielinkNummer": {
        name: "Studielinknummer",
        description: "studielinknummer van student zoals geregistreerd bij www.studielink.nl"
      },
      "urn:mace:surffederatie_nl:attribute-def:nlDigitalAuthorIdentifier": {
        name: "DAI",
        description: "Digital Author Identifier (DAI) zoals beschreven op: http://www.surffoundation.nl/smartsite.dws?ch=eng&id=13480"
      },
      "urn:oid:1.3.6.1.4.1.1076.20.100.10.10.1": {
        name: "Accountstatus",
        description: "Status van deze account in de SURFfederatie"
      },
      "urn:oid:1.3.6.1.4.1.5923.1.1.1.1": {
        name: "Accountstatus",
        description: "Status van deze account in de SURFfederatie"
      },
      "nameid": {
        name: "Identifier",
        description: "Status van deze account in de SURFfederatie"
      },
      "urn:oid:1.3.6.1.4.1.1076.20.100.10.10.2": {
        name: "Naam Virtuele Organisatie",
        description: "De naam van de Virtuele Organisatie waarvoor je bent ingelogd."
      },
      "urn:oid:1.3.6.1.4.1.1076.20.40.40.1": {
        name: "Identifier",
        description: "Status van deze account in de SURFfederatie"
      },
      "urn:oid:1.3.6.1.4.1.5923.1.1.1.10": {
        name: "Identifier",
        description: "Status van deze account in de SURFfederatie"
      },
      "urn:nl.surfconext.licenseInfo": {
        name: "Licentieinformatie",
        description: "Licentie informatie voor de huidige dienst"
      },
      "urn:oid:1.3.6.1.4.1.5923.1.5.1.1": {
        name: "Lidmaatschap",
        description: "Lidmaatschap van virtuele organisaties en de SURFfederatie"
      }
    }
  }
};
