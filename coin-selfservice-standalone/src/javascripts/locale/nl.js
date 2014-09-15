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

  apps: {
    overview: {
      application: "Applicatie",
      provider: "Provider",
      license: "Licentie",
      connection: "Connectie",
      added: "Toegevoegd",
      search_hint: "Zoek op naam, bedrijf, of trefwoord",
      search: "Zoek"
    },
    detail: {

    }
  }
};
