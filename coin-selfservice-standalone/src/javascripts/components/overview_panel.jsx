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
          <App.Components.LicenseInfo app={this.props.app} onSwitchPanel={this.props.onSwitchPanel} />
        </div>

        <div className="mod-title">
          <h3 dangerouslySetInnerHTML={{ __html: I18n.t("overview_panel.wiki_info_html", { link: "http://www.google.com"}) }} />
        </div>

        <div className="mod-description">
          <h2>{I18n.t("overview_panel.description")}</h2>
          <p dangerouslySetInnerHTML={{ __html: this.props.app.enduserDescription}} />
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
        <i className="fa fa-chain" />
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
      <div className="technical unknown split">
        <i className="fa fa-chain-broken" />
        <h2>{I18n.t("overview_panel.no_connection")}</h2>
        {connect}
      </div>
    );
  }
});
