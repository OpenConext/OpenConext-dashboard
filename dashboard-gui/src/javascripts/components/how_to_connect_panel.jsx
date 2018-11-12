import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import {Link} from "react-router-dom";

import {AppShape} from "../shapes";
import {makeConnection, removeConnection, updateInviteRequest} from "../api";
import stopEvent from "../utils/stop";
import {privacyProperties} from "../utils/privacy";
import CheckBox from "./checkbox";
import {isEmpty} from "../utils/utils";
import {setFlash} from "../utils/flash";

class HowToConnectPanel extends React.Component {
    constructor() {
        super();

        this.state = {
            currentStep: "connect",
            accepted: false,
            comments: "",
            failed: false,
            action: undefined,
            acceptedAansluitOvereenkomstRefused: false
        };
    }

    componentWillMount() {
        this.setState({currentStep: this.props.app.connected ? "disconnect" : "connect"});
    }

    getPanelRoute(panel) {
        const {app} = this.props;
        return `/apps/${app.id}/${app.exampleSingleTenant ? "single_tenant_template" : "saml20_sp"}/${panel}`;
    }

    renderConnectStep(isInvite) {
        let lastNumber = 3;
        if (this.props.app.exampleSingleTenant) {
            ++lastNumber;
        }
        if (this.props.app.aansluitovereenkomstRefused) {
            ++lastNumber;
        }
        const classNameConnect = this.state.accepted && (!this.props.app.aansluitovereenkomstRefused || this.state.acceptedAansluitOvereenkomstRefused) ? "" : "disabled";
        const hasPrivacyInfo = privacyProperties.some(prop => this.props.app.privacyInfo[prop]);
        const title = isInvite ? "connect_invite_title" : "connect_title";
        const subTitle = isInvite ? "info_sub_invite_title" : "info_sub_title";
        const actionName = isInvite ? "approve" : "connect";
        return (
            <div className="l-middle-app-detail">
                <div className="mod-title">
                    <h1>{I18n.t(`how_to_connect_panel.${title}`, {app: this.props.app.name})}</h1>
                    <p>{I18n.t(`how_to_connect_panel.${subTitle}`)}</p>
                </div>

                <div className="mod-connect">
                    <div className="box">
                        <div className="content">
                            <div className="number">1</div>
                            <h2>{I18n.t("how_to_connect_panel.checklist")}</h2>
                            <ul>
                                <li>
                                    {I18n.t("how_to_connect_panel.check")}&nbsp;
                                    <Link to={this.getPanelRoute("license_data")}>
                                        {I18n.t("how_to_connect_panel.license_info")}
                                    </Link>
                                </li>
                                <li>
                                    {I18n.t("how_to_connect_panel.check")}&nbsp;
                                    <Link to={this.getPanelRoute("attribute_policy")}>
                                        {I18n.t("how_to_connect_panel.attributes_policy")}
                                    </Link>
                                </li>
                                {hasPrivacyInfo && <li>
                                    {I18n.t("how_to_connect_panel.check")}&nbsp;
                                    <Link to={this.getPanelRoute("privacy")}>
                                        {I18n.t("how_to_connect_panel.privacy_policy")}
                                    </Link>
                                </li>}
                                <li>
                                    <span
                                        dangerouslySetInnerHTML={{__html: I18n.t("how_to_connect_panel.processing_agreements")}}/>
                                </li>
                                {this.renderWikiUrl()}
                            </ul>
                        </div>
                        <hr/>
                        <div className="content">
                            <div className="number">2</div>
                            <h2>{I18n.t("how_to_connect_panel.terms_title")}</h2>
                            <ul>
                                <li>
                                    {I18n.t("how_to_connect_panel.provide_attributes.before")}
                                    <Link to={this.getPanelRoute("attribute_policy")}>
                                        {I18n.t("how_to_connect_panel.attributes")}
                                    </Link>
                                    {I18n.t("how_to_connect_panel.provide_attributes.after")}
                                </li>

                                <li>
                                    {I18n.t("how_to_connect_panel.forward_permission.before")}
                                    <Link to={this.getPanelRoute("attribute_policy")}>
                                        {I18n.t("how_to_connect_panel.attributes")}
                                    </Link>
                                    {I18n.t("how_to_connect_panel.forward_permission.after", {app: this.props.app.name})}
                                </li>

                                <li>
                                    {I18n.t("how_to_connect_panel.obtain_license.before")}
                                    <Link to={this.getPanelRoute("license_data")}>
                                        {I18n.t("how_to_connect_panel.license")}
                                    </Link>
                                    {I18n.t("how_to_connect_panel.obtain_license.after", {app: this.props.app.name})}
                                </li>
                            </ul>
                            <br/>
                            <p>
                                <label>
                                    <input type="checkbox" checked={this.state.accepted}
                                           onChange={e => this.setState({accepted: e.target.checked})}/>
                                    &nbsp;
                                    {I18n.t("how_to_connect_panel.accept")}
                                </label>
                            </p>
                        </div>
                        {this.renderSingleTenantServiceWarning()}
                        {this.renderAansluitovereenkomstRefusedWarning(this.props.app.exampleSingleTenant ? 4 : 3)}
                        <hr/>
                        <div className="content">
                            <div className="number">{lastNumber}</div>
                            <h2>{I18n.t("how_to_connect_panel.comments_title")}</h2>
                            <p>{I18n.t("how_to_connect_panel.comments_description")}</p>
                            <textarea rows="5" value={this.state.comments}
                                      onChange={e => this.setState({comments: e.target.value})}
                                      placeholder={I18n.t("how_to_connect_panel.comments_placeholder")}/>
                        </div>
                    </div>
                    <p className="cta">
                        <a href="/connection" className={"c-button " + classNameConnect}
                           onClick={this.handleMakeConnection(isInvite)}>{I18n.t(`how_to_connect_panel.${actionName}`)}</a>
                        {isInvite &&
                        <a href="/deny" className="c-button white"
                           onClick={this.denyInvite(this.props.jiraKey)}>{I18n.t(`how_to_connect_panel.deny`)}</a>}
                    </p>
                </div>
            </div>
        );
    }

    renderWikiUrl() {
        if (this.props.app.wikiUrl) {
            return (
                <li>
                    {I18n.t("how_to_connect_panel.read")}&nbsp;
                    <a href={this.props.app.wikiUrl} target="_blank">
                        {I18n.t("how_to_connect_panel.wiki")}
                    </a>
                </li>
            );
        }

        return null;
    }

    renderSingleTenantServiceWarning() {
        if (this.props.app.exampleSingleTenant) {
            return (
                <div>
                    <hr/>
                    <div className="content">
                        <div className="number">3</div>
                        <h2>{I18n.t("overview_panel.single_tenant_service")}</h2>
                        <p
                            dangerouslySetInnerHTML={{__html: I18n.t("overview_panel.single_tenant_service_html", {name: this.props.app.name})}}/>
                        <p>{I18n.t("how_to_connect_panel.single_tenant_service_warning")}</p>
                    </div>
                </div>
            );
        }
        return null;
    }


    renderAansluitovereenkomstRefusedWarning(number) {
        if (this.props.app.aansluitovereenkomstRefused) {
            return (
                <div>
                    <hr/>
                    <div className="content">
                        <div className="number">{number}</div>
                        <h2>{I18n.t("overview_panel.aansluitovereenkomst")}</h2>
                        <p
                            dangerouslySetInnerHTML={{__html: I18n.t("overview_panel.aansluitovereenkomstRefused", {name: this.props.app.name})}}/>
                        <label>
                            <input type="checkbox" checked={this.state.acceptedAansluitOvereenkomstRefused}
                                   onChange={e => this.setState({acceptedAansluitOvereenkomstRefused: e.target.checked})}/>
                            &nbsp;
                            {I18n.t("how_to_connect_panel.aansluitovereenkomst_accept")}
                        </label>
                    </div>
                </div>
            );
        }

        return null;
    }

    renderDoneDisconnectStep() {
        const subtitle = this.state.action.jiraKey ?
            I18n.t("how_to_connect_panel.done_disconnect_subtitle_html_with_jira_html", {jiraKey: this.state.action.jiraKey}) :
            I18n.t("how_to_connect_panel.done_disconnect_subtitle_html");
        return (
            <div className="l-middle-app-detail">
                <div className="mod-title">
                    <h1>{I18n.t("how_to_connect_panel.done_disconnect_title")}</h1>
                    <p dangerouslySetInnerHTML={{__html: subtitle}}/>
                    <br/>
                    <p className="cta">
                        <a href="/apps" onClick={this.backToServices.bind(this)}
                           className="c-button">{I18n.t("how_to_connect_panel.back_to_apps")}</a>
                    </p>
                </div>
            </div>
        );
    }

    denyInvite = jiraKey => e => {
        stopEvent(e);
        updateInviteRequest({status: "REJECTED", jiraKey: jiraKey}).then(() => {
            const app = this.props.app;
            const type = app.exampleSingleTenant ? "single_tenant_template" : "saml20_sp";
            setFlash(I18n.t("how_to_connect_panel.invite_denied", {jiraKey: jiraKey}));
            this.context.router.history.replace(`/apps/${app.id}/${type}/overview`);
            window.scrollTo(0, 0);
        })
    };

    renderDenyInvitation = jiraKey => {
        const app = this.props.app;
        const type = app.exampleSingleTenant ? "single_tenant_template" : "saml20_sp";
        return <div className="l-middle-app-detail">
            <div className="mod-title">
                <h1>{I18n.t("how_to_connect_panel.deny_invitation")}</h1>
                <p>{I18n.t("how_to_connect_panel.deny_invitation_info")}</p>
                <br/>
                <p className="cta">
                    <a href="/deny" onClick={this.denyInvite(jiraKey)}
                       className="c-button">{I18n.t("how_to_connect_panel.deny")}</a>
                    <Link className="c-button white"
                          to={`/apps/${this.props.app.id}/${type}/how_to_connect/${jiraKey}/accept`}>
                        {I18n.t("how_to_connect_panel.approve")}
                    </Link>
                </p>
            </div>
        </div>
    };

    renderDoneStep() {
        const jiraKey = this.state.jiraKey || this.state.action.jiraKey;
        const subtitle = jiraKey ?
            I18n.t("how_to_connect_panel.done_subtitle_with_jira_html", {jiraKey: this.state.action.jiraKey}) :
            I18n.t("how_to_connect_panel.done_subtitle_html");

        return (
            <div className="l-middle-app-detail">
                <div className="mod-title">
                    <h1>{I18n.t("how_to_connect_panel.done_title")}</h1>
                    <p dangerouslySetInnerHTML={{__html: subtitle}}/>
                    <br/>
                    <p className="cta">
                        <a href="/apps" onClick={this.backToServices.bind(this)}
                           className="c-button">{I18n.t("how_to_connect_panel.back_to_apps")}</a>
                    </p>
                </div>
            </div>
        );
    }

    backToServices(e) {
        stopEvent(e);
        this.context.router.history.replace("/apps");
    }

    renderDisconnectStep = () =>
        <div className="l-middle-app-detail">
            <div className="mod-title">
                <h1>{I18n.t("how_to_connect_panel.disconnect_title", {app: this.props.app.name})}</h1>
            </div>

            <div className="mod-connect">
                <div className="box">
                    <div className="content">
                        <h2>{I18n.t("how_to_connect_panel.comments_title")}</h2>
                        <p>{I18n.t("how_to_connect_panel.comments_description")}</p>
                        <textarea value={this.state.comments}
                                  onChange={e => this.setState({comments: e.target.value})}
                                  placeholder={I18n.t("how_to_connect_panel.comments_placeholder")}/>
                        <CheckBox name="disclaimer"
                                  value={this.state.checked}
                                  info={I18n.t("how_to_connect_panel.accept_disconnect", {app: this.props.app.name})}
                                  onChange={e => this.setState({accepted: e.target.checked})}/>
                    </div>
                </div>
                <p className="cta">
                    <a href="/disconnect" className={"c-button " + (this.state.accepted ? "" : "disabled")}
                       onClick={this.handleDisconnect.bind(this)}>{I18n.t("how_to_connect_panel.disconnect")}</a>
                </p>
            </div>
        </div>;

    renderJiraConflict = (action, isConnection) => {
        const message = I18n.t("apps.detail.outstandingIssue",
            {
                jiraKey: action.jiraKey,
                type: I18n.t("history.action_types_name." + action.type),
                status: I18n.t("history.statuses." + action.status)
            });
        const title = isConnection ? I18n.t("how_to_connect_panel.connect_title", {app: this.props.app.name}) :
            I18n.t("how_to_connect_panel.disconnect_title", {app: this.props.app.name});
        return <div className="l-middle-app-detail">
            <div className="mod-title">
                <h1>{title}</h1>
            </div>
            <div className="mod-connect">
                <div className="box">
                    <div className="content">
                        <h2>{message}</h2>
                        <p dangerouslySetInnerHTML={{__html: I18n.t("how_to_connect_panel.disconnect_jira_info", {jiraKey: action.jiraKey})}}/>
                    </div>
                </div>
            </div>
        </div>;
    };

    handleMakeConnection = isInvite => e => {
        const allowed = this.state.accepted &&
            (!this.props.app.aansluitovereenkomstRefused || this.state.acceptedAansluitOvereenkomstRefused) &&
            this.context.currentUser.dashboardAdmin;
        stopEvent(e);
        if (allowed) {
            const promise = isInvite ? updateInviteRequest({status: "ACCEPTED", jiraKey: this.props.jiraKey}) :
                makeConnection(this.props.app, this.state.comments);
            promise
                .then(action => this.setState({currentStep: "done", action: action}, () => window.scrollTo(0, 0)))
                .catch(() => this.setState({failed: true}));

        }
    }

    handleDisconnect(e) {
        stopEvent(e);
        if (this.state.accepted && this.context.currentUser.dashboardAdmin) {
            removeConnection(this.props.app, this.state.comments)
                .then(action =>
                    this.setState({
                        currentStep: "done-disconnect",
                        action: action
                    }, () => window.scrollTo(0, 0)))
                .catch(() => this.setState({failed: true}));
        }
    }

    render() {
        const {failed, currentStep} = this.state;
        const {jiraKey, inviteAction, conflictingJiraIssue} = this.props;
        if (failed) {
            return (
                <div className="mod-not-found">
                    <h1>{I18n.t("how_to_connect_panel.jira_unreachable")}</h1>
                    <p>{I18n.t("how_to_connect_panel.jira_unreachable_description")} </p>
                </div>
            );
        }
        if (currentStep === "connect" &&
            !this.context.currentUser.currentIdp.publishedInEdugain &&
            this.props.app.publishedInEdugain && isEmpty(conflictingJiraIssue)) {
            return (
                <div className="mod-edugain">
                    <h1>{I18n.t("how_to_connect_panel.not_published_in_edugain_idp")}</h1>
                    <p>{I18n.t("how_to_connect_panel.not_published_in_edugain_idp_info", {name: this.props.app.name})} </p>
                    <Link className="link-edit-id" to={"/my-idp/edit"}>
                        {I18n.t("how_to_connect_panel.edit_my_idp_link")}
                    </Link>
                </div>
            );
        }

        switch (currentStep) {
            case "disconnect":
                return conflictingJiraIssue ? this.renderJiraConflict(conflictingJiraIssue, false) : this.renderDisconnectStep();
            case "connect":
                return conflictingJiraIssue ? this.renderJiraConflict(conflictingJiraIssue, true) :
                    inviteAction === "deny" ? this.renderDenyInvitation(jiraKey) : this.renderConnectStep(jiraKey && inviteAction);
            case "done":
                return this.renderDoneStep();
            case "done-disconnect":
                return this.renderDoneDisconnectStep();
            default:
                return null;
        }
    }

}

HowToConnectPanel.contextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object
};

HowToConnectPanel.propTypes = {
    app: AppShape.isRequired,
    jiraKey: PropTypes.string,
    inviteAction: PropTypes.string,
    conflictingJiraIssue: PropTypes.object
};

export default HowToConnectPanel;
