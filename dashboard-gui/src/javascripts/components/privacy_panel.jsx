import React from "react";
import I18n from "i18n-js";

import {AppShape} from "../shapes";
import {privacyProperties} from "../utils/privacy";

class PrivacyPanel extends React.Component {

    render() {
        return (
            <div className="l-middle-app-detail">
                <div className="mod-title">
                    <h1>{I18n.t("privacy_panel.title")}</h1>
                    <p>{I18n.t("privacy_panel.subtitle", {name: this.props.app.name})}</p>
                    <p className="info">{I18n.t("privacy_panel.subtitle2", {name: this.props.app.name})}</p>
                </div>
                {this.renderPrivacy(this.props.app)}
            </div>
        );
    }

    renderPrivacy(app) {
        return (
            <div className="mod-privacy-info">
                <table>
                    <thead>
                    <tr>
                        <th className="question">{I18n.t("privacy_panel.question")}</th>
                        <th answer="question">{I18n.t("privacy_panel.answer")}
                        </th>
                    </tr>
                    </thead>
                    <tbody>
                    {privacyProperties.map(prop => this.renderPrivacyProp(prop, app.privacyInfo[prop]))}
                    </tbody>
                </table>
            </div>
        );
    }

    renderPrivacyProp(name, prop) {
        const isDate = name === "certificationValidTo" || name === "certificationValidFrom";
        const noValue = prop === undefined || prop === null;
        let value;
        if (isDate && !noValue) {
            value = prop.substring(0, 10);
        } else if (prop === true) {
            value = I18n.t("boolean.yes");
        } else if (prop === false) {
            value = I18n.t("boolean.no");
        } else {
            value = prop;
        }
        return (
            <tr key={name}>
                <td>{I18n.t(`privacy_panel.${name}`)}</td>
                <td>
                    {noValue ? <em>{I18n.t("privacy_panel.noInformation")}</em> : value}
                </td>
            </tr>);
    }


}

PrivacyPanel.propTypes = {
    app: AppShape.isRequired
};

export default PrivacyPanel;
