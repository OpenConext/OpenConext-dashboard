import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import {Link} from "react-router-dom";
import moment from "moment";

import LicenseInfo from "./license_info";
import Screenshots from "./screenshots";

import {AppShape} from "../shapes";
import {privacyProperties} from "../utils/privacy";

class OverviewPanel extends React.Component {

    renderStrongAuthentication() {
        return (
            <div className={`strong-authentication ${this.props.app.strongAuthentication ? "yes" : "no-needed"} split`}>
                {this.renderStrongAuthenticationContent()}
            </div>
        );
    }

    renderStrongAuthenticationContent() {
        const {currentUser} = this.context;
        const {app} = this.props;
        const idpLoaLevelConfigured = (currentUser.getCurrentIdp().stepupEntities || []).some(e => e.name === app.spEntityId);
        if (app.strongAuthentication || idpLoaLevelConfigured) {
            return <div><i className="fa fa-lock"></i> <h2>{I18n.t("overview_panel.supports_ssa")}</h2></div>;
        }

        return null;
    }

    renderPublishInEdugainDate() {
        if (this.props.app.publishInEdugainDate) {
            return (
                <div>
                    {I18n.t("overview_panel.publish_in_edugain_date")} {moment(this.props.app.publishInEdugainDate).format("LL")}
                </div>
            );
        }
        return null;
    }

    renderInterfedSource() {
        if (this.props.app.interfedSource !== "SURFconext") {
            return (
                <div className="mod-interfed">
                    <h3>{I18n.t("overview_panel.interfed_source")} {this.props.app.interfedSource}</h3>
                    {this.renderPublishInEdugainDate()}
                </div>
            );
        }
        return null;
    }

    renderEntityCategory(field) {
        const entityCategory = this.props.app[field];
        if (entityCategory) {
            return (
                <li>
                    <a href={entityCategory}
                       target="_blank"
                       rel="noopener noreferrer">{I18n.t(`overview_panel.entity_category.${entityCategory.replace(/\./g, "")}`)}</a>
                </li>
            );
        }

        return null;
    }

    renderEntityCategories() {
        const {app} = this.props;

        if (app.entityCategories1 || app.entityCategories2) {
            return (
                <div className="mod-description">
                    <h2 key="title">{I18n.t("overview_panel.entity_categories")}</h2>
                    <ul key="list">
                        {this.renderEntityCategory("entityCategories1")}
                        {this.renderEntityCategory("entityCategories2")}
                    </ul>
                </div>
            );
        }

        return null;
    }

    renderWikiUrl() {
        if (this.props.app.wikiUrl) {
            return (
                <div className="mod-title">
                    <h3
                        dangerouslySetInnerHTML={{__html: I18n.t("overview_panel.wiki_info_html", {link: this.props.app.wikiUrl})}}/>
                </div>
            );
        }

        return null;
    }

    renderAansluitOvereenkomstRefused() {
        const shown = this.props.app.aansluitovereenkomstRefused;
        if (!shown) {
            return null;
        }
        return (
            <div className="mod-description">
                <h2>{I18n.t("overview_panel.aansluitovereenkomst")}</h2>
                <p dangerouslySetInnerHTML={{__html: I18n.t("overview_panel.aansluitovereenkomstRefused")}}/>
            </div>);
    }

    renderPrivacyInformation() {
        const hasPrivacyInfo = privacyProperties.some(prop => this.props.app.privacyInfo[prop]);
        if (hasPrivacyInfo) {
            return null;
        }
        return <div className="mod-description">
            <h2>{I18n.t("overview_panel.privacyInformation")}</h2>
            <p dangerouslySetInnerHTML={{__html: I18n.t("overview_panel.privacyInformationInfo")}}/>
        </div>;
    }

    renderSingleTenantService() {
        if (this.props.app.entityType === "single_tenant_template") {
            return (
                <div className="mod-description">
                    <h2>{I18n.t("overview_panel.single_tenant_service")}</h2>
                    <h3
                        dangerouslySetInnerHTML={{__html: I18n.t("overview_panel.single_tenant_service_html", {name: this.props.app.name})}}/>
                </div>);
        }
        return null;
    }

    renderSURFSecureId = () => {
        const {currentUser} = this.context;
        const {app} = this.props;
        const idpLoaLevelConfigured = (currentUser.getCurrentIdp().stepupEntities || []).some(e => e.name === app.spEntityId);
        const msg = idpLoaLevelConfigured ? "minimalLoaLevelIdp" : "minimalLoaLevel";
        const loa = idpLoaLevelConfigured ? currentUser.getCurrentIdp().stepupEntities.find(e => e.name === app.spEntityId).level : app.minimalLoaLevel;
        if (app.strongAuthentication || idpLoaLevelConfigured) {
            return (
                <div className="mod-description">
                    <h2>{I18n.t("overview_panel.supports_ssa")}</h2>
                    <p dangerouslySetInnerHTML={{__html: I18n.t(`overview_panel.${msg}`,
                            {minimalLoaLevel: loa.substring(loa.lastIndexOf("/") + 1)})}}/>
                </div>);
        }
        return null;
    };

    renderContractualBase = () => {
        if (this.props.app.contractualBase) {
            return (
                <div className="mod-description">
                    <h2>{I18n.t("apps.overview.contractualBase")}</h2>
                    <h3>{I18n.t(`overview_panel.contractualBase.${this.props.app.contractualBase.toLowerCase()}`)}</h3>
                </div>);
        }
        return null;
    };


    renderDescription() {
        const hasText = function (value) {
            return value && value.trim().length > 0;
        };
        if (hasText(this.props.app.enduserDescription)) {
            return <p dangerouslySetInnerHTML={{__html: this.props.app.enduserDescription}}/>;
        } else if (hasText(this.props.app.institutionDescription)) {
            return <p dangerouslySetInnerHTML={{__html: this.props.app.institutionDescription}}/>;
        } else if (hasText(this.props.app.description)) {
            return <p dangerouslySetInnerHTML={{__html: this.props.app.description}}/>;
        }

        return <p>{I18n.t("overview_panel.no_description")}</p>;
    }

    renderConnection() {
        return this.props.app.connected ? this.renderHasConnection() : this.renderNoConnection();
    }

    renderHasConnection() {
        const {currentUser} = this.context;
        const {app, conflictingJiraIssue} = this.props;
        let disconnect = null;
        if (currentUser.dashboardAdmin && !conflictingJiraIssue) {
            disconnect = <p>
                <Link
                    to={`/apps/${app.id}/${app.entityType}/how_to_connect`}>{I18n.t("overview_panel.disconnect")}</Link>
            </p>;
        }

        return (
            <div className="technical yes split">
                <i className="fa fa-chain"/>

                <h2>{I18n.t("overview_panel.has_connection")}</h2>
                {disconnect}
            </div>
        );
    }

    renderNoConnection() {
        const {currentUser} = this.context;
        const {app, conflictingJiraIssue} = this.props;
        let connect = null;
        if (currentUser.dashboardAdmin && !conflictingJiraIssue) {
            connect = <p>
                <Link
                    to={`/apps/${app.id}/${app.entityType}/how_to_connect`}>{I18n.t("overview_panel.how_to_connect")}</Link>
            </p>;
        }

        return (
            <div className="technical unknown split">
                <i className="fa fa-chain-broken"/>

                <h2>{I18n.t("overview_panel.no_connection")}</h2>
                {connect}
            </div>
        );
    }

    render() {
        let connectionClass = "mod-connection";
        if (this.props.app.strongAuthentication) {
            connectionClass += " ssa";
        }
        const {currentUser} = this.context;
        return (
            <div className="l-middle-app-detail">
                <div className="mod-title">
                    <h1>{this.props.app.name}</h1>
                    <p className="sub-title">Entity ID: {this.props.app.spEntityId}</p>
                </div>

                <div className={connectionClass}>
                    {!currentUser.guest && this.renderConnection()}
                    <LicenseInfo app={this.props.app} showLinks/>
                    {this.renderStrongAuthentication()}
                </div>

                {this.renderInterfedSource()}

                {this.renderWikiUrl()}

                <div className="mod-description">
                    <h2>{I18n.t("overview_panel.description")}</h2>
                    {this.renderDescription()}
                </div>

                {this.renderSURFSecureId()}

                {this.renderContractualBase()}

                {this.renderSingleTenantService()}

                {this.renderEntityCategories()}

                {this.renderAansluitOvereenkomstRefused()}

                {this.renderPrivacyInformation()}

                <Screenshots screenshotUrls={this.props.app.screenshotUrls}/>
            </div>
        );
    }

}

OverviewPanel.contextTypes = {
    currentUser: PropTypes.object
};

OverviewPanel.propTypes = {
    app: AppShape.isRequired,
    conflictingJiraIssue: PropTypes.object,
};

export default OverviewPanel;
