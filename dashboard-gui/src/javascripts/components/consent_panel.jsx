import React from "react";
import I18n from "i18n-js";
import PropTypes from "prop-types";
import {AppShape} from "../shapes";
import SelectWrapper from "./select_wrapper";
import stopEvent from "../utils/stop";
import ReactTooltip from "react-tooltip";
import {consentChangeRequest} from "../api";
import {setFlash} from "../utils/flash";
import {consentTypes} from "../utils/utils";

class ConsentPanel extends React.Component {

    constructor(props, context) {
        super(props, context);
        const {app} = this.props;
        const disableConsent = this.props.idpDisableConsent;
        const consent = disableConsent.find(dc => dc.spEntityId === app.spEntityId) || {
            spEntityId: app.spEntityId,
            type: "DEFAULT_CONSENT",
            explanationNl: "",
            explanationEn: ""
        };
        this.state = {consent};
    }

    render() {
        const isDashboardAdmin = this.context.currentUser.dashboardAdmin;
        const subTitle2 = isDashboardAdmin ? "subtitle2" : "subtitle2Viewer";
        return (
            <div className="l-middle-app-detail">
                <div className="mod-title">
                    <h1>{I18n.t("consent_panel.title")}</h1>
                    <p>{I18n.t("consent_panel.subtitle", {name: this.props.app.name})}</p>
                    <p className="info"
                       dangerouslySetInnerHTML={{__html: I18n.t(`consent_panel.${subTitle2}`, {name: this.props.app.name})}}></p>
                </div>
                {this.renderConsent(this.state.consent, isDashboardAdmin)}
            </div>
        );
    }

    saveRequest = e => {
        stopEvent(e);
        consentChangeRequest(this.state.consent)
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

    renderConsent(consent, isDashboardAdmin) {
        const msgAllowed = consent.type !== "no_consent";

        return <div className="mod-consent">
            <section className="change-form">
                <label>{I18n.t("consent_panel.consent_value")}
                    <i className="fa fa-info-circle" data-for="consent_value_tooltip" data-tip></i>
                    <ReactTooltip id="consent_value_tooltip" type="info" class="tool-tip" effect="solid"
                                  multiline={true}>
                        <span dangerouslySetInnerHTML={{__html: I18n.t("consent_panel.consent_value_tooltip")}}/>
                    </ReactTooltip>
                </label>
                <SelectWrapper
                    defaultValue={consent.type}
                    options={consentTypes.map(t => ({value: t, display: I18n.t(`consent_panel.${t.toLowerCase()}`)}))}
                    multiple={false}
                    isDisabled={!isDashboardAdmin}
                    handleChange={val => this.setState({consent: {...consent, type: val}})}/>

                {msgAllowed && <label>{I18n.t("consent_panel.explanationNl")}
                    <i className="fa fa-info-circle" data-for="explanationNl_tooltip" data-tip></i>
                    <ReactTooltip id="explanationNl_tooltip" type="info" class="tool-tip" effect="solid"
                                  multiline={true}>
                        <span dangerouslySetInnerHTML={{__html: I18n.t("consent_panel.explanationNl_tooltip")}}/>
                    </ReactTooltip>
                </label>}
                {msgAllowed && <input type="text" value={consent.explanationNl} disabled={!isDashboardAdmin}
                                      onChange={e => this.setState({
                                          consent: {
                                              ...consent,
                                              explanationNl: e.target.value
                                          }
                                      })}/>}

                {msgAllowed && <label>{I18n.t("consent_panel.explanationEn")}
                    <i className="fa fa-info-circle" data-for="explanationEn_tooltip" data-tip></i>
                    <ReactTooltip id="explanationEn_tooltip" type="info" class="tool-tip" effect="solid"
                                  multiline={true}>
                        <span dangerouslySetInnerHTML={{__html: I18n.t("consent_panel.explanationEn_tooltip")}}/>
                    </ReactTooltip>
                </label>}
                {msgAllowed && <input type="text" value={consent.explanationEn} disabled={!isDashboardAdmin}
                                      onChange={e => this.setState({
                                          consent: {
                                              ...consent,
                                              explanationEn: e.target.value
                                          }
                                      })}/>}
                {isDashboardAdmin && <a href="/save" className="t-button save"
                                        onClick={e => this.saveRequest(e)}>{I18n.t("consent_panel.save")}</a>}
            </section>

        </div>
    }

}

ConsentPanel.contextTypes = {
    currentUser: PropTypes.object
};

ConsentPanel.propTypes = {
    app: AppShape.isRequired,
    idpDisableConsent: PropTypes.array
};

export default ConsentPanel;
