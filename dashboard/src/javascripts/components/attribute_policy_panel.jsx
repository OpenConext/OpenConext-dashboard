import React from "react";
import I18n from "i18n-js";
import marked from "marked";
import ReactTooltip from "react-tooltip";
import {AppShape} from "../shapes";

class AttributePolicyPanel extends React.Component {
    render() {
        return (
            <div className="l-middle">
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
        const renderFilters = attribute.filters.filter(filter => filter !== "*");
        const name = attribute.name;
        const isCollabPersonId = name === "urn:oid:1.3.6.1.4.1.1076.20.40.40.1";
        return (
            <tr key={name}>
                <td>{isCollabPersonId ? "collabPersonId" : name}
                    {isCollabPersonId && <span>
                            <i className="fa fa-info-circle" data-for="collabPersonId" data-tip></i>
                                <ReactTooltip id="collabPersonId" type="info" class="tool-tip" effect="solid">
                                    <span>Collab unique user identifier</span>
                                </ReactTooltip>
                        </span>}
                    {renderFilters.length > 0 &&
                    <span className="filter-info">{I18n.t("attributes_policy_panel.filter")}</span>}
                </td>
                <td>
                    <ul>
                        {attribute.userValues.length > 0 ? attribute.userValues.map(this.renderAttributeValue) :
                            <em className="no-attribute-value">{I18n.t("attributes_policy_panel.no_attribute_value")}</em>}
                    </ul>
                    {renderFilters.length > 0 && <ul className="filters">
                        {renderFilters.map((filter, index) => <li key={index} className="filter">- {filter}</li>)}
                    </ul>}
                </td>
                <td>{this.props.app.motivations[name]}</td>
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
