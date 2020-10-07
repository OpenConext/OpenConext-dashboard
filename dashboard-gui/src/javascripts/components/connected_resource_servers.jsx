import React from "react";
import I18n from "i18n-js";
import PropTypes from "prop-types";
import {AppShape} from "../shapes";
import {isEmpty} from "../utils/utils";

class ConnectedResourceServersPanel extends React.Component {

  description = rs => {
    const descriptions = rs.descriptions;
    if (isEmpty(descriptions)) {
      return "";
    }
    const alternative = I18n.locale === "en" ? "nl" : "en";
    return descriptions[I18n.locale] || descriptions[alternative]
  }

  renderResourceServers = app => {
    if (isEmpty(app.resourceServers)) {
      return null;
    }
    const {resourceServers} = this.props;

    return (
      <div className="mod-attributes">
        <table>
          <thead>
          <tr>
            <th className="clientId">{I18n.t("connected_resource_servers_panel.clientId")}</th>
            <th className="name">{I18n.t("connected_resource_servers_panel.name")}</th>
            <th className="description">{I18n.t("connected_resource_servers_panel.description")}</th>
          </tr>
          </thead>
          <tbody>
          {resourceServers.map((rs, index) => <tr key={index}>
            <td>{rs.spEntityId}</td>
            <td>{rs.names[I18n.locale]}</td>
            <td>{this.description(rs)}</td>
          </tr>)}
          </tbody>
        </table>
      </div>
    );
  };

  render() {
    const {app} = this.props;
    return (
      <div className="l-middle-app-detail">
        <div className="mod-title">
          <h1>{I18n.t("connected_resource_servers_panel.title")}</h1>

          <p>{I18n.t("connected_resource_servers_panel.subtitle", {name: app.name})}</p>
        </div>
        {this.renderResourceServers(app)}
      </div>
    );
  }
}

ConnectedResourceServersPanel.contextTypes = {
  currentUser: PropTypes.object
};

ConnectedResourceServersPanel.propTypes = {
  app: AppShape.isRequired,
  resourceServers: PropTypes.array.isRequired
};

export default ConnectedResourceServersPanel;
