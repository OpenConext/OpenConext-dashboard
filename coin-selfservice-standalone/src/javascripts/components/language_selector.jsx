/** @jsx React.DOM */

App.Components.LanguageSelector = React.createClass({
  render: function () {
    return (
      <ul className="language">
        {[
          this.renderLocaleChooser("en"),
          this.renderLocaleChooser("nl")
        ]}
      </ul>
    );
  },

  renderLocaleChooser: function(locale) {
    return (
      <li key={locale} className={I18n.currentLocale() == locale ? "selected" : ""}>
        <a
          href="#"
          title={I18n.t("select_locale", {locale: locale})}
          onClick={this.handleChooseLocale(locale)}>
          {I18n.t("code", {locale: locale})}
        </a>
      </li>
    );
  },

  handleChooseLocale: function(locale) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      window.location.search = "lang=" + locale;
    }
  }
});
