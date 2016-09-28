import React from "react";
import I18n from "i18n-js";

import { AppShape } from "../shapes";
import Contact from "./contact";

class AppMeta extends React.Component {
  render() {
    return (
      <div className="l-right">
        <div className="mod-app-meta">
          {this.renderLogo()}

          <div className="contact">
            <h2>{I18n.t("app_meta.question")}</h2>
            <address>
              <a href={"mailto:support@surfconext.nl?subject=Question about " + this.props.app.name}>support@surfconext.nl</a>
            </address>
          </div>

          <Contact email={this.props.app.supportMail} />
          {this.renderUrl("support", this.props.app.supportUrl)}
          {this.renderUrl("login", this.props.app.appUrl)}
          {this.renderUrl("website", this.props.app.websiteUrl)}
          {this.renderUrl("eula", this.props.app.eulaUrl)}
        </div>
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
}

AppMeta.propTypes = {
  app: AppShape.isRequired
};

export default AppMeta;
