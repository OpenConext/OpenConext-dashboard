import React from "react";
import I18n from "i18n-js";
import PropTypes from "prop-types";
import stopEvent from "../utils/stop";

class NotFound extends React.Component {

    login = e => {
        stopEvent(e);
        window.location.href = `/login?redirect_url=${encodeURIComponent(window.location.href)}`;
    };

    render() {
        const {currentUser} = this.context;
        return (
            <div className="mod-not-found">
                <h1>{I18n.t("not_found.title")}</h1>
                <h2 className="sub-title">{I18n.t("not_found.subTitle")}</h2>
                <ul>
                    {currentUser.guest && <li>
                        <span>{I18n.t("not_found.reasonLoginPre")}</span>
                        <a href="/login" onClick={this.login}>Login</a>
                        <span>{I18n.t("not_found.reasonLoginPost")}</span>
                    </li>
                    }
                    <li dangerouslySetInnerHTML={{__html: I18n.t("not_found.reasonHelp")}}></li>
                    <li dangerouslySetInnerHTML={{__html: I18n.t("not_found.reasonRemoved")}}></li>
                    <li dangerouslySetInnerHTML={{__html: I18n.t("not_found.reasonUnknown")}}></li>
                </ul>
            </div>
        );
    }
}

NotFound.contextTypes = {
    currentUser: PropTypes.object
};
export default NotFound;
