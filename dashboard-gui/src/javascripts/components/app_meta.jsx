import React from "react";
import I18n from "i18n-js";

import {AppShape} from "../shapes";
import Contact from "./contact";

export default class AppMeta extends React.Component {

    renderRegistrationInfo(url) {
        if (!url) {
            return null;
        }

        return (
            <div className="contact">
                <address>
                    <span dangerouslySetInnerHTML={{__html: I18n.t("app_meta.registration_info_html", {url})}}/>
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
                    <img src={this.props.app.detailLogoUrl} alt={this.props.app.name}/>
                </div>
            );
        }
        return null;
    }

    render() {
        const app = this.props.app;
        return (
            <div className="l-left-app-meta">
                <div className="mod-app-meta">
                    <div className="name">
                        {app.name}
                    </div>
                    {this.renderLogo()}
                    <Contact email={app.supportMail}/>
                    {this.renderUrl("support", app.supportUrl)}
                    {this.renderUrl("login", app.appUrl)}
                    {this.renderUrl("website", app.websiteUrl)}
                    {this.renderUrl("eula", app.eulaUrl)}
                    {this.renderUrl("registration_policy", app.registrationPolicyUrl)}
                    {this.renderUrl("privacy_statement", app.privacyStatementUrl)}
                    {this.renderRegistrationInfo(app.registrationInfoUrl)}
                </div>
            </div>
        );
    }


}

AppMeta.propTypes = {
    app: AppShape.isRequired
};
