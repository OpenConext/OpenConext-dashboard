import React from "react";

import I18n from "i18n-js";

import { switchToIdp } from "../api";

class IDPSelector extends React.Component {
  constructor() {
    super();

    this.state = {
      activeIdp: null
    };
  }

  componentWillMount() {
    const { currentUser } = this.context;
    this.setState({
      activeIdp: (currentUser.switchedToIdp || currentUser.getCurrentIdp()).id
    });
  }

  render() {
    const { currentUser } = this.context;

    if (currentUser.institutionIdps.length > 0) {
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
    const { currentUser } = this.context;

    return (
      <ul>
        {currentUser.institutionIdps.map(this.renderItem.bind(this))}
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
      switchToIdp(idp.id, null).then(() => {
        location.reload();
      });
    }.bind(this);
  }
}

IDPSelector.contextTypes = {
  currentUser: React.PropTypes.object
};

export default IDPSelector;
