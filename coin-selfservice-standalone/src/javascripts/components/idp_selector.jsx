/** @jsx React.DOM */

App.Components.IDPSelector = React.createClass({
  getInitialState: function() {
    return {
      active: false
    }
  },

  render: function () {
    if (App.superUserNotSwitched()) {
      return null;
    } else {
      return (
        <span className={"ugly idp" + (this.state.active ? " active" : "")}>
          <a href="#" className="toggle" onClick={this.handleToggle}>{this.renderSelectedIdp()}</a>
          {this.renderMenu()}
        </span>
      );
    }
  },

  renderSelectedIdp: function() {
    var idp = (App.currentUser.switchedToIdp || App.currentUser.currentIdp);
    return idp.name;
  },

  renderMenu: function() {
    if (this.state.active) {
      return (
        <ul>
          {App.currentUser.institutionIdps.map(this.renderItem)}
        </ul>
      );
    }
  },

  renderItem: function(idp) {
    return (
      <li key={idp.id}><a href="#" onClick={this.handleChooseIdp(idp)}>{idp.name}</a></li>
    );
  },

  handleToggle: function(e) {
    e.preventDefault();
    e.stopPropagation();
    this.setState({active: !this.state.active});
  },

  handleChooseIdp: function(idp) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      App.Controllers.User.switchToIdp(idp, null, function() {
        this.setState({active: false});
      }.bind(this));
    }.bind(this)
  }
});

