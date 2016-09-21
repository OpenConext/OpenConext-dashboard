import React from "react";
import I18n from "../lib/i18n";
import _ from "lodash";
import LanguageSelector from "./language_selector";

class Header extends React.Component {
  constructor() {
    super();

    this.state = {
      dropDownActive: false
    }
  }

  render() {
    return (
      <div className="mod-header">
        <h1 className="title"><a href="/">{I18n.t("header.title")}</a></h1>
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
      return;
    } else if (!currentUser.superUser) {
      return (
        <span>
          {I18n.t("header.welcome")}&nbsp;
          <a href="#" onClick={this.handleToggle}>
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
            <li><a href="/profile" onClick={this.handleClose}>{I18n.t("header.profile")}</a></li>
          </ul>
          <App.Components.IDPSelector />
        </ul>
      );
    }
  }

  renderExitLogout() {
    const { currentUser } = this.context;
    if (_.isUndefined(currentUser)) {
      return;
    } else if (currentUser.superUser && currentUser.switchedToIdp) {
      return (
        <li><a href="/exit">{I18n.t("header.links.exit")}</a></li>
      );
    } else {
      return (
        <li><a href="/logout">{I18n.t("header.links.logout")}</a></li>
      );
    }
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
