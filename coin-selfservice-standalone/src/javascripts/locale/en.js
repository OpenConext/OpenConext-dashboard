// Interpolation works as follows:
//
// Make a key with the translation and enclose the variable with {{}}
// ie "Hello {{name}}" Do not add any spaces around the variable name.
// Provide the values as: I18n.t("key", {name: "John Doe"})


I18n.translations.en = {
  code: "EN",
  name: "English",
  select_locale: "Select English",

  header: {
    title: "SurfConext Dashboard",
    welcome: "Welcome, {{name}}"
  }
};
