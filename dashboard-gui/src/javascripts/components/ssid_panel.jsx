import React from "react";
import PropTypes from "prop-types";
// import I18n from "i18n-js";
import {withRouter} from "react-router";

import {AppShape} from "../shapes";
// import {removeConnection} from "../api";
// import stopEvent from "../utils/stop";
// import CheckBox from "./checkbox";

class SSIDPanel extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            currentStep: "initial",
            comments: "",
            action: undefined,
            failed: false
        };
    }


    // renderDoneStep() {
    //     const subtitle = this.state.action.jiraKey ?
    //         I18n.t("how_to_connect_panel.done_disconnect_subtitle_html_with_jira_html", {jiraKey: this.state.action.jiraKey}) :
    //         I18n.t("how_to_connect_panel.done_disconnect_subtitle_html");
    //     return (
    //         <div className="l-middle-app-detail">
    //             <div className="mod-title">
    //                 <h1>{I18n.t("ssid_panel.jira_ticket_submitted_title")}</h1>
    //                 <p dangerouslySetInnerHTML={{__html: subtitle}}/>
    //                 <br/>
    //                 <p className="cta">
    //                     <a href="/apps" onClick={this.backToServices.bind(this)}
    //                        className="c-button">{I18n.t("ssid_panel.back_to_apps")}</a>
    //                 </p>
    //             </div>
    //         </div>
    //     );
    // }
    //
    // backToServices(e) {
    //     stopEvent(e);
    //     this.props.history.replace("/apps");
    // }
    //
    // renderUpdateSSIDStep = app =>
    //     <div className="l-middle-app-detail">
    //         <div className="mod-title">
    //             <h1>{I18n.t("how_to_connect_panel.disconnect_title", {app: this.props.app.name})}</h1>
    //         </div>
    //
    //         <div className="mod-connect">
    //             <div className="box">
    //                 <div className="content">
    //                     <h2>{I18n.t("how_to_connect_panel.comments_title")}</h2>
    //                     <p>{I18n.t("how_to_connect_panel.comments_description")}</p>
    //                     <textarea rows="5"
    //                               value={this.state.comments}
    //                               onChange={e => this.setState({comments: e.target.value})}
    //                               placeholder={I18n.t("how_to_connect_panel.comments_placeholder")}/>
    //                     <CheckBox name="disclaimer"
    //                               value={this.state.checked}
    //                               info={I18n.t("how_to_connect_panel.accept_disconnect", {app: this.props.app.name})}
    //                               onChange={e => this.setState({accepted: e.target.checked})}/>
    //                 </div>
    //             </div>
    //             <p className="cta">
    //                 <a href="/disconnect" className={"c-button " + (this.state.accepted ? "" : "disabled")}
    //                    onClick={this.handleDisconnect.bind(this)}>{I18n.t("how_to_connect_panel.disconnect")}</a>
    //             </p>
    //         </div>
    //     </div>;
    //
    // handleSumit(e) {
    //     stopEvent(e);
    //     if (this.state.accepted && this.context.currentUser.dashboardAdmin) {
    //         removeConnection(this.props.app, this.state.comments)
    //             .then(action =>
    //                 this.setState({
    //                     currentStep: "done-disconnect",
    //                     action: action
    //                 }, () => window.scrollTo(0, 0)))
    //             .catch(() => this.setState({failed: true}));
    //     }
    // }
    //
    // render() {
    //     const {failed, currentStep} = this.state;
    //     const {currentUser} = this.context;
    //     const currentIdp = currentUser.getCurrentIdp();
    //     const {app} = this.props;
    //     if (failed) {
    //         return (
    //             <div className="mod-not-found">
    //                 <h1>{I18n.t("how_to_connect_panel.jira_unreachable")}</h1>
    //                 <p>{I18n.t("how_to_connect_panel.jira_unreachable_description")} </p>
    //             </div>
    //         );
    //     }
    //     switch (currentStep) {
    //         case "initial":
    //             return this.renderUpdateSSIDStep(app);
    //         case "done":
    //             return this.renderDoneStep(app);
    //         default:
    //             return null;
    //     }
    // }

}

SSIDPanel.contextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object
};

SSIDPanel.propTypes = {
    app: AppShape.isRequired,
    jiraKey: PropTypes.string,
    inviteAction: PropTypes.string,
    conflictingJiraIssue: PropTypes.object
};

export default withRouter(SSIDPanel);
