/** @jsx React.DOM */

App.Pages.AppDetail = React.createClass({
  panelMap: {
    "overview": {
      component: App.Components.OverviewPanel,
      icon: "fa-list"
    },
    "license_info": {
      component: App.Components.LicenseInfoPanel,
      icon: "fa-file-text-o"
    },
    "application_usage": {
      component: App.Components.ApplicationUsagePanel,
      icon: "fa-area-chart"
    },
    "attribute_policy": {
      component: App.Components.AttributePolicyPanel,
      icon: "fa-table"
    },
    "how_to_connect": {
      component: App.Components.HowToConnectPanel,
      icon: "fa-chain"
    }
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

        <App.Components.AppMeta app={this.props.app} onSwitchPanel={this.handleSwitchPanel} />

        {this.renderActivePanel()}

      </div>
    );
  },

  renderNavItem: function(panelKey) {
    // do not include app usage in the left menu
    if (panelKey == "application_usage") {
      return;
    }

    if (panelKey == "how_to_connect") {
      if (!App.currentUser.dashboardAdmin) {
        return;
      }

      if (this.props.app.connected) {
        key = "how_to_disconnect";
      } else {
        key = "how_to_connect";
      }
    } else {
      key = panelKey;
    }

    var panel = this.panelMap[panelKey];
    return (
      <li key={panelKey}>
        <a href="#" onClick={this.handleSwitchPanel(panelKey)} className={panelKey == this.state.activePanel ? "current" : ""}>
          <i className={"fa " + panel.icon}></i>
          {I18n.t("apps.detail." + key)}
        </a>
      </li>
    );
  },

  renderActivePanel: function() {
    var panel = this.panelMap[this.state.activePanel];
    if (!panel || (this.state.activePanel == "how_to_connect" && !App.currentUser.dashboardAdmin)) {
      panel = this.panelMap["overview"];
    }
    return panel.component({onSwitchPanel: this.handleSwitchPanel, app: this.props.app});
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
