import React from "react";
import I18n from "i18n-js";

import { getIdpsForSuper, switchToIdp } from "../api";

class SearchUser extends React.Component {
  constructor() {
    super();

    this.state = {
      search: "",
      idps: [],
      roles: []
    };
  }

  componentWillMount() {
    getIdpsForSuper().then(json => this.setState({idps: json.idps, roles: json.roles}));
  }

  render() {
    return (
      <div className="l-mini">
        <div className="mod-super-user">
          <h1>{I18n.t("search_user.switch_identity")}</h1>
          <div className="mod-super-user-search">
            <form>
              <fieldset>
                <i className="fa fa-search"/>
                <input
                  type="search"
                  value={this.state.search}
                  onChange={(e) => this.setState({search: e.target.value})}
                  placeholder={I18n.t("search_user.search_hint")} />
                <button type="submit">{I18n.t("search_user.search")}</button>
              </fieldset>
            </form>
          </div>
          <table>
            <thead>
              <tr>
                <th>{I18n.t("search_user.name")}</th>
                <th className="center percent_25">{I18n.t("search_user.switch_to")}</th>
              </tr>
            </thead>
            <tbody>
              {this.filteredIdps().map(this.renderItem.bind(this))}
            </tbody>
          </table>
        </div>
      </div>
    );
  }

  renderItem(idp) {
    return (
      <tr key={idp.name}>
        <td>{idp.name}</td>
        <td className="center">
          {
            this.state.roles.map(function(role) {
              return this.renderSwitchToRole(idp, role);
            }.bind(this))
          }
        </td>
      </tr>
    );
  }

  renderSwitchToRole(idp, role) {
    return (
      <a key={role} href="#" className="c-button" onClick={this.handleSwitchToUser(idp, role)}>
        {I18n.t("search_user.switch." + role.toLowerCase())}
      </a>
    );
  }

  handleSwitchToUser(idp, role) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      switchToIdp(idp.id, role).then(() => {
        window.location = "/";
      });
    };
  }

  filteredIdps() {
    return this.state.idps.filter(this.filterBySearchQuery.bind(this));
  }

  filterBySearchQuery(idp) {
    return idp.name.toLowerCase().indexOf(this.state.search.toLowerCase()) >= 0;
  }
}

export default SearchUser;
