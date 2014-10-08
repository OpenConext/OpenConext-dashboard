/** @jsx React.DOM */

App.Components.OverviewPanel = React.createClass({
  render: function() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{this.props.app.name}</h1>
        </div>

        <div className="mod-connection">
          {this.renderConnection()}
          {this.renderLicense()}
        </div>

        <div className="mod-description">
          <h2>{I18n.t("overview_panel.description")}</h2>
          <p>{this.props.app.enduserDescription}</p>
        </div>

        <App.Components.Screenshots screenshotUrls={this.props.app.screenshotUrls} />
      </div>
    );
  },

  renderConnection: function() {
    return this.props.app.connected ? this.renderHasConnection() : this.renderNoConnection();
  },

  renderHasConnection: function() {
    if (App.currentUser.dashboardAdmin) {
      var disconnect = <p><a href="#" onClick={this.props.onSwitchPanel("how_to_connect")}>{I18n.t("overview_panel.disconnect")}</a></p>;
    }

    return (
      <div className="technical yes split">
        <h2>{I18n.t("overview_panel.has_connection")}</h2>
        {disconnect}
      </div>
    );
  },

  renderNoConnection: function() {
    if (App.currentUser.dashboardAdmin) {
      var connect = <p><a href="#" onClick={this.props.onSwitchPanel("how_to_connect")}>{I18n.t("overview_panel.how_to_connect")}</a></p>;
    }

    return (
      <div className="technical no split">
        <h2>{I18n.t("overview_panel.no_connection")}</h2>
        {connect}
      </div>
    );
  },

  renderLicense: function() {
    if (this.props.app.hasCrmLink) {
      return this.props.app.license ? this.renderHasLicense() : this.renderNoLicense();
    } else {
      return this.renderUnknownLicense();
    }
  },

  renderHasLicense: function() {
    return (
      <div className="license yes split">
        <h2>{I18n.t("overview_panel.has_license")}</h2>
      </div>
    );
  },

  renderNoLicense: function() {
    return (
      <div className="license no split">
        <h2>{I18n.t("overview_panel.no_license")}</h2>
        <p><a href="#" onClick={this.props.onSwitchPanel("license_info")}>{I18n.t("overview_panel.license_info")}</a></p>
      </div>
    );
  },

  renderUnknownLicense: function() {
    return (
      <div className="license unknown split">
        <h2>{I18n.t("overview_panel.unknown_license")}</h2>
      </div>
    );
  },
});
