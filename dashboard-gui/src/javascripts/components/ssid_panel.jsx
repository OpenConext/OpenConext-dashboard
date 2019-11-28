import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import {withRouter} from "react-router";

import {AppShape} from "../shapes";
import stopEvent from "../utils/stop";
import {surfSecureIdChangeRequest} from "../api";
import {setFlash} from "../utils/flash";
import SelectWrapper from "./select_wrapper";

class SSIDPanel extends React.Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            loaLevel: "",
        };

    }

    render() {
        const isDashboardAdmin = this.context.currentUser.dashboardAdmin;
        return (
            <div className="l-middle-app-detail">
                <div className="mod-title">
                    <h1>{I18n.t("ssid_panel.title")}</h1>
                    <p>{I18n.t("ssid_panel.subtitle")}</p>
                    <p className="info"
                       dangerouslySetInnerHTML={{__html: I18n.t("ssid_panel.subtitle2")}}/>
                    {isDashboardAdmin && <p className="info"
                                            dangerouslySetInnerHTML={{__html: I18n.t("ssid_panel.subtitle3")}}/>}
                </div>
                {this.renderSSID(isDashboardAdmin)}
            </div>
        );
    }

    saveRequest = e => {
        stopEvent(e);
        const {loaLevel} = this.state;
        const {app} = this.props;
        surfSecureIdChangeRequest({spEntityId: app.spEntityId, loaLevel})
            .then(res => {
                res.json().then(action => {
                    if (action.payload["no-changes"]) {
                        setFlash(I18n.t("consent_panel.no_change_request_created"), "warning");
                    } else {
                        setFlash(I18n.t("consent_panel.change_request_created"));
                    }
                    window.scrollTo(0, 0);
                });
            }).catch(() => {
            setFlash(I18n.t("consent_panel.change_request_failed"), "error");
            window.scrollTo(0, 0);
        });

    };

    renderSSID(isDashboardAdmin) {
        const {currentUser} = this.context;
        const {loaLevel} = this.state;
        const options = [{value: "", display: I18n.t("consent_panel.defaultLoa")}]
            .concat(currentUser.loaLevels.map(t => (
                {value: t, display: I18n.t(`consent_panel.${t.substring(t.lastIndexOf("/") + 1).toLowerCase()}`)})
            ));

        return (
            <div className="mod-consent">
                <section className="change-form">
                    <h2>{I18n.t("consent_panel.loa_level")}</h2>
                    <SelectWrapper
                        defaultValue={loaLevel}
                        options={options}
                        multiple={false}
                        handleChange={val => this.setState({loaLevel: val})}/>
                    {isDashboardAdmin && <a href="/save" className="t-button save"
                                            onClick={e => this.saveRequest(e)}>{I18n.t("consent_panel.save")}</a>}

                </section>

            </div>)
    }

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
