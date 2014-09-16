/** @jsx React.DOM */

App.Components.IDPSelector = React.createClass({
  getInitialState: function() {
    return {
      active: false
    }
  },

  render: function () {
    return (
      <span className={"ugly idp" + (this.state.active ? " active" : "")}>
        <a href="#" className="toggle" onClick={this.handleToggle}>{App.currentUser.currentIdp.name}</a>
        {this.renderMenu()}
      </span>
    );
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
      App.Controllers.User.switchToIdp(idp);
    }
  }
});

