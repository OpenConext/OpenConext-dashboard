import React from "react";
import I18n from "i18n-js";

import { getLicenseContactPerson, getIdpRolesWithUsers } from "../api";

class MyIdp extends React.Component {
  constructor() {
    super();

    this.state = {
      roles: {},
      licenseContactPersons: []
    };
  }

  componentWillMount() {
    const { currentUser } = this.context;
    const idpId = currentUser.getCurrentIdp();

    getIdpRolesWithUsers(idpId).then(data => {
      this.setState({ roles: data.payload });
    });
    getLicenseContactPerson(idpId).then(data => {
      this.setState({ licenseContactPersons: data.payload });
    });
  }

  render() {
    const roles = Object.keys(this.state.roles);
    return (
      <div className="l-mini">
        <div className="mod-idp">
          <h1>{I18n.t("my_idp.title")}</h1>

          <p dangerouslySetInnerHTML={{ __html: I18n.t("my_idp.sub_title_html") }}></p>
          {this.renderRoles(roles)}
          {this.renderLicenseContactPersons(this.state.licenseContactPersons)}
        </div>
      </div>
    );
  }

  renderRoles(roles) {
    return (
      <table>
        <thead>
        <tr>
          <th className="percent_50">{I18n.t("my_idp.role")}</th>
          <th className="percent_50">{I18n.t("my_idp.users")}</th>
        </tr>
        </thead>
        <tbody>
        {roles.map(this.renderRole.bind(this))}
        </tbody>
      </table>
    );
  }

  renderRole(role) {
    const names = this.state.roles[role].map(r => {
      return r.firstName + " " + r.surname;
    }).sort().join(", ");
    const roleName = I18n.t("my_idp")[role];
    return (
      <tr key={role}>
        <td>{roleName}</td>
        <td>{names}</td>
      </tr>
    );
  }

  renderLicenseContactPerson(licenseContactPerson) {
    return (
      <tr>
        <td>{licenseContactPerson.name}</td>
        <td>{licenseContactPerson.email}</td>
        <td>{licenseContactPerson.phone}</td>
      </tr>
    );
  }

  renderLicenseContactPersons(licenseContactPersons) {
    if (licenseContactPersons && licenseContactPersons.length > 0) {
      return (
        <div>
          <p className="next" dangerouslySetInnerHTML={{ __html: I18n.t("my_idp.license_contact_html") }}></p>
          <table>
            <thead>
            <tr>
              <th className="percent_35">{I18n.t("my_idp.license_contact_name")}</th>
              <th className="percent_35">{I18n.t("my_idp.license_contact_email")}</th>
              <th className="percent_35">{I18n.t("my_idp.license_contact_phone")}</th>
            </tr>
            </thead>
            <tbody>
            {licenseContactPersons.map(this.renderLicenseContactPerson.bind(this))}
            </tbody>
          </table>
        </div>
      );
    }

    return null;
  }
}

MyIdp.contextTypes = {
  currentUser: React.PropTypes.object
};

export default MyIdp;
