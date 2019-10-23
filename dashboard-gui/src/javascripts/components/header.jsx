import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import LanguageSelector from "./language_selector";
import Logout from "../pages/logout";
import {render} from "react-dom";
import {exit, logout} from "../api";
import {Link} from "react-router-dom";
import IDPSelector from "../components/idp_selector";
import isUndefined from "lodash.isundefined";
import stopEvent from "../utils/stop";
import {isEmpty} from "../utils/utils";

class Header extends React.Component {

    constructor() {
        super();
        this.state = {dropDownActive: false};
    }

    render() {
        const {currentUser} = this.context;
        const supportedLanguageCodes = currentUser ? currentUser.supportedLanguages : [];
        const organization = currentUser ? currentUser.organization : "openconext";
        const idp = (currentUser.switchedToIdp || currentUser.getCurrentIdp());
        const state = isEmpty(idp) ? "" : I18n.t(`my_idp.${idp.state}`);
        return (
            <div className="mod-header">
                <h1 className={`title ${organization.toLowerCase()}`}>
                    <Link to="/apps">IdP dashboard</Link>
                </h1>
                {!currentUser.guest && <div className="institute">
                    <p className={`${idp.state}`}>{`${idp.name} - ${state}`}</p>
                </div>}

                <div className="meta">
                    {!currentUser.guest && <div className="name">
                        {this.renderProfileLink()}
                        {this.renderDropDown()}
                    </div>}
                    <LanguageSelector supportedLanguageCodes={supportedLanguageCodes}/>
                    <ul className="links">
                        <li dangerouslySetInnerHTML={{__html: I18n.t("header.links.help_html")}}></li>
                        {currentUser.guest && <li className="login">
                            <a href="/login">Login</a>
                        </li>}
                        {!currentUser.guest && this.renderExitLogout()}
                    </ul>
                </div>
            </div>
        );
    }

    renderProfileLink() {
        const {currentUser} = this.context;
        if (isUndefined(currentUser)) {
            return null;
        }
        return currentUser.superUser ?
            <span>
                <span>{I18n.t("header.welcome")}&nbsp;{currentUser.displayName}</span>
                <Link className="super-user" to={"/users/search"}>
                        {I18n.t("header.super_user_switch")}
                    </Link></span> :

            <span>
          {I18n.t("header.welcome")}&nbsp;
                <a href="/welcome" onClick={this.handleToggle.bind(this)}>
            {currentUser.displayName}
                    {this.renderDropDownIndicator()}
          </a>
        </span>;
    }

    renderDropDownIndicator() {
        if (this.state.dropDownActive) {
            return <i className="fa fa-caret-up"/>;
        }

        return <i className="fa fa-caret-down"/>;
    }

    renderDropDown() {
        const {currentUser} = this.context;
        if (currentUser && !currentUser.superUser && this.state.dropDownActive) {
            return (
                <ul>
                    <h2>{I18n.t("header.you")}</h2>
                    <ul>
                        <li><Link to="/profile" onClick={this.handleClose.bind(this)}>{I18n.t("header.profile")}</Link>
                        </li>
                    </ul>
                    <IDPSelector/>
                </ul>
            );
        }

        return null;
    }

    renderExitLogout() {
        const {currentUser} = this.context;
        if (isUndefined(currentUser)) {
            return null;
        } else if (currentUser.superUser && currentUser.switchedToIdp) {
            return (
                <li><a href="/exit" onClick={this.handleExitClick.bind(this)}>{I18n.t("header.links.exit")}</a></li>
            );
        }

        return (
            <li><a href="/logout" onClick={this.handleLogoutClick.bind(this)}>{I18n.t("header.links.logout")}</a></li>
        );
    }

    handleLogoutClick(e) {
        stopEvent(e);
        logout().then(() => render(<Logout/>, document.getElementById("app")));
    }

    handleExitClick(e) {
        stopEvent(e);
        exit().then(() => window.location = "/");
    }

    handleClose() {
        this.setState({dropDownActive: false});
    }

    handleToggle(e) {
        stopEvent(e);
        this.setState({dropDownActive: !this.state.dropDownActive});
    }
}

Header.contextTypes = {
    currentUser: PropTypes.object
};

export default Header;
