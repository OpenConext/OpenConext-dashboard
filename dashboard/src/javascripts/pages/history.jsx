import React from "react";

import I18n from "i18n-js";

import { getActions } from "../api";
import sort from "../utils/sort";
import moment from "moment";

import SortableHeader from "../components/sortable_header";

  // mixins: [App.Mixins.SortableTable("history", "requestDate", true)],
class History extends React.Component {

  constructor() {
    super();
    this.state = {
      actions: [],
      sortAttribute: "requestDate",
      sortAscending: true
    };
  }

  componentWillMount() {
    const { currentUser } = this.context;
    getActions(currentUser.getCurrentIdpId()).then(data => this.setState({ actions: data.payload }));
  }

  handleSort(sortObject) {
    this.setState({
      sortAttribute: sortObject.sortAttribute,
      sortAscending: sortObject.sortAscending
    });
  }

  renderSortableHeader(className, attribute) {
    return (
      <SortableHeader
        sortAttribute={this.state.sortAttribute}
        attribute={attribute}
        sortAscending={this.state.sortAscending}
        localeKey="history"
        className={className}
        onSort={this.handleSort.bind(this)}
        />
    );
  }

  render() {
    return (
      <div className="l-mini">

        <div className="mod-history">
          <h1>{I18n.t("history.title")}</h1>

          <table>
            <thead>
              <tr>
                {this.renderSortableHeader("percent_15", "requestDate")}
                {this.renderSortableHeader("percent_15", "userName")}
                {this.renderSortableHeader("percent_25", "type")}
                {this.renderSortableHeader("percent_20", "jiraKey")}
                {this.renderSortableHeader("percent_25", "status")}
              </tr>
            </thead>
            <tbody>
            { sort(this.state.actions).map(this.renderAction.bind(this)) }
            </tbody>
          </table>
        </div>
      </div>
    );
  }

  renderAction(action) {
    return (
      <tr key={action.jiraKey}>
        <td className="percent_15">{moment(action.requestDate).format("DD-MM-YYYY")}</td>
        <td className="percent_15">{action.userName}</td>
        <td className="percent_25">{I18n.t("history.action_types." + action.type, {serviceName: action.spName})}</td>
        <td className="percent_20">{action.jiraKey}</td>
        <td className="percent_25">{action.status}</td>
      </tr>
    );
  }

  convertRequestDateForSort(value) {
    return Date.parse(value);
  }
}

History.contextTypes = {
  currentUser: React.PropTypes.object
};

export default History;
