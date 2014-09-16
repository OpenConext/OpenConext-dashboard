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
    welcome: "Welkom, {{name}}"
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
      search_hint: "Zoek op naam, bedrijf, of trefwoord",
      search: "Zoek"
    },
    detail: {
      support_contact_description: "Support Mail",
      application_by: "Beschikbaar gesteld door",
      overview: "Overzicht",
      license_info: "Licentie informatie",
      application_usage: "Applicatie gebruik",
      attribute_policy: "Applicatie gebruik",
      how_to_connect: "Hoe aan te sluiten"
    }
  },

  footer: {
    surfnet_html: "<a href=\"http://www.surfnet.nl/\" target=\"_blank\">SURFnet</a>",
    terms_html: "<a href=\"https://wiki.surfnetlabs.nl/display/conextsupport/Terms+of+Service+%28NL%29\" target=\"_blank\">Gebruikersvoorwaarden</a>",
    contact_html: "<a href=\"mailto:help@surfconext.nl\">help@surfconext.nl</a>"
  }
};
