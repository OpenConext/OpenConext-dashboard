import React from "react";
import I18n from "i18n-js";

class IdpUsagePanel extends React.Component {
  render() {
    var subtitle = this.props.institutions.length === 0 ? I18n.t("idp_usage_panel.subtitle_none", {name: this.props.app.name}) : I18n.t("idp_usage_panel.subtitle", {name: this.props.app.name});
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("idp_usage_panel.title")}</h1>

          <p>{subtitle}</p>
        </div>
        <div className="mod-used-by">
          {this.renderUsedByInstitutions(this.props.institutions)}
        </div>
      </div>
    );
  }

  renderUsedByInstitutions(institutions) {
    return (<table>
      <tbody>
      {institutions.sort(function (l, r) {
        return l.name.localeCompare(r.name);
      }).map(this.renderInstitution)}
      </tbody>
    </table>)
  }

  renderInstitution(institution) {
    var value = (institution.displayName && institution.displayName.trim().length > 0) ? institution.displayName : institution.name;
    return (
      <tr key={institution.id}>
        <td >{value}</td>
      </tr>
    );
  }

}

export default IdpUsagePanel;
