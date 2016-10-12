import React from "react";
import I18n from "i18n-js";
import Link from "react-router/Link";

import LicenseInfo from "./license_info";
import Screenshots from "./screenshots";

import { AppShape } from "../shapes";

class OverviewPanel extends React.Component {
  render() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{this.props.app.name}</h1>
        </div>

        <div className="mod-connection">
          {this.renderConnection()}
          <LicenseInfo app={this.props.app} showLinks />
        </div>

        {this.renderWikiUrl()}

        <div className="mod-description">
          <h2>{I18n.t("overview_panel.description")}</h2>
          {this.renderDescription()}
        </div>

        {this.renderNormenKader()}

        {this.renderSingleTenantService()}

        <Screenshots screenshotUrls={this.props.app.screenshotUrls}/>
      </div>
    );
  }

  renderWikiUrl() {
    if (this.props.app.wikiUrl) {
      return (
        <div className="mod-title">
          <h3
            dangerouslySetInnerHTML={{ __html: I18n.t("overview_panel.wiki_info_html", { link: this.props.app.wikiUrl }) }}/>
        </div>
      );
    }

    return null;
  }

  renderNormenKader() {
    const html = (this.props.app.normenkaderPresent && this.props.app.normenkaderUrl) ?
      I18n.t("overview_panel.normen_kader_html", { name: this.props.app.name, link: this.props.app.normenkaderUrl }) :
      I18n.t("overview_panel.no_normen_kader_html", { name: this.props.app.name });
    return (
      <div className="mod-description">
        <h2>{I18n.t("overview_panel.normen_kader")}</h2>
        <h3
          dangerouslySetInnerHTML={{ __html: html }}/>
      </div>);
  }

  renderSingleTenantService() {
    if (this.props.app.exampleSingleTenant) {
      return (
        <div className="mod-description">
        <h2>{I18n.t("overview_panel.single_tenant_service")}</h2>
        <h3
          dangerouslySetInnerHTML={{ __html: I18n.t("overview_panel.single_tenant_service_html", { name: this.props.app.name }) }}/>
      </div>);
    }

    return null;
  }

  renderDescription() {
    const hasText = function(value) {
      return value && value.trim().length > 0;
    };
    if (hasText(this.props.app.enduserDescription)) {
      return <p dangerouslySetInnerHTML={{ __html: this.props.app.enduserDescription }}/>;
    } else if (hasText(this.props.app.institutionDescription)) {
      return <p dangerouslySetInnerHTML={{ __html: this.props.app.institutionDescription }}/>;
    } else if (hasText(this.props.app.description)) {
      return <p dangerouslySetInnerHTML={{ __html: this.props.app.description }}/>;
    }

    return <p>{I18n.t("overview_panel.no_description")}</p>;
  }

  renderConnection() {
    return this.props.app.connected ? this.renderHasConnection() : this.renderNoConnection();
  }

  renderHasConnection() {
    const { currentUser } = this.context;

    let disconnect = null;
    if (currentUser.dashboardAdmin) {
      disconnect = <p>
        <Link to={`/apps/${this.props.app.id}/how_to_connect`}>{I18n.t("overview_panel.disconnect")}</Link>
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
    const { currentUser } = this.context;

    let connect = null;
    if (currentUser.dashboardAdmin) {
      connect = <p>
        <Link to={`/apps/${this.props.app.id}/how_to_connect`}>{I18n.t("overview_panel.how_to_connect")}</Link>
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
}

OverviewPanel.contextTypes = {
  currentUser: React.PropTypes.object
};

OverviewPanel.propTypes = {
  app: AppShape.isRequired
};

export default OverviewPanel;
