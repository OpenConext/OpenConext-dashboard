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
    welcome: "Welcome, {{name}}"
  },

  facets: {
    title: "Filters",
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
      added: "Added",
      search_hint: "Filter by name, company, or keyword",
      search: "Search"
    },
    detail: {
      support_contact_description: "Support Email"
    }
  }
};
