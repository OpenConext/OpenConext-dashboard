import React from "react";
import I18n from "../lib/i18n";

import LicenseInfo from "./license_info";

class LicenseInfoPanel extends React.Component {
  render() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("license_info_panel.title")}</h1>
        </div>

        <div className="mod-connection">
          <LicenseInfo app={this.props.app} split={false} />
        </div>
        {this.renderLicenseStatus()}
      </div>
    );
  }

  renderLicenseStatus() {
    switch (this.props.app.licenseStatus) {
      case "HAS_LICENSE_SURFMARKET":
        return this.renderHasLicense(I18n.t("license_info_panel.has_license_surfmarket_html")) ;
      case "HAS_LICENSE_SP":
        return this.renderHasLicense(I18n.t("license_info_panel.has_license_sp_html", {serviceName: this.props.app.name , serviceUrl: this.props.app.serviceUrl})) ;
      case "NO_LICENSE":
        return this.renderNoLicense();
      case "NOT_NEEDED":
        return this.renderNoLicenseNeeded();
      case "UNKNOWN":
        return this.renderUnknownLicense();
    }
  }

  renderHasLicense(msg) {
    return (
      <div className="mod-title">
        <h3 dangerouslySetInnerHTML={{ __html: msg}} />
      </div>
    );
  }

  renderNoLicense() {
    return (
      <div className="mod-title">
        <h3 dangerouslySetInnerHTML={{ __html: I18n.t("license_info_panel.no_license_html")}} />
        <br />
        <div className="mod-description" dangerouslySetInnerHTML={{ __html: I18n.t("license_info_panel.no_license_description_html")}} />
      </div>
    );
  }

  renderNoLicenseNeeded() {
    return (
      <div className="mod-title">
        <h3 dangerouslySetInnerHTML={{ __html: I18n.t("license_info_panel.not_needed_html")}} />
      </div>
    );
  }

  renderUnknownLicense() {
    return (
      <div className="mod-title">
        <h3>{I18n.t("license_info_panel.unknown_license")}</h3>
        <br />
        <div className="mod-description" dangerouslySetInnerHTML={{ __html: I18n.t("license_info_panel.unknown_license_description_html")}} />
      </div>
    );
  }
}

export default LicenseInfoPanel;
