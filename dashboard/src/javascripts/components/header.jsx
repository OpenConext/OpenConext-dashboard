import React from "react";
import I18n from "i18n-js";
import LanguageSelector from "./language_selector";
import Logout from "../pages/logout";
import { render } from "react-dom";
import { exit, logout } from "../api";
import Link from "react-router/Link";
import IDPSelector from "../components/idp_selector";

class Header extends React.Component {
  constructor() {
    super();

    this.state = {
      dropDownActive: false
    };
  }

  render() {
    return (
      <div className="mod-header">
        <h1 className="title"><Link to="/apps">{I18n.t("header.title")}</Link></h1>
        <div className="meta">
          <div className="name">
            {this.renderProfileLink()}
            {this.renderDropDown()}
          </div>
          <LanguageSelector />
          <ul className="links">
            <li dangerouslySetInnerHTML={{__html: I18n.t("header.links.help_html") }}></li>
            {this.renderExitLogout()}
          </ul>
        </div>
      </div>
    );
  }

  renderProfileLink() {
    const { currentUser } = this.context;
    if (_.isUndefined(currentUser)) {
      return null;
    } else if (!currentUser.superUser) {
      return (
        <span>
          {I18n.t("header.welcome")}&nbsp;
          <a href="#" onClick={this.handleToggle.bind(this)}>
            {currentUser.displayName}
            {this.renderDropDownIndicator()}
          </a>
        </span>
      );
    } else {
      return (
        <span>
          {I18n.t("header.welcome")}&nbsp;{currentUser.displayName}
        </span>
      );
    }
  }

  renderDropDownIndicator() {
    if (this.state.dropDownActive) {
      return <i className="fa fa-caret-up" />;
    } else {
      return <i className="fa fa-caret-down" />;
    }
  }

  renderDropDown() {
    const { currentUser } = this.context;
    if (currentUser && !currentUser.superUser && this.state.dropDownActive) {
      return (
        <ul>
          <h2>{I18n.t("header.you")}</h2>
          <ul>
            <li><Link to="/profile" onClick={this.handleClose.bind(this)}>{I18n.t("header.profile")}</Link></li>
          </ul>
          <IDPSelector />
        </ul>
      );
    }

    return null;
  }

  renderExitLogout() {
    const { currentUser } = this.context;
    if (_.isUndefined(currentUser)) {
      return null;
    } else if (currentUser.superUser && currentUser.switchedToIdp) {
      return (
        <li><a href="#" onClick={this.handleExitClick.bind(this)}>{I18n.t("header.links.exit")}</a></li>
      );
    } else {
      return (
        <li><a href="#" onClick={this.handleLogoutClick.bind(this)}>{I18n.t("header.links.logout")}</a></li>
      );
    }
  }

  handleLogoutClick(e) {
    e.preventDefault();
    logout().then(() => render(<Logout />, document.getElementById("app")));
  }

  handleExitClick(e) {
    e.preventDefault();
    exit().then(() => window.location = "/");
  }

  handleClose() {
    this.setState({dropDownActive: false});
  }

  handleToggle(e) {
    e.preventDefault();
    e.stopPropagation();
    this.setState({dropDownActive: !this.state.dropDownActive});
  }
}

Header.contextTypes = {
  currentUser: React.PropTypes.object
};

export default Header;
