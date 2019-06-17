import React from "react";
import I18n from "i18n-js";
import marked from "marked";
import ReactTooltip from "react-tooltip";
import {AppShape} from "../shapes";

class AttributePolicyPanel extends React.Component {
    render() {
        return (
            <div className="l-middle-app-detail">
                <div className="mod-title">
                    <h1>{I18n.t("attributes_policy_panel.title")}</h1>

                    <p>{I18n.t("attributes_policy_panel.subtitle", {name: this.props.app.name})}</p>
                </div>
                {this.renderAttributeReleasePolicy(this.props.app)}
                {this.renderManipulationScript(this.props.app)}
            </div>
        );
    }

    renderAttributeReleasePolicy(app) {
        if (app.arp.noArp) {
            return <p>{I18n.t("attributes_policy_panel.arp.noarp", {name: app.name})}</p>;
        } else if (app.arp.noAttrArp) {
            return <p>{I18n.t("attributes_policy_panel.arp.noattr", {name: app.name})}</p>;
        }
        const hasFilters = app.filteredUserAttributes.some(attribute => attribute.filters.filter(filter => filter !== "*").length > 0);
        return (
            <div className="mod-attributes">
                <table>
                    <thead>
                    <tr>
                        <th className="attribute">{I18n.t("attributes_policy_panel.attribute")}</th>
                        <th className="value">{I18n.t("attributes_policy_panel.your_value")}</th>
                        <th className="motivation">{I18n.t("attributes_policy_panel.motivation")}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {app.filteredUserAttributes.map(this.renderAttribute.bind(this))}
                    </tbody>
                </table>
                <p>{I18n.t("attributes_policy_panel.warning")}</p>
                <ul className="attributes-policy-warnings">
                    <li>{I18n.t("attributes_policy_panel.hint")}</li>
                    <li>{I18n.t("attributes_policy_panel.motivationInfo")}</li>
                    {hasFilters && <li>{I18n.t("attributes_policy_panel.filterInfo")}</li>}
                </ul>
            </div>
        );
    }

    renderManipulationScript(app) {
        if (!app.manipulationNotes) {
            return null;
        }
        const notes = marked(app.manipulationNotes).replace(/<a href/g, "<a target=\"_blank\" href");
        return <div className="manipulation-notes">
            <p className="title">{I18n.t("attributes_policy_panel.arp.manipulation")}</p>
            <section className="notes" dangerouslySetInnerHTML={{__html: notes}}/>
        </div>;
    }

    renderAttribute(attribute) {
        const renderFilters = attribute.filters.filter(flt => flt !== "*");
        let name = attribute.name;
        let values = attribute.userValues;
        let noValueMessage = I18n.t("attributes_policy_panel.no_attribute_value")
        let tooltip = undefined;

        if (name === "urn:oid:1.3.6.1.4.1.1076.20.40.40.1") {
          name = "collabPersonId";
          tooltip = "Collab unique user identifier";
        }

        if (name === "nameIds") {
          const addition = I18n.t("attributes_policy_panel.name_id_addition")

          tooltip = I18n.t("attributes_policy_panel.name_id_format_tooltip")
          noValueMessage += addition

          values = values
            .filter(val => val.includes("unspecified") || val.includes("persistent"))
            .length > 0 ? ['Persistent'] : ["Transient"]
            .map(val => val += addition)
        }

        return (
            <tr key={name}>
                <td>
                    {name}
                    {tooltip && (
                        <span>
                            <i className="fa fa-info-circle" data-for={name} data-tip />
                            <ReactTooltip id={name} type="info" class="tool-tip" effect="solid">
                                <span dangerouslySetInnerHTML={{__html: tooltip}}/>
                            </ReactTooltip>
                        </span>
                    )}
                    {
                      renderFilters.length > 0 &&
                        <span className="filter-info">{I18n.t("attributes_policy_panel.filter")}</span>
                    }
                </td>
                <td>
                    <ul>
                      {
                        values.length > 0 ?
                          values.map(val => <li key={val} dangerouslySetInnerHTML={{__html: val}} />) :
                          (
                            <em className="no-attribute-value">
                              <span dangerouslySetInnerHTML={{__html: noValueMessage}}/>
                            </em>
                          )
                      }
                    </ul>
                    {
                      renderFilters.length > 0 &&
                        <ul className="filters">
                          {renderFilters.map((filter, i) => <li key={i} className="filter">- {filter}</li>)}
                        </ul>
                    }
                </td>
                <td>{this.props.app.motivations[name]}</td>
            </tr>
        );
    }
}

AttributePolicyPanel.propTypes = {
    app: AppShape.isRequired
};

export default AttributePolicyPanel;
