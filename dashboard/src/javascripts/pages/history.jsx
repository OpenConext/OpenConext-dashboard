import React from "react";

  // mixins: [App.Mixins.SortableTable("history", "requestDate", true)],
class History extends React.Component {

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
            {this.sort(this.props.actions).map(this.renderAction)}
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

export default History;
