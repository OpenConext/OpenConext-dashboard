/** @jsx React.DOM */

App.Components.Navigation = React.createClass({
  render: function () {
    return (
      <div className="mod-navigation">
        <ul>
          <li><a href="/apps">Apps</a></li>
          <li><a href="/notifications">Notifications</a></li>
          <li><a href="/history">History</a></li>
          <li><a href="/statistics">Statistics</a></li>
        </ul>
      </div>
    );
  }
});
