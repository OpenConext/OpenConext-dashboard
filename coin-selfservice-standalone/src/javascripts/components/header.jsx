/** @jsx React.DOM */

App.Components.Header = React.createClass({
  render: function () {
    return (
      <div className="mod-header">
        <h1 className="title"><a href="/">{I18n.t("header.title")}</a></h1>
        <div className="meta">
          <p className="name">{I18n.t("header.welcome", { name: App.currentUser.displayName } )}</p>
          <App.Components.IDPSelector />
          <App.Components.LanguageSelector />
          <ul className="links">
            <li dangerouslySetInnerHTML={{__html: I18n.t("header.links.help_html") }}></li>
            <li><a href="/logout">{I18n.t("header.links.logout")}</a></li>
          </ul>
        </div>
      </div>
    );
  }
});
