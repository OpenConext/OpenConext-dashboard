/** @jsx React.DOM */

App.Components.LicenseInfoPanel = React.createClass({
  render: function() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("license_info_panel.title")}</h1>
        </div>

        <div className="mod-connection">
          <App.Components.LicenseInfo app={this.props.app} split={false} />
        </div>
        {this.renderLicenseStatus()}
      </div>
    );
  },

  renderLicenseStatus: function() {
    if (this.props.app.hasCrmLink) {
      return this.props.app.license ? this.renderHasLicense() : this.renderNoLicense();
    } else {
      return this.renderUnknownLicense();
    }
  },

  renderHasLicense: function() {
    return (
      <div className="mod-title">
        <h3 dangerouslySetInnerHTML={{ __html: I18n.t("license_info_panel.has_license_html")}} />
      </div>
    );
  },

  renderNoLicense: function() {
    return (
      <div className="mod-title">
        <h3 dangerouslySetInnerHTML={{ __html: I18n.t("license_info_panel.no_license_html")}} />
        <br />
        <div className="mod-description" dangerouslySetInnerHTML={{ __html: I18n.t("license_info_panel.no_license_description_html")}} />
      </div>
    );
  },

  renderUnknownLicense: function() {
    return (
      <div className="mod-title">
        <h3>{I18n.t("license_info_panel.unknown_license")}</h3>
        <br />
        <div className="mod-description" dangerouslySetInnerHTML={{ __html: I18n.t("license_info_panel.unknown_license_description_html")}} />
      </div>
    );
  }
});
