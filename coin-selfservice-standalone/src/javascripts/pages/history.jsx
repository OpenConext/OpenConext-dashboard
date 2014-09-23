/** @jsx React.DOM */

App.Pages.History = React.createClass({
  render: function() {
    var actions = this.props.actions;
    return (
      <div className="l-mini">

        <div className="mod-history">
          <h1>{I18n.t("history.title")}</h1>

          <table>
            <thead>
              <tr>
                <th className="percent_15">{I18n.t("history.date")}</th>
                <th className="percent_25">{I18n.t("history.type")}</th>
                <th className="percent_15">{I18n.t("history.ticket")}</th>
                <th className="percent_25">{I18n.t("history.status")}</th>
                <th className="percent_20">{I18n.t("history.by")}</th>
              </tr>
            </thead>
            <tbody>
            {actions.map(this.renderAction)}
            </tbody>
          </table>

        </div>
      </div>
      );
  },
  renderAction: function(action) {
    return (
      <tr key={action.id}>
        <td>{new Date(Date.parse(action.requestDate)).format("dd-MM-yyyy")}</td>
        <td>{I18n.t("history.action_types." + action.type, {serviceName: action.spName})}</td>
        <td>{action.jiraKey}</td>
        <td>{I18n.t("history.statusses." + action.status)}</td>
        <td>{action.userName}</td>
      </tr>
      );
  }

});
