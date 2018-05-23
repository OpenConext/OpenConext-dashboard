import React from "react";
import I18n from "i18n-js";

import {AppShape} from "../shapes";

class PrivacyPanel extends React.Component {

  render() {
    return (
      <div className="l-middle">
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
    const properties = ["accessData", "certification", "certificationLocation", "country", "otherInfo", "privacyPolicy",
      "privacyPolicyUrl", "securityMeasures", "snDpaWhyNot", "surfmarketDpaAgreement", "surfnetDpaAgreement", "whatData",
      "certificationValidFrom", "certificationValidTo"];
    return (
      <div className="mod-privacy-info">
        <table>
          <thead>
          <tr>
            <th className="question">{I18n.t("privacy_panel.question")}</th>
            <th  answer="question">{I18n.t("privacy_panel.answer")}
            </th>
          </tr>
          </thead>
          <tbody>
          {properties.map(prop => this.renderPrivacyProp(prop, app.privacyInfo[prop]))}
          </tbody>
        </table>
        <p>
          <span className="star">*</span> {I18n.t("attributes_policy_panel.hint")}</p>
      </div>
    );
  }

  renderPrivacyProp(name, prop) {
    return (
      <tr key={name}>
        <td>{I18n.t(`privacy_panel.${name}`)}</td>
        <td>
          {(prop === undefined || prop === null) ? <em>{I18n.t("privacy_panel.noInformation")}</em> :
            prop === true ? I18n.t("boolean.yes") :
              prop === false ? I18n.t("boolean.no") : prop}
        </td>
      </tr>);
  }


}

PrivacyPanel.propTypes = {
  app: AppShape.isRequired
};

export default PrivacyPanel;
