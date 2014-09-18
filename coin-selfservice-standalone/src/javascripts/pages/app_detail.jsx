/** @jsx React.DOM */

App.Pages.AppDetail = React.createClass({
  panelMap: {
    "overview": App.Components.OverviewPanel,
    "license_info": App.Components.LicenseInfoPanel,
    "application_usage": App.Components.ApplicationUsagePanel,
    "attribute_policy": App.Components.AttributePolicyPanel,
    "how_to_connect": App.Components.HowToConnectPanel
  },

  getInitialState: function() {
    return {
      activePanel: this.props.activePanel
    }
  },

  getDefaultProps: function() {
    return {
      activePanel: "overview"
    }
  },

  render: function () {
    return (
      <div className="l-center">
        <div className="l-left">
          <div className="mod-app-nav">
            <ul>
              {Object.keys(this.panelMap).map(this.renderNavItem)}
            </ul>
          </div>
        </div>

        <App.Components.AppMeta app={this.props.app} />

        {this.renderActivePanel()}

      </div>
    );
  },

  renderNavItem: function(panel) {
    return (
      <li key={panel}><a href="#" onClick={this.handleSwitchPanel(panel)}>{I18n.t("apps.detail." + panel)}</a></li>
    );
  },

  renderActivePanel: function() {
    var component = this.panelMap[this.state.activePanel];
    return component({onSwitchPanel: this.handleSwitchPanel, app: this.props.app});
  },

  handleSwitchPanel: function(panel) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      this.setState({activePanel: panel});
      page.replace(page.uri("/apps/:id/:active_panel", { id: this.props.app.id, active_panel: panel }));
    }.bind(this);
  }
});
