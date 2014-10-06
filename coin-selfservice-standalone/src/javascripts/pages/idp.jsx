/** @jsx React.DOM */

App.Pages.MyIdp = React.createClass({
  render: function() {
    var roles = Object.keys(this.props.roles);
    var services = this.props.services;
    return (
      <div className="l-mini">

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

          <h2>{I18n.t("my_idp.services_title")}</h2>
          <table>
            <tbody>
            {services.map(function(s) {
              return s.service;
            }).sort(function(l, r) {
              return l.name.localeCompare(r.name);
            }).map(this.renderService)}
            </tbody>
          </table>
        </div>
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
  },

  renderService: function(service) {
    return (
      <tr key={service.id}>
        <td><a href={page.uri("/apps/:id", { id: service.id })}>{service.name}</a></td>
      </tr>
    );
  }

});
