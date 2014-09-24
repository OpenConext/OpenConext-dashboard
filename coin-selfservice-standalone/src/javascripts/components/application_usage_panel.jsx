/** @jsx React.DOM */

App.Components.ApplicationUsagePanel = React.createClass({
  render: function() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>Application usage</h1>
        </div>

        <div className="mod-usage">
          <div className="title">
            <h2>Number of logins per week</h2>
            <select>
              <option selected>Last 3 months</option>
            </select>
          </div>
          <div className="body">
          </div>
        </div>
      </div>
    );
  }
});
