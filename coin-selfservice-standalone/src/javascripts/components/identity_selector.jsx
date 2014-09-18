/** @jsx React.DOM */

App.Components.IdentitySelector = React.createClass({
  render: function () {
    return (
      <span className="ugly identity">
        <a href="/users/switch">Switch idendity</a>
      </span>
    );
  }

});
