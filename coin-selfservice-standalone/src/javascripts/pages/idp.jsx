/** @jsx React.DOM */

App.Pages.MyIdp = React.createClass({
  render: function() {
    var roles = Object.keys(this.props.roles);
    return (
      <div className="l-mini">
        {this.renderRoles(roles)}
        {this.renderLicenseContact(licenseContact)}
      </div>
    );
  },

  renderRoles: function(roles) {
    return (
      <div className="mod-idp">
        <h1>{I18n.t("my_idp.title")}</h1>
        <p dangerouslySetInnerHTML={{__html: I18n.t("my_idp.sub_title_html") }}></p>
        <table>
          <thead>
          <tr>
            <th className="percent_50">{I18n.t("my_idp.role")}</th>
            <th className="percent_50">{I18n.t("my_idp.users")}</th>
          </tr>
          </thead>
          <tbody>
          {roles.map(this.renderRole)}
          </tbody>
        </table>
      </div>
    );
  },

  renderRole: function(role) {
    var names = this.props.roles[role].map(function(r) {
      return r.firstName + " " + r.surname
    }).sort().join(", ");
    var roleName = I18n.t("my_idp")[role];
    return (
      <tr key={role}>
        <td>{roleName}</td>
        <td>{names}</td>
      </tr>
    );
  }

});
