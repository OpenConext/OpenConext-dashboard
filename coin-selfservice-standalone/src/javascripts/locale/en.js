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

  header: {
    title: "SurfConext Dashboard",
    welcome: "Welcome, {{name}}",
    links: {
      help_html: "<a href=\"https://wiki.surfnetlabs.nl/display/conextsupport/Dashboard+van+SURFconext+%28EN%29\" target=\"_blank\">Help</a>",
      logout: "Logout"
    }
  },

  navigation: {
    apps: "Apps",
    notifications: "Notifications",
    history: "History",
    stats: "Statistics"
  },

  facets: {
    title: "Filters",
    reset: "reset",
    totals: {
      all: "Showing all {{total}} apps.",
      filtered: "Showing {{count}} of {{total}} apps."
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
        no_license: "No license"
      }
    }
  },

  apps: {
    overview: {
      application: "Application",
      provider: "Provider",
      license: "License",
      connection: "Connection",
      search_hint: "Filter by name, company, or keyword",
      search: "Search",
      connect: "Connect"
    },
    detail: {
      overview: "Overview",
      license_info: "License info",
      application_usage: "Application usage",
      attribute_policy: "Attribute policy",
      how_to_connect: "How to connect"
    }
  },

  overview_panel: {
    provider: "Application by {{name}}",
    description: "Description"
  },

  contact: {
    email: "Support email"
  },

  footer: {
    surfnet_html: "<a href=\"http://www.surfnet.nl/en\" target=\"_blank\">SURFnet</a>",
    terms_html: "<a href=\"https://wiki.surfnetlabs.nl/display/conextsupport/Terms+of+Service+%28EN%29\" target=\"_blank\">Terms of Service</a>",
    contact_html: "<a href=\"mailto:help@surfconext.nl\">help@surfconext.nl</a>"
  }
};
