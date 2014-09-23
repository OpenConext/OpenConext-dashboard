/** @jsx React.DOM */

App.Components.Header = React.createClass({
  getInitialState: function() {
    return {
      dropDownActive: false
    }
  },

  render: function () {
    return (
      <div className="mod-header">
        <h1 className="title"><a href="/">{I18n.t("header.title")}</a></h1>
        <div className="meta">
          <div className="name">
            <a href="#" onClick={this.handleToggle}>{I18n.t("header.welcome", { name: App.currentUser.displayName } )}</a>
            {this.renderDropDown()}
          </div>
          <App.Components.LanguageSelector />
          <ul className="links">
            <li dangerouslySetInnerHTML={{__html: I18n.t("header.links.help_html") }}></li>
            {this.renderExitLogout()}
          </ul>
        </div>
      </div>
    );
  },

  renderDropDown: function() {
    if(this.state.dropDownActive) {
      return (
        <ul>
          <h2>{I18n.t("header.you")}</h2>
          <ul>
            <li><a href="/profile">{I18n.t("header.profile")}</a></li>
          </ul>
          {this.renderIdpSelector()}
        </ul>
      )
    }
  },

  renderExitLogout: function() {
    if (App.currentUser.superUser && App.currentUser.switchedToIdp) {
      return (
        <li><a href="/exit">{I18n.t("header.links.exit")}</a></li>
      );
    } else {
      return (
        <li><a href="/logout">{I18n.t("header.links.logout")}</a></li>
      );
    }
  },

  renderIdpSelector: function() {
    if(!App.currentUser.superUser) {
      return <App.Components.IDPSelector />
    }
  },

  handleToggle: function(e) {
    e.preventDefault();
    e.stopPropagation();
    this.setState({dropDownActive: !this.state.dropDownActive});
  }
});
