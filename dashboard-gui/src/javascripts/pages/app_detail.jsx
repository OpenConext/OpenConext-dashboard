import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import {Link} from "react-router-dom";

import {disableConsent, getApp, getIdps} from "../api";

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
import {privacyProperties} from "../utils/privacy";

const componentsOrdering = ["overview", "how_to_connect", "consent", "attribute_policy", "license_data", "privacy",
     "idp_usage", "sirtfi_security", "application_usage"];

class AppDetail extends React.Component {
    constructor() {
        super();

        this.panelMap = {
            "overview": {
                component: OverviewPanel,
                icon: "fa-list"
            },
            "license_data": {
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
                icon: "fa-university"
            },
            "how_to_connect": {
                component: HowToConnectPanel,
                icon: "fa-chain"
            }

        };

        this.state = {
            app: null,
            institutions: [],
            idpDisableConsent: []
        };
    }

    componentWillMount() {
        Promise.all([getApp(this.props.match.params.id, this.props.match.params.type), disableConsent()])
        .then(data => {
            const app = data[0].payload;
            const hasPrivacyInfo = privacyProperties.some(prop => app.privacyInfo[prop]);
            if (hasPrivacyInfo) {
                this.panelMap = {
                    ...this.panelMap,  "privacy": {
                        component: PrivacyPanel,
                        icon: "fa-lock"
                    }
                };
            }
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
                        icon: "fa-clipboard"
                    }
                }
            }
            this.setState({app: app, idpDisableConsent: data[1]});
            getIdps(app.spEntityId).then(data => this.setState({institutions: data.payload}));
        });

    }

    render() {
        if (this.state.app) {
            const panelKeys = componentsOrdering.filter(k => this.panelMap[k]);
            return (
                <div className="l-center-app-detail">
                    <Flash/>
                    <div className="l-app-detail">
                        <div className="mod-app-nav">
                            <ul>
                                <li key="back">
                                    <Link to="/apps">
                                        <i className="fa fa-arrow-left"></i>
                                        {I18n.t("apps.detail.back")}
                                    </Link>
                                </li>
                                {panelKeys.map(panelKey => this.renderNavItem(panelKey))}
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

    renderNavItem(panelKey) {
        const {currentUser} = this.context;
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

        return <Component app={this.state.app}
                          institutions={this.state.institutions}
                          idpDisableConsent={this.state.idpDisableConsent}/>;
    }
}

AppDetail.contextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object
};

export default AppDetail;
