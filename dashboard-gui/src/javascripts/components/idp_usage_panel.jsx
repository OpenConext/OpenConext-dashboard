import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";

import {AppShape} from "../shapes";

class IdpUsagePanel extends React.Component {
    render() {
        const { exampleSingleTenant, name, state } = this.props.app;

        const institutions = this.props.institutions.filter(inst => inst.state === state);

        const subtitle = exampleSingleTenant ?
            I18n.t("idp_usage_panel.subtitle_single_tenant", {name}) :
            institutions.length === 0 ?
                I18n.t("idp_usage_panel.subtitle_none", {name}) :
                I18n.t("idp_usage_panel.subtitle", {name});

        return (
            <div className="l-middle-app-detail">
                <div className="mod-title">
                    <h1>{I18n.t("idp_usage_panel.title")}</h1>
                    <p>{subtitle}</p>
                </div>
                {!exampleSingleTenant && <div className="mod-used-by">
                    {this.renderUsedByInstitutions(institutions)}
                </div>}
            </div>
        );
    }

    renderUsedByInstitutions = (institutions) => (
      <table>
          <tbody>
            {
              institutions
                .sort((l, r) => l.name.localeCompare(r.name))
                .map(this.renderInstitution)
            }
          </tbody>
      </table>
    );


    renderInstitution(institution) {
        const nameEn = institution.name && institution.name.trim().length > 0 ? institution.name : institution.nameNl;
        const nameNl = institution.nameNl && institution.nameNl.trim().length > 0 ? institution.nameNl : institution.name;

        return (
            <tr key={institution.id}>
                <td>{I18n.locale === "en" ? nameEn : nameNl}</td>
            </tr>
        );
    }

}

IdpUsagePanel.propTypes = {
    app: AppShape.isRequired,
    institutions: PropTypes.arrayOf(PropTypes.shape({
        name: PropTypes.string,
        nameNl: PropTypes.string,
        displayName: PropTypes.string,
        id: PropTypes.string.isRequired,
        state: PropTypes.string,
    })).isRequired
};

export default IdpUsagePanel;
