import React from "react";
import I18n from "i18n-js";

import { AppShape } from "../shapes";
import Contact from "./contact";

class AppMeta extends React.Component {
  render() {
    const app = this.props.app;
    return (
      <div className="l-right">
        <div className="mod-app-meta">
          {this.renderLogo()}

          <div className="contact">
            <h2>{I18n.t("app_meta.question")}</h2>
            <address>
              <a href={"mailto:support@surfconext.nl?subject=Question about " + app.name}>support@surfconext.nl</a>
            </address>
          </div>

          <Contact email={app.supportMail} />
          {this.renderUrl("support", app.supportUrl)}
          {this.renderUrl("login", app.appUrl)}
          {this.renderUrl("website", app.websiteUrl)}
          {this.renderUrl("eula", app.eulaUrl)}
          {this.renderUrl("registration_policy", app.registrationPolicyUrl)}
          {this.renderUrl("privacy_statement", app.privacyStatementUrl)}
          {this.renderRegistrationInfo(app.registrationInfoUrl)}
          {this.renderMetadataLink(app.spEntityId)}
        </div>
      </div>
    );
  }

  renderRegistrationInfo(url) {
    if (!url) {
      return null;
    }

    return (
      <div className="contact">
        <address>
          <span dangerouslySetInnerHTML={{ __html: I18n.t("app_meta.registration_info_html", { url }) }}/>
        </address>
      </div>
    );
  }

  renderUrl(key, link, target) {
    if (link) {
      const linkTarget = target ? target : "_blank";
      return (
        <div className="contact">
          <address>
            <a href={link} target={linkTarget}>{I18n.t("app_meta." + key)}</a>
          </address>
        </div>
      );
    }
    return null;
  }

  renderLogo() {
    if (this.props.app.detailLogoUrl) {
      return (
        <div className='logo'>
          <img src={this.props.app.detailLogoUrl} alt={this.props.app.name} />
        </div>
      );
    }
    return null;
  }

  renderMetadataLink(spEntityId) {
    const env = window.location.href.indexOf(".acc.") > 0 ? ".acc." : ".";
    return (
      <div className="contact">
        <a target="_blank" href={`https://engine${env}surfconext.nl/authentication/proxy/idps-metadata?sp-entity-id=${encodeURIComponent(spEntityId)}`}>
          {I18n.t("app_meta.metadata_link")}
        </a>
      </div>
    );
  }
}

AppMeta.propTypes = {
  app: AppShape.isRequired
};

export default AppMeta;
