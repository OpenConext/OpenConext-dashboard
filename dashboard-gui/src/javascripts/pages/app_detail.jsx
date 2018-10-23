import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import {Link} from "react-router-dom";

import {getApp, getIdps} from "../api";

import AppMeta from "../components/app_meta";
import OverviewPanel from "../components/overview_panel";
import LicenseInfoPanel from "../components/license_info_panel";
import ApplicationUsagePanel from "../components/application_usage_panel";
import AttributePolicyPanel from "../components/attribute_policy_panel";
import IdpUsagePanel from "../components/idp_usage_panel";
import HowToConnectPanel from "../components/how_to_connect_panel";
import SirtfiPanel from "../components/sirtfi_panel";
import PrivacyPanel from "../components/privacy_panel";
import ConsentPanel from "../components/consent_panel";
import Flash from "../components/flash";

class AppDetail extends React.Component {
    constructor() {
        super();

        this.panelMap = {
            "overview": {
                component: OverviewPanel,
                icon: "fa-list"
            },
            "license_info": {
                component: LicenseInfoPanel,
                icon: "fa-file-text-o"
            },
            "application_usage": {
                component: ApplicationUsagePanel,
                icon: "fa-area-chart"
            },
            "attribute_policy": {
                component: AttributePolicyPanel,
                icon: "fa-table"
            },
            "idp_usage": {
                component: IdpUsagePanel,
                icon: "fa-clipboard"
            },
            "how_to_connect": {
                component: HowToConnectPanel,
                icon: "fa-chain"
            },
            "privacy": {
                component: PrivacyPanel,
                icon: "fa-lock"
            }
        };

        this.state = {
            app: null,
            institutions: []
        };
    }

    componentWillMount() {
        getApp(this.props.match.params.id, this.props.match.params.type).then(data => {
            const app = data.payload;
            if (app.contactPersons && app.contactPersons.filter(cp => cp.sirtfiSecurityContact).length > 0) {
                this.panelMap = {
                    ...this.panelMap, "sirtfi_security": {
                        component: SirtfiPanel,
                        icon: "fa-users"
                    }
                };
            }
            if (app.connected) {
                this.panelMap = {
                    ...this.panelMap, "consent": {
                        component: ConsentPanel,
                        icon: "fa-university"
                    }
                }
            }
            this.setState({app: app});
            return getIdps(app.spEntityId).then(data => this.setState({institutions: data.payload}));
        });

    }

    render() {
        if (this.state.app) {
            return (
                <div className="l-center">
                    <Flash/>
                    <div className="l-left">
                        <div className="mod-app-nav">
                            <ul>
                                {Object.keys(this.panelMap).map(panelKey => this.renderNavItem(panelKey))}
                            </ul>
                        </div>
                        <br/>

                        <div className="mod-app-nav">
                            <ul>
                                {this.renderNavItem("application_usage", true)}
                            </ul>
                        </div>
                    </div>

                    <AppMeta app={this.state.app}/>

                    {this.renderActivePanel()}

                </div>
            );
        }

        return null;
    }

    renderNavItem(panelKey, force) {
        const {currentUser} = this.context;
        // do not include app usage in the top left menu
        if (panelKey === "application_usage" && force !== true) {
            return null;
        }

        let key = null;

        if (panelKey === "how_to_connect") {
            if (!(currentUser.dashboardAdmin && currentUser.getCurrentIdp().institutionId)) {
                return null;
            }

            if (this.state.app.connected) {
                key = "how_to_disconnect";
            } else {
                key = "how_to_connect";
            }
        } else {
            key = panelKey;
        }

        const panel = this.panelMap[panelKey];
        return (
            <li key={panelKey}>
                <Link to={`/apps/${this.props.match.params.id}/${this.props.match.params.type}/${panelKey}`}
                      className={panelKey === this.props.match.params.activePanel ? "current" : ""}>
                    <i className={"fa " + panel.icon}></i>
                    {I18n.t("apps.detail." + key)}
                </Link>
            </li>
        );
    }

    renderActivePanel() {
        const {activePanel} = this.props.match.params;
        const {currentUser} = this.context;
        let panel = this.panelMap[activePanel];
        if (!panel || (activePanel === "how_to_connect" && !(currentUser.dashboardAdmin && currentUser.getCurrentIdp().institutionId))) {
            panel = this.panelMap["overview"];
        }

        const Component = panel.component;

        return <Component app={this.state.app} institutions={this.state.institutions}/>;
    }
}

AppDetail.contextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object
};

export default AppDetail;
