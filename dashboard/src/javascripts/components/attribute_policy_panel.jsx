import React from "react";
import I18n from "i18n-js";

import { AppShape } from "../shapes";

class AttributePolicyPanel extends React.Component {
  render() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("attributes_policy_panel.title")}</h1>

          <p>{I18n.t("attributes_policy_panel.subtitle", { name: this.props.app.name })}</p>
        </div>
        {this.renderAttributeReleasePolicy(this.props.app)}
      </div>
    );
  }

  renderAttributeReleasePolicy(app) {
    if (app.arp.noArp) {
      return (
        <p>{I18n.t("attributes_policy_panel.arp.noarp", { name: app.name })}</p>
      );
    } else if (app.arp.noAttrArp) {
      return (
        <p>{I18n.t("attributes_policy_panel.arp.noattr", { name: app.name })}</p>
      );
    }

    return (
      <div className="mod-attributes">
        <table>
          <thead>
            <tr>
              <th>{I18n.t("attributes_policy_panel.attribute")}</th>
              <th>{I18n.t("attributes_policy_panel.your_value")}
                <span className="star">*</span>
              </th>
            </tr>
          </thead>
          <tbody>
            {app.filteredUserAttributes.map(this.renderAttribute)}
          </tbody>
        </table>
        <p>
          <span className="star">*</span> {I18n.t("attributes_policy_panel.hint")}</p>
      </div>
    );
  }

  renderAttribute(attribute) {
    return (
      <tr key={attribute.name}>
        <td>{attribute.name}</td>
        <td>
          <ul>
            {attribute.userValues.map(this.renderAttributeValue)}
          </ul>
        </td>
      </tr>
    );
  }

  renderAttributeValue(attributeValue) {
    return (
      <li key={attributeValue}>{attributeValue}</li>
    );
  }
}

AttributePolicyPanel.propTypes = {
  app: AppShape.isRequired
};

export default AttributePolicyPanel;
