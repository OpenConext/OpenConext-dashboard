import React from "react";
import I18n from "i18n-js";
import Link from "react-router/Link";

import {AppShape} from "../shapes";
import {makeConnection, removeConnection} from "../api";

class HowToConnectPanel extends React.Component {
    constructor() {
        super();

        this.state = {
            currentStep: "connect",
            accepted: false,
            comments: "",
            failed: false,
            acceptedAansluitOvereenkomstRefused: false
        };
    }

    componentWillMount() {
        this.setState({currentStep: this.props.app.connected ? "disconnect" : "connect"});
    }

    render() {
        if (this.state.failed) {
            return (
                <div className="mod-not-found">
                    <h1>{I18n.t("how_to_connect_panel.jira_unreachable")}</h1>
                    <p>{I18n.t("how_to_connect_panel.jira_unreachable_description")} </p>
                </div>
            );
        }
        if (this.state.currentStep === "connect" &&
            !this.context.currentUser.currentIdp.publishedInEdugain &&
            this.props.app.publishedInEdugain) {
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

        switch (this.state.currentStep) {
            case "disconnect":
                return this.renderDisconnectStep();
            case "connect":
                return this.renderConnectStep();
            case "done":
                return this.renderDoneStep();
            case "done-disconnect":
                return this.renderDoneDisconnectStep();
            default:
                return null;
        }
    }

    getPanelRoute(panel) {
        const {app} = this.props;
        return `/apps/${app.id}/${app.exampleSingleTenant ? "single_tenant_template" : "saml20_sp"}/${panel}`;
    }

    renderConnectStep() {
        let lastNumber = 3;
        if (this.props.app.exampleSingleTenant) {
            ++lastNumber;
        }
        if (this.props.app.aansluitovereenkomstRefused) {
            ++lastNumber;
        }
        const classNameConnect = this.state.accepted && (!this.props.app.aansluitovereenkomstRefused || this.state.acceptedAansluitOvereenkomstRefused) ? "" : "disabled";
        return (
            <div className="l-middle">
                <div className="mod-title">
                    <h1>{I18n.t("how_to_connect_panel.connect_title", {app: this.props.app.name})}</h1>
                    <p>{I18n.t("how_to_connect_panel.info_sub_title")}</p>
                </div>

                <div className="mod-connect">
                    <div className="box">
                        <div className="content">
                            <div className="number">1</div>
                            <h2>{I18n.t("how_to_connect_panel.checklist")}</h2>
                            <ul>
                                <li>
                                    {I18n.t("how_to_connect_panel.check")}&nbsp;
                                    <Link to={this.getPanelRoute("license_info")}>
                                        {I18n.t("how_to_connect_panel.license_info")}
                                    </Link>
                                </li>
                                <li>
                                    {I18n.t("how_to_connect_panel.check")}&nbsp;
                                    <Link to={this.getPanelRoute("attribute_policy")}>
                                        {I18n.t("how_to_connect_panel.attributes_policy")}
                                    </Link>
                                </li>
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
                                    <Link to={this.getPanelRoute("license_info")}>
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
                        <a href="#" className={"c-button " + classNameConnect}
                           onClick={this.handleMakeConnection.bind(this)}>{I18n.t("how_to_connect_panel.connect")}</a>
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
            <div className="l-middle">
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

    renderDoneStep() {
        const subtitle = this.state.action.jiraKey ?
            I18n.t("how_to_connect_panel.done_subtitle_with_jira_html", {jiraKey: this.state.action.jiraKey}) :
            I18n.t("how_to_connect_panel.done_subtitle_html");

        return (
            <div className="l-middle">
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
        e.preventDefault();
        e.stopPropagation();
        this.context.router.transitionTo("/apps");
    }

    renderDisconnectStep() {
        return (
            <div className="l-middle">
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
                            <label>
                                <input type="checkbox" checked={this.state.checked}
                                       onChange={e => this.setState({accepted: e.target.checked})}/>

                                {I18n.t("how_to_connect_panel.accept_disconnect", {app: this.props.app.name})}
                            </label>
                        </div>
                    </div>
                    <p className="cta">
                        <a href="#" className={"c-button " + (this.state.accepted ? "" : "disabled")}
                           onClick={this.handleDisconnect.bind(this)}>{I18n.t("how_to_connect_panel.disconnect")}</a>
                    </p>
                </div>
            </div>
        );
    }

    handleMakeConnection(e) {
        const allowed = this.state.accepted &&
            (!this.props.app.aansluitovereenkomstRefused || this.state.acceptedAansluitOvereenkomstRefused) &&
            this.context.currentUser.dashboardAdmin;
        e.preventDefault();
        if (allowed) {
            makeConnection(this.props.app, this.state.comments)
                .then(action => {
                    this.setState({currentStep: "done", action: action}, () => window.scrollTo(0, 0))
                }).catch(() => this.setState({failed: true}));
        }
    }

    handleDisconnect(e) {
        e.preventDefault();
        if (this.state.accepted && this.context.currentUser.dashboardAdmin) {
            removeConnection(this.props.app, this.state.comments)
                .then(action => {
                    this.setState({
                        currentStep: "done-disconnect",
                        action: action
                    }, () => window.scrollTo(0, 0))
                })
                .catch(() => this.setState({failed: true}));
        }
    }
}

HowToConnectPanel.contextTypes = {
    currentUser: React.PropTypes.object,
    router: React.PropTypes.object
};

HowToConnectPanel.propTypes = {
    app: AppShape.isRequired
};

export default HowToConnectPanel;
