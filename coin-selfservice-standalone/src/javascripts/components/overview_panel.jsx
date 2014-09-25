/** @jsx React.DOM */

App.Components.OverviewPanel = React.createClass({
  render: function() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{this.props.app.name}</h1>
          <h2>{I18n.t("overview_panel.provider", {name: this.props.app.spName})}</h2>
        </div>

        <div className="mod-connection">
          {this.renderConnection()}
          {this.renderLicense()}
        </div>

        <div className="mod-description">
          <h2>{I18n.t("overview_panel.description")}</h2>
          <p>{this.props.app.description}</p>
        </div>

        <App.Components.Screenshots screenshotUrls={this.props.app.screenshotUrls} />
      </div>
    );
  },

  renderConnection: function() {
    return this.props.app.connected ? this.renderHasConnection() : this.renderNoConnection();
  },

  renderHasConnection: function() {
    return (
      <div className="technical yes split">
        <h2>Has technical connection</h2>
      </div>
    );
  },

  renderNoConnection: function() {
    return (
      <div className="technical no split">
        <h2>No technical connection</h2>
        <p>Read <a href="#" onClick={this.props.onSwitchPanel("how_to_connect")}>how to connect</a></p>
      </div>
    );
  },

  renderLicense: function() {
    return this.props.app.license ? this.renderHasLicense() : this.renderNoLicense();
  },

  renderHasLicense: function() {
    return (
      <div className="license yes split">
        <h2>Has license</h2>
      </div>
    );
  },

  renderNoLicense: function() {
    return (
      <div className="license no split">
        <h2>License information unknown</h2>
        <p>Read <a href="#" onClick={this.props.onSwitchPanel("how_to_connect")}>how to connect</a></p>
      </div>
    );
  }
});
