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

  header: {
    title: "SurfConext Dashboard",
    welcome: "Welkom, {{name}}",
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
    apps: "Apps",
    notifications: "Notificaties",
    history: "Historie",
    stats: "Statistieken"
  },

  facets: {
    title: "Filters",
    reset: "reset",
    totals: {
      all: "Alle {{total}} apps worden weergegeven.",
      filtered: "{{count}} uit {{total}} apps worden weergegeven."
    },
    static: {
      connection: {
        name: "Connectie",
        has_connection: "Met connectie",
        no_connection: "Geen connection"
      },
      license: {
        name: "Licentie",
        has_license: "Met licentie",
        no_license: "Zonder licentie"
      }
    }
  },

  apps: {
    overview: {
      application: "Applicatie",
      provider: "Provider",
      license: "Licentie",
      connection: "Connectie",
      search_hint: "Filter op naam",
      search: "Zoek",
      connect: "Aansluiten"
    },
    detail: {
      overview: "Overzicht",
      license_info: "Licentie informatie",
      application_usage: "Applicatie gebruik",
      attribute_policy: "Attribuut beleid",
      how_to_connect: "Hoe aan te sluiten"
    }
  },

  overview_panel: {
    provider: "Beschikbaar gesteld door {{name}}",
    description: "Beschrijving"
  },

  contact: {
    email: "Support email"
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

  footer: {
    surfnet_html: "<a href=\"http://www.surfnet.nl/\" target=\"_blank\">SURFnet</a>",
    terms_html: "<a href=\"https://wiki.surfnetlabs.nl/display/conextsupport/Terms+of+Service+%28NL%29\" target=\"_blank\">Gebruikersvoorwaarden</a>",
    contact_html: "<a href=\"mailto:help@surfconext.nl\">help@surfconext.nl</a>"
  },

  notifications: {
    title: "Notificaties",
    icon: "Icoon",
    name: "Naam",
    license: "Licentie aanwezig",
    connection: "Verbonden",
    messages: {
      fcp: "Onderstaande diensten zijn mogelijkerwijs nog niet beschikbaar, omdat de licentie of de technische connectie nog niet aanwezig is. Neem contact op met de licentie contactpersoon van uw instelling."
    }
  }
};
