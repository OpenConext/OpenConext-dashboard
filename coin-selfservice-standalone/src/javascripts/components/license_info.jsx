/** @jsx React.DOM */

App.Components.LicenseInfo = React.createClass({
  getDefaultProps: function() {
    return {
      split: true
    }
  },

  render: function() {
    if (this.props.app.hasCrmLink) {
      return this.props.app.license ? this.renderHasLicense() : this.renderNoLicense();
    } else {
      return this.renderUnknownLicense();
    }
  },

  renderSplitClass: function(classNames) {
    if (this.props.split) {
      return classNames + " split";
    } else {
      return classNames;
    }
  },

  renderHasLicense: function() {
    return (
      <div className={this.renderSplitClass("license yes")}>
        <i className="fa fa-file-text-o"></i>
        <h2>{I18n.t("license_info.has_license")}</h2>
        <p>
          {
            I18n.t("license_info.valid", {
              date: I18n.strftime(new Date(this.props.app.license.endDate), "%-d %B %Y")
            })
          }
        </p>
      </div>
    );
  },

  renderNoLicense: function() {
    if (this.props.onSwitchPanel) {
      var link = <p><a href="#" onClick={this.props.onSwitchPanel("license_info")}>{I18n.t("license_info.license_info")}</a></p>;
    }

    return (
      <div className={this.renderSplitClass("license no")}>
        <i className="fa fa-file-text-o"></i>
        <h2>{I18n.t("license_info.no_license")}</h2>
        {link}
      </div>
    );
  },

  renderUnknownLicense: function() {
    if (this.props.onSwitchPanel) {
      var link = <p><a href="#" onClick={this.props.onSwitchPanel("license_info")}>{I18n.t("license_info.license_unknown_info")}</a></p>;
    }
    return (
      <div className={this.renderSplitClass("license unknown")}>
        <i className="fa fa-file-text-o"></i>
        <h2>{I18n.t("license_info.unknown_license")}</h2>
        {link}
      </div>
    );
  },
});
