/** @jsx React.DOM */

App.Components.IDPSelector = React.createClass({

  render: function () {
    if (App.superUserNotSwitched()) {
      return null;
    } else {
      return (
        <li>
          <h2>{I18n.t("header.switch_idp")}</h2>
          {this.renderMenu()}
        </li>
      );
    }
  },

  renderSelectedIdp: function() {
    var idp = (App.currentUser.switchedToIdp || App.currentUser.currentIdp);
    return idp.name;
  },

  renderMenu: function() {
    return (
      <ul>
        {App.currentUser.institutionIdps.map(this.renderItem)}
      </ul>
    );
  },

  renderItem: function(idp) {
    return (
      <li key={idp.id}><a href="#" onClick={this.handleChooseIdp(idp)}>{idp.name}</a></li>
    );
  },

  handleChooseIdp: function(idp) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      App.Controllers.User.switchToIdp(idp);
    }.bind(this)
  }
});

