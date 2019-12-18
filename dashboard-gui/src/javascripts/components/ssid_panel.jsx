import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import {withRouter} from "react-router";

import {AppShape} from "../shapes";
import stopEvent from "../utils/stop";
import {surfSecureIdChangeRequest} from "../api";
import {setFlash} from "../utils/flash";
import SelectWrapper from "./select_wrapper";
import {isEmpty} from "../utils/utils";

class SSIDPanel extends React.Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            loaLevel: "",
            options: [],
            highestLoaLevel: false
        };
    }

    componentDidMount() {
        const {app} = this.props;
        const {currentUser} = this.context;
        const loaLevel = app.minimalLoaLevel || "";
        let options = [];
        if (isEmpty(loaLevel)) {
            options.push({value: "", display: I18n.t("consent_panel.defaultLoa")});
        }
        options = options.concat(currentUser.loaLevels.map(t => (
            {value: t, display: I18n.t(`consent_panel.${t.substring(t.lastIndexOf("/") + 1).toLowerCase()}`)})
        ));
        this.setState({loaLevel: loaLevel, options: options, highestLoaLevel: loaLevel.endsWith("loa3")});
    }

    saveRequest = e => {
        stopEvent(e);
        const {loaLevel} = this.state;
        const {app} = this.props;
        surfSecureIdChangeRequest({entityId: app.spEntityId, loaLevel: loaLevel, entityType: app.entityType})
            .then(res => {
                res.json().then(action => {
                    if (action.payload["no-changes"]) {
                        setFlash(I18n.t("my_idp.no_change_request_created"), "warning");
                    } else {
                        setFlash(I18n.t("my_idp.change_request_created", {jiraKey: action.payload.jiraKey}));
                    }
                    window.scrollTo(0, 0);
                });
            }).catch(() => {
            setFlash(I18n.t("my_idp.change_request_failed"), "error");
            window.scrollTo(0, 0);
        });

    };

    renderSSID(isDashboardAdmin) {
        const {loaLevel, options, highestLoaLevel} = this.state;

        return (
            <div className="mod-ssid-panel">
                <section className="change-form">
                    <h2>{I18n.t("consent_panel.loa_level")}</h2>
                    {highestLoaLevel && <p class="error" dangerouslySetInnerHTML={{__html: I18n.t("ssid_panel.highestLoaReached")}}/>}
                    <SelectWrapper
                        defaultValue={loaLevel}
                        options={options}
                        multiple={false}
                        isDisabled={highestLoaLevel || !isDashboardAdmin}
                        handleChange={val => this.setState({loaLevel: val})}/>
                    {(isDashboardAdmin && !highestLoaLevel) && <a href="/save" className="t-button save"
                                            onClick={e => this.saveRequest(e)}>{I18n.t("consent_panel.save")}</a>}

                </section>

            </div>)
    }

    render() {
        const isDashboardAdmin = this.context.currentUser.dashboardAdmin;
        return (
            <div className="l-middle-app-detail">
                <div className="mod-title">
                    <h1 dangerouslySetInnerHTML={{__html: I18n.t("ssid_panel.title")}}/>
                    <p dangerouslySetInnerHTML={{__html: I18n.t("ssid_panel.subtitle")}}/>
                    <p className="info"
                       dangerouslySetInnerHTML={{__html: I18n.t("ssid_panel.subtitle2")}}/>
                    {isDashboardAdmin && <p className="info"
                                            dangerouslySetInnerHTML={{__html: I18n.t("ssid_panel.subtitle3")}}/>}
                </div>
                {this.renderSSID(isDashboardAdmin)}
            </div>
        );
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
