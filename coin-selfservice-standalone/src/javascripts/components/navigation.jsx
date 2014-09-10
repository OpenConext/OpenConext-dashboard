/** @jsx React.DOM */

App.Components.Navigation = React.createClass({
  render: function () {
    return (
      <div className="mod-navigation">
        <ul>
          <li><a href="#">Apps</a></li>
          <li><a href="#">Notifications</a></li>
          <li><a href="#">History</a></li>
          <li><a href="#">Statistics</a></li>
        </ul>
      </div>
    );
  }
});
