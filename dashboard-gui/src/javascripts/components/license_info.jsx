import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import {Link} from "react-router-dom";
import {AppShape} from "../shapes";

class LicenseInfo extends React.Component {
    render() {
        switch (this.props.app.licenseStatus) {
            case "HAS_LICENSE_SURFMARKET":
            case "HAS_LICENSE_SP":
                return this.renderHasLicense();
            case "NO_LICENSE":
                return this.renderNoLicense();
            case "NOT_NEEDED":
                return this.renderNoLicenseNeeded();
            case "UNKNOWN":
                return this.renderUnknownLicense();
            default:
                return null;
        }
    }

    renderSplitClass(classNames) {
        if (this.props.split) {
            return classNames + " split";
        }

        return classNames;
    }

    renderHasLicense() {
        const licenseStatus = this.props.app.licenseStatus === "HAS_LICENSE_SURFMARKET" ? I18n.t("license_info.has_license_surfmarket") : I18n.t("license_info.has_license_sp");
        const license = this.props.app.license;
        const licenseInfo = (license && license.endDate) ? I18n.t("license_info.valid", {date: I18n.strftime(new Date(license.endDate), "%-d %B %Y")}) : "";
        return (
            <div className={this.renderSplitClass("license yes")}>
                <i className="fa fa-file-text-o"></i>
                <h2>{licenseStatus}</h2>
                <p>{licenseInfo}</p>
            </div>
        );
    }

    renderNoLicense() {
        let link;
        const {app} = this.props;
        if (this.props.showLinks) {
            link = (
                <p>
                    <Link
                        to={`/apps/${app.id}/${app.entityType}/license_info`}>{I18n.t("license_info.license_info")}</Link>
                </p>
            );
        }
        return (
            <div className={this.renderSplitClass("license no")}>
                <i className="fa fa-file-text-o"></i>
                <h2>{I18n.t("license_info.no_license")}</h2>
                {link}
            </div>
        );
    }

    renderNoLicenseNeeded() {
        return (
            <div className={this.renderSplitClass("license no-needed")}>
                <i className="fa fa-file-text-o"></i>
                <h2>{I18n.t("license_info.no_license_needed")}</h2>
            </div>
        );
    }

    renderUnknownLicense() {
        let link;
        const {app} = this.props;
        if (this.props.showLinks) {
            link = (
                <p>
                    <Link
                        to={`/apps/${app.id}/${app.entityType}/license_info`}>{I18n.t("license_info.license_unknown_info")}</Link>
                </p>
            );
        }

        return (
            <div className={this.renderSplitClass("license unknown")}>
                <i className="fa fa-file-text-o"></i>
                <h2>{I18n.t("license_info.unknown_license")}</h2>
                {link}
            </div>
        );
    }
}

LicenseInfo.defaultProps = {
    showLinks: false,
    split: true
};

LicenseInfo.propTypes = {
    app: AppShape.isRequired,
    showLinks: PropTypes.bool,
    split: PropTypes.bool
};

export default LicenseInfo;
