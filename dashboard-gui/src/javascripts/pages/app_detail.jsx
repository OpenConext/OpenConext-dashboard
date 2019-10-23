import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import {Link} from "react-router-dom";
import {clearFlash, setFlash} from "../utils/flash";
import {disableConsent, getApp, getIdps, getServicesByEntityIds, searchJira} from "../api";

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
import {isEmpty} from "../utils/utils";

const componentsOrdering = ["overview", "how_to_connect", "consent", "attribute_policy", "license_data", "privacy",
    "idp_usage", "sirtfi_security", "application_usage"];

class AppDetail extends React.Component {

    constructor() {
        super();
        this.state = {
            app: null,
            institutions: [],
            idpDisableConsent: [],
            jiraKey: undefined,
            inviteAction: undefined,
            conflictingJiraIssue: undefined,
            resourceServers: []
        };
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
    }

    componentWillMount() {
        const params = this.props.match.params;
        Promise.all([getApp(params.id, params.type), disableConsent()])
            .then(data => {
                const app = data[0].payload;
                const hasPrivacyInfo = privacyProperties.some(prop => app.privacyInfo[prop]);
                if (hasPrivacyInfo) {
                    this.panelMap = {
                        ...this.panelMap, "privacy": {
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
                const {currentUser} = this.context;
                const currentIdp = currentUser.getCurrentIdp();
                const removeStats = currentUser.guest || (currentUser.dashboardMember && !currentIdp.displayStatsInDashboard);

                if (app.connected && currentUser.manageConsentEnabled) {
                    this.panelMap = {
                        ...this.panelMap, "consent": {
                            component: ConsentPanel,
                            icon: "fa-clipboard"
                        }
                    }
                }
                if (currentUser.guest) {
                    delete this.panelMap["how_to_connect"];
                    delete this.panelMap["sirtfi_security"];
                    delete this.panelMap["consent"];
                    delete this.panelMap["license_data"];
                }
                if (currentUser.dashboardMember) {
                    delete this.panelMap["how_to_connect"];
                    delete this.panelMap["sirtfi_security"];
                    delete this.panelMap["consent"];
                }
                if (removeStats) {
                    delete this.panelMap["application_usage"];
                }
                const jiraFilter = {
                    maxResults: 1,
                    startAt: 0,
                    spEntityId: app.spEntityId,
                    statuses: params.jiraKey ? [] : ["To Do", "In Progress", "Awaiting Input"],
                    types: ["LINKREQUEST", "UNLINKREQUEST", "LINKINVITE"],
                    key: params.jiraKey || null
                };
                if (currentUser.guest || currentUser.dashboardMember) {
                    this.setState({app: app, idpDisableConsent: data[1]});
                } else {
                    searchJira(jiraFilter).then(res => {
                        const newState = {
                            app: app,
                            idpDisableConsent: data[1],
                            jiraKey: params.jiraKey,
                            inviteAction: params.action
                        };
                        if (res.payload.total > 0) {
                            const action = res.payload.issues[0];
                            if (params.jiraKey && action.status !== "Awaiting Input") {
                                const i18nParam = action.status === "Closed" ? "denied" : "approved";
                                setFlash(I18n.t("apps.detail.inviteAlreadyProcessed", {
                                    jiraKey: action.jiraKey,
                                    action: I18n.t(`apps.detail.${i18nParam}`),
                                }), "warning");
                                newState.conflictingJiraIssue = action;
                            } else if (!params.jiraKey) {
                                let message = I18n.t("apps.detail.outstandingIssue", {
                                    jiraKey: action.jiraKey,
                                    type: I18n.t("history.action_types_name." + action.type),
                                    status: I18n.t("history.statuses." + action.status)
                                });
                                if (action.type === "LINKINVITE" && action.status === "Awaiting Input" && !app.connected) {
                                    message += I18n.t("apps.detail.outstandingIssueLink", {
                                        link: `/apps/${this.props.match.params.id}/${this.props.match.params.type}/how_to_connect/${action.jiraKey}/accept`,
                                        linkName: I18n.t("apps.detail.how_to_connect")
                                    });
                                    newState.jiraKey = action.jiraKey;
                                    newState.inviteAction = "accept";
                                } else {
                                    newState.conflictingJiraIssue = action;
                                }
                                setFlash(message, "warning");
                            }
                        }
                        this.setState(newState);
                    });
                }
                getIdps(app.spEntityId, params.type).then(res => this.setState({institutions: res.payload}));
                if (!isEmpty(app.resourceServers)) {
                    getServicesByEntityIds(app.resourceServers).then(res => this.setState({resourceServers: res.payload}));
                }
            });

    }

    componentWillUnmount() {
        clearFlash(null);
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
        const clearJiraState = () => this.setState({jiraKey: undefined, inviteAction: undefined});
        return (
            <li key={panelKey}>
                <Link to={`/apps/${this.props.match.params.id}/${this.props.match.params.type}/${panelKey}`}
                      onClick={clearJiraState}
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
        const {app, institutions, idpDisableConsent, jiraKey, inviteAction, conflictingJiraIssue, resourceServers} = this.state;
        let panel = this.panelMap[activePanel];
        if (!panel || (activePanel === "how_to_connect" && !(currentUser.dashboardAdmin && currentUser.getCurrentIdp().id))) {
            panel = this.panelMap["overview"];
        }

        const Component = panel.component;

        return <Component app={app}
                          institutions={institutions}
                          idpDisableConsent={idpDisableConsent}
                          jiraKey={jiraKey}
                          inviteAction={inviteAction}
                          conflictingJiraIssue={conflictingJiraIssue}
                          resourceServers={resourceServers}/>;
    }

    render() {
        if (this.state.app) {
            const panelKeys = componentsOrdering.filter(k => this.panelMap[k]);
            return (
                <div className="l-center-app-detail">
                    <Flash className="flash no-margin-bottom"/>
                    <div className="l-app-detail">
                        <div className="mod-app-nav">
                            <ul>
                                <li key="back" className="back">
                                    <Link to="/apps/back">
                                        <i className="fa fa-arrow-left"></i>
                                        {I18n.t("apps.detail.back")}
                                    </Link>
                                </li>
                                {panelKeys.map(panelKey => this.renderNavItem(panelKey))}
                            </ul>
                        </div>
                    </div>
                    <div className="app-detail-container">
                        <AppMeta app={this.state.app}/>
                        {this.renderActivePanel()}
                    </div>
                </div>
            );
        }
        return null;
    }

}

AppDetail.contextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object
};

export default AppDetail;
