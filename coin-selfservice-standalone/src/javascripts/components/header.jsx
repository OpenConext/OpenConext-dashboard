/** @jsx React.DOM */

App.Components.Header = React.createClass({
  render: function () {
    return (
      <div className="mod-header">
        <h1 className="title">{I18n.t("header.title")}</h1>
        <div className="meta">
          <p className="name">{I18n.t("header.welcome", { name: App.currentUser.displayName } )}</p>
          <ul className="language">
            <li className="selected"><a href="#">EN</a></li>
            <li><a href="#">NL</a></li>
          </ul>
          <ul className="links">
            <li><a href="#">Help</a></li>
            <li><a href="#">Logout</a></li>
          </ul>
        </div>
      </div>
    );
  }
});
