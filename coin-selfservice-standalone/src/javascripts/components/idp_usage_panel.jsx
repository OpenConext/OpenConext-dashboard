/** @jsx React.DOM */

App.Components.IdpUsagePanel = React.createClass({
  render: function () {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("idp_usage_panel.title")}</h1>

          <p>{I18n.t("idp_usage_panel.subtitle", {name: this.props.app.name})}</p>
        </div>
        <div className="mod-used-by">
          {this.renderUsedByInstitutions(this.props.institutions)}
        </div>
      </div>
    );
  },

  renderUsedByInstitutions: function (institutions) {
    return (<table>
      <tbody>
      {institutions.sort(function (l, r) {
        return l.name.localeCompare(r.name);
      }).map(this.renderInstitution)}
      </tbody>
    </table>)
  },

  renderInstitution: function (institution) {
    return (
      <tr>
        <td>{institution.name}</td>
      </tr>
    );
  }

});
