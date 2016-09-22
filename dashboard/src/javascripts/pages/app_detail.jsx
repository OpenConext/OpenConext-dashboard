import React from "react";
import I18n from "../lib/i18n";

import { getApp, getIdps } from "../api";

import AppMeta from "../components/app_meta";
import OverviewPanel from "../components/overview_panel";

class AppDetail extends React.Component {
  constructor() {
    super();

    this.panelMap = {
      "overview": {
        component: OverviewPanel,
        icon: "fa-list"
      },
      "license_info": {
        // component: App.Components.LicenseInfoPanel,
        icon: "fa-file-text-o"
      },
      "application_usage": {
        // component: App.Components.ApplicationUsagePanel,
        icon: "fa-area-chart"
      },
      "attribute_policy": {
        // component: App.Components.AttributePolicyPanel,
        icon: "fa-table"
      },
      "idp_usage": {
        // component: App.Components.IdpUsagePanel,
        icon: "fa-clipboard"
      },
      "how_to_connect": {
        // component: App.Components.HowToConnectPanel,
        icon: "fa-chain"
      }
    };

    this.state = {
      app: null,
      institutions: []
    };
  }

  componentWillMount() {
    const { currentUser } = this.context;
    const idpId = currentUser.getCurrentIdpId();

    getApp(this.props.params.id, idpId).then(data => {
      const app = data.payload;

      return getIdps(app.spEntityId, idpId).then(data => {
        const institutions = data.payload;
        this.setState({ app, institutions });
      });
    });

  }

  render() {
    if (this.state.app) {
      return (
        <div className="l-center">
          <div className="l-left">
            <div className="mod-app-nav">
              <ul>
                {Object.keys(this.panelMap).map((panelKey) => this.renderNavItem(panelKey))}
              </ul>
            </div>
            <br />

            <div className="mod-app-nav">
              <ul>
                {this.renderNavItem("application_usage", true)}
              </ul>
            </div>
          </div>

          <AppMeta app={this.state.app} />

          {this.renderActivePanel()}

        </div>
      );
    }

    return null;
  }

  renderNavItem(panelKey, force) {
    const { currentUser } = this.context;
    // do not include app usage in the top left menu
    if (panelKey == "application_usage" && force != true) {
      return;
    }

    let key = null;

    if (panelKey == "how_to_connect") {
      if (!(currentUser.dashboardAdmin && currentUser.getCurrentIdp().institutionId)) {
        return;
      }

      if (this.state.app.connected) {
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
        <a href="#" onClick={(e) => this.handleSwitchPanel(e, panelKey)}
           className={panelKey == this.props.params.activePanel ? "current" : ""}>
          <i className={"fa " + panel.icon}></i>
          {I18n.t("apps.detail." + key)}
        </a>
      </li>
    );
  }

  renderActivePanel() {
    const { activePanel } = this.props.params;
    const { currentUser } = this.context;
    var panel = this.panelMap[activePanel];
    if (!panel || (activePanel == "how_to_connect" && !(currentUser.dashboardAdmin && currentUser.getCurrentIdp().institutionId))) {
      panel = this.panelMap["overview"];
    }

    const Component = panel.component;

    return <Component app={this.state.app} institutions={this.state.institutions} onSwitchPanel={(e, panel) => this.handleSwitchPanel(e, panel)} />;
  }

  handleSwitchPanel(e, panel) {
    const { router } = this.context;

    e.preventDefault();
    e.stopPropagation();
    this.setState({activePanel: panel});

    router.transitionTo(`/apps/${this.props.params.id}/${panel}`);
  }
}

AppDetail.defaultProps = {
  activePanel: "overview"
};

AppDetail.contextTypes = {
  currentUser: React.PropTypes.object,
  router: React.PropTypes.object
};

export default AppDetail;
