import React from "react";

class IDPSelector extends React.Component {
  constructor() {
    super();

    this.state = {
      activeIdp: (App.currentUser.switchedToIdp || App.currentUser.currentIdp).id
    }
  }

  render() {
    if (App.currentUser.institutionIdps.length > 0) {
      return (
        <li className="select-idp">
          <h2>{I18n.t("header.switch_idp")}</h2>
          {this.renderMenu()}
        </li>
      );
    } else {
      return null;
    }
  }

  renderMenu() {
    return (
      <ul>
        {App.currentUser.institutionIdps.map(this.renderItem)}
      </ul>
    );
  }

  renderItem(idp) {
    return (
      <li key={idp.id}>
        <a href="#" onClick={this.handleChooseIdp(idp)}>
          {this.renderActiveIndicator(idp)}
          {idp.name}
        </a>
      </li>
    );
  }

  renderActiveIndicator(idp) {
    if (this.state.activeIdp == idp.id) {
      return (
        <i className="fa fa-caret-right" />
      );
    } else {
      return "";
    }
  }

  handleChooseIdp(idp) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      App.Controllers.User.switchToIdp(idp, null, function() {
        this.setState({ activeIdp: idp.id });
        page.replace(window.history.state.path);
      }.bind(this));
    }.bind(this)
  }
}

export default IDPSelector;
