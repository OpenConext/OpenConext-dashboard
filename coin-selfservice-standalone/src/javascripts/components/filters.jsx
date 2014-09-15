/** @jsx React.DOM */

App.Components.Filters = React.createClass({
  render: function() {
    return (
      <div className="mod-filters">
        <h1>Filters</h1>
        <form action="">
          <fieldset>
            <h2>License</h2>
            <label>
              <input type="checkbox" name="license_information" />
            Has license</label>
            <label>
              <input type="checkbox" name="license_information" />
            No license</label>
          </fieldset>
          <fieldset>
            <h2>Connection</h2>
            <label className="inactive">
              <input type="checkbox" name="connection_information" />
            Has connection</label>
            <label>
              <input type="checkbox" name="connection_information" />
            No connection</label>
          </fieldset>
          <fieldset>
            <h2>Usage</h2>
            <label>
              <input type="checkbox" name="usage_information" />
            Used in last 3 months</label>
            <label>
              <input type="checkbox" name="usage_information" />
            Never used</label>
          </fieldset>
        </form>
      </div>
    );
  }
});
