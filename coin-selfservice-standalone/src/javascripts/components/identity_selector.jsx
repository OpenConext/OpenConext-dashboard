/** @jsx React.DOM */

App.Components.IdentitySelector = React.createClass({
  render: function () {
    if (App.currentUser.superUser) {
      return (
        <span className="ugly identity">
          <a href="/users/switch">Switch idendity</a>
        </span>
      );
    } else {
      return null;
    }
  }

});
