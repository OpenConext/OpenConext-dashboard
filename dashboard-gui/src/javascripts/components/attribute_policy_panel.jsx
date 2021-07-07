import React from "react";
import I18n from "i18n-js";
import PropTypes from "prop-types";
import marked from "marked";
import ReactTooltip from "react-tooltip";
import {AppShape} from "../shapes";
import {isEmpty} from "../utils/utils";

class AttributePolicyPanel extends React.Component {

  renderAttributeReleasePolicy(app) {
    if (app.arp.noArp) {
      return <p>{I18n.t("attributes_policy_panel.arp.noarp", {name: app.name})}</p>;
    } else if (app.arp.noAttrArp) {
      return <p>{I18n.t("attributes_policy_panel.arp.noattr", {name: app.name})}</p>;
    }

    const hasFilters = app.filteredUserAttributes.some(attribute => attribute.filters.filter(filter => filter !== "*").length > 0);

    const nameIdValue = app
      .nameIds.filter(val => val.includes("unspecified") || val.includes("persistent"))
      .length ? "Persistent" : "Transient";
    const {currentUser} = this.context;
    return (
      <div className="mod-attributes">
        <table>
          <thead>
            <tr>
              <th className="attribute">{I18n.t("attributes_policy_panel.attribute")}</th>
              {!currentUser.guest &&
                        <th className="value">{I18n.t("attributes_policy_panel.your_value")}</th>}
              <th className="motivation">{I18n.t("attributes_policy_panel.motivation")}</th>
            </tr>
          </thead>
          <tbody>
            {app.filteredUserAttributes.map(this.renderAttribute.bind(this))}
          </tbody>
        </table>
        <p>{I18n.t("attributes_policy_panel.warning")}</p>
        <ul className="attributes-policy-warnings">
          {!currentUser.guest && <li>{I18n.t("attributes_policy_panel.hint")}</li>}
          <li>{I18n.t("attributes_policy_panel.motivationInfo")}</li>
          {hasFilters && <li>{I18n.t("attributes_policy_panel.filterInfo")}</li>}
          <li dangerouslySetInnerHTML={{__html: I18n.t("attributes_policy_panel.nameIdInfo", {type: nameIdValue})}}/>
        </ul>
      </div>
    );
  }

  renderManipulationScript(app) {
    if (!app.manipulationNotes) {
      return null;
    }
    const notes = marked(app.manipulationNotes).replace(/<a href/g, "<a target=\"_blank\" href");
    return (
      <div className="manipulation-notes">
        <p className="title">{I18n.t("attributes_policy_panel.arp.manipulation")}</p>
        <section className="notes" dangerouslySetInnerHTML={{__html: notes}}/>
      </div>
    );
  }

  renderAttribute(attribute) {
    const {currentUser} = this.context;
    const renderFilters = attribute.filters.filter(flt => flt !== "*");

    let name = attribute.name;
    let tooltip = undefined;

    if (name === "urn:oid:1.3.6.1.4.1.1076.20.40.40.1") {
      name = "collabPersonId";
      tooltip = "Collab unique user identifier";
    }

    return (
      <tr key={name}>
        <td>
          {name}
          {tooltip && (
            <span>
              <i className="fa fa-info-circle" data-for={name} data-tip/>
              <ReactTooltip id={name} type="info" class="tool-tip" effect="solid">{tooltip}</ReactTooltip>
            </span>
          )}
          {
            (renderFilters.length > 0 && !currentUser.guest) &&
                        <span className="filter-info">{I18n.t("attributes_policy_panel.filter")}</span>
          }
        </td>
        {!currentUser.guest && <td>
          <ul>
            {
              attribute.userValues.length > 0 ?
                attribute.userValues.map(val => <li key={val}>{val}</li>) :
                (
                  <em className="no-attribute-value">{
                    name.indexOf("eduPersonTargetedID") > 0 ?
                      I18n.t("attributes_policy_panel.attribute_value_generated") :
                      I18n.t("attributes_policy_panel.no_attribute_value")
                  }</em>
                )
            }
          </ul>
          {
            renderFilters.length > 0 &&
                        <ul className="filters">
                          {renderFilters.map((filter, i) => <li key={i} className="filter">- {filter}</li>)}
                        </ul>
          }
        </td>}
        <td>{this.props.app.motivations[name]}</td>
      </tr>
    );
  }

  renderResourceServers(app) {
    if (isEmpty(app.resourceServers)) {
      return null;
    }
    const {resourceServers} = this.props;
    return (
      <div className="resource-servers">
        <p className="title">{I18n.t("attributes_policy_panel.arp.resourceServers")}</p>
        <ul className="resource-servers">
          {resourceServers.sort((a, b) => a.name.localeCompare(b.name)).map((rs, index) =>
            <li key={index}>
              {rs.name}
            </li>)}
        </ul>
      </div>
    );
  }

  render() {
    const {app} = this.props;
    return (
      <div className="l-middle-app-detail">
        <div className="mod-title">
          <h1>{I18n.t("attributes_policy_panel.title")}</h1>

          <p>{I18n.t("attributes_policy_panel.subtitle", {name: app.name})}</p>
        </div>
        {this.renderAttributeReleasePolicy(app)}
        {this.renderResourceServers(app)}
        {this.renderManipulationScript(app)}
      </div>
    );
  }
}

AttributePolicyPanel.contextTypes = {
  currentUser: PropTypes.object
};

AttributePolicyPanel.propTypes = {
  app: AppShape.isRequired,
  resourceServers: PropTypes.array.isRequired
};

export default AttributePolicyPanel;
