/** @jsx React.DOM */

App.Components.Header = React.createClass({
  render: function () {
    return (
      <div className="mod-header">
        <h1 className="title"><a href="/">{I18n.t("header.title")}</a></h1>
        <div className="meta">
          <p className="name">{I18n.t("header.welcome", { name: App.currentUser.displayName } )}</p>
          <ul className="language">
            {[
              this.renderLocaleChooser("en"),
              this.renderLocaleChooser("nl")
            ]}
          </ul>
          <ul className="links">
            <li><a href="#">Help</a></li>
            <li><a href="#">Logout</a></li>
          </ul>
        </div>
      </div>
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
