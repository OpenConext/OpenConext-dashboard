import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import {Link} from "react-router-dom";
import {withRouter} from "react-router";

import {AppShape} from "../shapes";
import {makeConnection, removeConnection, updateInviteRequest} from "../api";
import stopEvent from "../utils/stop";
import {privacyProperties} from "../utils/privacy";
import CheckBox from "./checkbox";
import {isEmpty} from "../utils/utils";
import {setFlash} from "../utils/flash";
import ConfirmationDialog from "../components/confirmation_dialog";
import SelectWrapper from "./select_wrapper";

class HowToConnectPanel extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            currentStep: "connect",
            accepted: false,
            comments: "",
            failed: false,
            action: undefined,
            acceptedAansluitOvereenkomstRefused: false,
            loaLevel: "",
            confirmationDialogOpen: false,
            confirmationQuestion: I18n.t("how_to_connect_panel.denyConfirmation"),
            confirmationDialogAction: () => this,
            cancelDialogAction: () => this.setState({confirmationDialogOpen: false})
        };
    }

    componentDidMount() {
        const {app, inviteAction, jiraKey} = this.props;
        let step = app.connected ? "disconnect" : "connect";
        if (app.connected && !isEmpty(jiraKey) && !isEmpty(inviteAction)) {
            step = "inviteActionCollision";
        }
        this.setState({currentStep: step});
    }

    getPanelRoute(panel) {
        const {app} = this.props;
        return `/apps/${app.id}/${app.entityType}/${panel}`;
    }

    renderConnectStep(isInvite) {
        const {confirmationDialogOpen, confirmationQuestion, confirmationDialogAction, cancelDialogAction} = this.state;
        let lastNumber = 3;
        const {app} = this.props;
        const {currentUser} = this.context;
        if (app.entityType === "single_tenant_template") {
            ++lastNumber;
        }
        if (app.aansluitovereenkomstRefused) {
            ++lastNumber;
        }
        if (isEmpty(app.minimalLoaLevel)) {
            ++lastNumber;
        }
        const classNameConnect = this.state.accepted && (!app.aansluitovereenkomstRefused || this.state.acceptedAansluitOvereenkomstRefused) ? "" : "disabled";
        const hasPrivacyInfo = privacyProperties.some(prop => app.privacyInfo[prop]);
        const title = isInvite ? "connect_invite_title" : "connect_title";
        const subTitle = isInvite ? "info_sub_invite_title" : "info_sub_title";
        const automaticallyConnect = app.dashboardConnectOption === "CONNECT_WITHOUT_INTERACTION_WITH_EMAIL" ||
            app.dashboardConnectOption === "CONNECT_WITHOUT_INTERACTION_WITHOUT_EMAIL";
        const shareInstitutionId = app.institutionId === currentUser.getCurrentIdp().institutionId;
        const actionName = isInvite ? "approve" : (automaticallyConnect ? "automatic_connect" : "connect");
        const subTitleAutomaticConnection = automaticallyConnect ? I18n.t("how_to_connect_panel.info_connection_without_interaction")
            : (shareInstitutionId ? I18n.t("how_to_connect_panel.info_connection_share_institution") : "");
        return (
            <div className="l-middle-app-detail">
                <ConfirmationDialog isOpen={confirmationDialogOpen}
                                    cancel={cancelDialogAction}
                                    confirm={confirmationDialogAction}
                                    question={confirmationQuestion}/>
                <div className="mod-title">
                    <h1>{I18n.t(`how_to_connect_panel.${title}`, {app: app.name})}</h1>
                    <p>{I18n.t(`how_to_connect_panel.${subTitle}`)} {subTitleAutomaticConnection}</p>
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
                        {isEmpty(app.minimalLoaLevel) && this.renderLoaLevel()}
                        <div className="content">
                            <div className="number">{isEmpty(app.minimalLoaLevel) ? 3 : 2}</div>
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
                                    {I18n.t("how_to_connect_panel.forward_permission.after", {app: app.name})}
                                </li>

                                <li>
                                    {I18n.t("how_to_connect_panel.obtain_license.before")}
                                    <Link to={this.getPanelRoute("license_data")}>
                                        {I18n.t("how_to_connect_panel.license")}
                                    </Link>
                                    {I18n.t("how_to_connect_panel.obtain_license.after", {app: app.name})}
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
                        {this.renderAansluitovereenkomstRefusedWarning(app.entityType === "single_tenant_template" ? 4 : 3)}
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
                        {isInvite &&
                        <a href="/deny" className="c-button white"
                           onClick={this.denyInvite(this.props.jiraKey)}>{I18n.t(`how_to_connect_panel.deny`)}</a>}
                        <a href="/connection" className={"c-button " + classNameConnect}
                           onClick={this.handleMakeConnection(isInvite)}>{I18n.t(`how_to_connect_panel.${actionName}`)}</a>
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
                    <a href={this.props.app.wikiUrl} target="_blank" rel="noopener noreferrer">
                        {I18n.t("how_to_connect_panel.wiki")}
                    </a>
                </li>
            );
        }
        return null;
    }

    renderLoaLevel = () => {
        const {currentUser} = this.context;
        const {loaLevel} = this.state;
        const options = [{value: "", display: I18n.t("consent_panel.defaultLoa")}]
            .concat(currentUser.loaLevels.map(t => (
                {value: t, display: I18n.t(`consent_panel.${t.substring(t.lastIndexOf("/") + 1).toLowerCase()}`)})
            ));

        return (
            <div>
                <div className="content">
                    <div className="number">2</div>
                    <h2>{I18n.t("consent_panel.loa_level")}</h2>
                    <p dangerouslySetInnerHTML={{__html: I18n.t("ssid_panel.subtitle3")}}/>
                    <SelectWrapper
                        defaultValue={loaLevel}
                        options={options}
                        multiple={false}
                        handleChange={val => this.setState({loaLevel: val})}/>
                </div>
                <hr/>
            </div>)

    };

    renderSingleTenantServiceWarning() {
        if (this.props.app.entityType === "single_tenant_template") {
            return (
                <div>
                    <hr/>
                    <div className="content">
                        <div className="number">4</div>
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

    doDenyInvite = jiraKey => e => {
        stopEvent(e);
        this.setState({confirmationDialogOpen: false});
        updateInviteRequest({status: "REJECTED", jiraKey: jiraKey, comment: this.state.comments}).then(() => {
            const app = this.props.app;
            setFlash(I18n.t("how_to_connect_panel.invite_denied", {jiraKey: jiraKey}));
            this.props.history.replace(`/apps/${app.id}/${app.entityType}/overview`);
            window.scrollTo(0, 0);
        })
    };


    denyInvite = jiraKey => e => {
        stopEvent(e);
        this.setState({
            confirmationDialogOpen: true,
            confirmationQuestion: I18n.t("how_to_connect_panel.deny_invitation", {app: this.props.app.name}),
            confirmationDialogAction: this.doDenyInvite(jiraKey)
        });
    };

    renderDenyInvitation = jiraKey => {
        const {confirmationDialogOpen, confirmationQuestion, confirmationDialogAction, cancelDialogAction} = this.state;
        const app = this.props.app;
        return <div className="l-middle-app-detail mod-connect">
            <ConfirmationDialog isOpen={confirmationDialogOpen}
                                cancel={cancelDialogAction}
                                confirm={confirmationDialogAction}
                                question={confirmationQuestion}/>
            <div className="mod-title">
                <h1>{I18n.t("how_to_connect_panel.deny_invitation", {app: app.name})}</h1>
                <p>{I18n.t("how_to_connect_panel.deny_invitation_info")}</p>
                <br/>
                <p>{I18n.t("how_to_connect_panel.comments_description")}</p>
                <textarea rows="5"
                          value={this.state.comments}
                          onChange={e => this.setState({comments: e.target.value})}
                          placeholder={I18n.t("how_to_connect_panel.comments_placeholder")}/>
                <p className="cta">
                    <a href="/deny" onClick={this.doDenyInvite(jiraKey)}
                       className="c-button">{I18n.t("how_to_connect_panel.deny")}</a>
                    <a href="/accept" className="c-button white approve" onClick={e => {
                        stopEvent(e);
                        this.props.history.replace(`/dummy`);
                        setTimeout(() => this.props.history.replace(`/apps/${this.props.app.id}/${this.props.app.entityType}/how_to_connect/${jiraKey}/accept`), 10);
                    }}>{I18n.t("how_to_connect_panel.approve")}</a>
                </p>
            </div>
        </div>
    };

    renderDoneStep() {
        if (this.state.action.connectWithoutInteraction) {
            const rejectedOrDone = this.state.action.rejected ? "rejected" : "done";
            return (
                <div className="l-middle-app-detail">
                    <div className="mod-title">
                        <h1>{I18n.t("how_to_connect_panel." + rejectedOrDone + "_without_interaction_title")}</h1>
                        <p>{I18n.t("how_to_connect_panel." + rejectedOrDone + "_without_interaction_subtitle")}</p>
                        <br/>
                        <p className="cta">
                            <a href="/apps" onClick={this.backToServices.bind(this)}
                               className="c-button">{I18n.t("how_to_connect_panel.back_to_apps")}</a>
                        </p>
                    </div>
                </div>
            );
        }
        const jiraKey = this.props.jiraKey || this.state.action.jiraKey;
        const subtitle = jiraKey ?
            I18n.t("how_to_connect_panel.done_subtitle_with_jira_html", {jiraKey: jiraKey}) :
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
        this.props.history.replace("/apps");
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
                        <textarea rows="5"
                                  value={this.state.comments}
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

    renderInviteActionCollision = (jiraKey, app) => {
        return <div className="l-middle-app-detail">
            <div className="mod-title">
                <h1>{I18n.t("how_to_connect_panel.invite_action_collision_title", {app: app.name})}</h1>
            </div>
            <div className="mod-connect">
                <div className="box">
                    <div className="content">
                        <h2>{I18n.t("how_to_connect_panel.invite_action_collision_subtitle")}</h2>
                        <p dangerouslySetInnerHTML={{
                            __html: I18n.t("how_to_connect_panel.invite_action_collision", {
                                app: app.name,
                                jiraKey: jiraKey
                            })
                        }}/>
                    </div>
                </div>
            </div>
        </div>;
    };

    renderStagingNoConnectAllowed = (app) => {
        return <div className="l-middle-app-detail">
            <div className="mod-title">
                <h1>{I18n.t("how_to_connect_panel.test_connected_no_connection_title", {app: app.name})}</h1>
            </div>
            <div className="mod-connect">
                <div className="box">
                    <div className="content">
                        <h2>{I18n.t("how_to_connect_panel.test_connected_no_connection_subtitle")}</h2>
                        <p dangerouslySetInnerHTML={{__html: I18n.t("how_to_connect_panel.test_connected_no_connection", {app: app.name})}}/>
                    </div>
                </div>
            </div>
        </div>;
    };

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
            const promise = isInvite ? updateInviteRequest({
                    status: "ACCEPTED",
                    comment: this.state.comments,
                    jiraKey: this.props.jiraKey
                }) :
                makeConnection(this.props.app, this.state.comments, this.state.loaLevel);
            promise
                .then(action => {
                    this.setState({currentStep: "done", action: action}, () => window.scrollTo(0, 0));
                })
                .catch(() => this.setState({failed: true}));

        }
    };

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
        const {currentUser} = this.context;
        const currentIdp = currentUser.getCurrentIdp();
        const {jiraKey, inviteAction, conflictingJiraIssue, app} = this.props;
        if (failed) {
            return (
                <div className="mod-not-found">
                    <h1>{I18n.t("how_to_connect_panel.jira_unreachable")}</h1>
                    <p>{I18n.t("how_to_connect_panel.jira_unreachable_description")} </p>
                </div>
            );
        }
        if (currentIdp.state === "testaccepted") {
            return this.renderStagingNoConnectAllowed(app);
        }
        if (currentStep === "connect" &&
            !currentIdp.publishedInEdugain &&
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
            case "inviteActionCollision":
                return this.renderInviteActionCollision(jiraKey, app);
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

export default withRouter(HowToConnectPanel);
