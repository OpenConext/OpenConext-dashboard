import React from "react";
import I18n from "i18n-js";

import { AppShape } from "../shapes";

class IdpUsagePanel extends React.Component {
  render() {
    const subtitle = this.props.institutions.length === 0 ? I18n.t("idp_usage_panel.subtitle_none", { name: this.props.app.name }) : I18n.t("idp_usage_panel.subtitle", { name: this.props.app.name });
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
      {institutions.sort((l, r) => {
        return l.name.localeCompare(r.name);
      }).map(this.renderInstitution)}
      </tbody>
    </table>);
  }

  renderInstitution(institution) {
    const locale = I18n.locale;
    const nameEn = institution.name && institution.name.trim().length > 0 ? institution.name : institution.nameNl;
    const nameNl = institution.nameNl && institution.nameNl.trim().length > 0 ? institution.nameNl : institution.name;
    const value = locale === "en" ? nameEn : nameNl;
    return (
      <tr key={institution.id}>
        <td >{value}</td>
      </tr>
    );
  }

}

IdpUsagePanel.propTypes = {
  app: AppShape.isRequired,
  institutions: React.PropTypes.arrayOf(React.PropTypes.shape({
    name: React.PropTypes.string,
    nameNl: React.PropTypes.string,
    displayName: React.PropTypes.string,
    id: React.PropTypes.string.isRequired
  })).isRequired
};

export default IdpUsagePanel;
