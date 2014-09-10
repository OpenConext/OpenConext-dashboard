/** @jsx React.DOM */

App.Pages.AppOverview = React.createClass({
  render: function () {
    return (
      <div className="l-main">
        <div className="l-left">
          <div className="mod-filters">
            <h1>Filters</h1>
            <form action="">
              <fieldset>
                <h2>License</h2>
                <label><input type="checkbox" name="license_information" /> Has license</label>
                <label><input type="checkbox" name="license_information" /> No license</label>
              </fieldset>
              <fieldset>
                <h2>Connection</h2>
                <label className="inactive"><input type="checkbox" name="connection_information" /> Has connection</label>
                <label><input type="checkbox" name="connection_information" /> No connection</label>
              </fieldset>
              <fieldset>
                <h2>Usage</h2>
                <label><input type="checkbox" name="usage_information" /> Used in last 3 months</label>
                <label><input type="checkbox" name="usage_information" /> Never used</label>
              </fieldset>
            </form>
          </div>
        </div>
        <div className="l-right">
          <div className="mod-app-search">
            <form action="">
              <fieldset>
                <input type="search" placeholder="Filter by name, company or keyword" />
                <button type="submit">Search</button>
              </fieldset>
            </form>
          </div>
          <div className="mod-app-list">
            <table>
              <thead>
                <tr>
                  <th className="percent_25">Application</th>
                  <th className="percent_25">Provider</th>
                  <th className="percent_10">License</th>
                  <th className="percent_10">Connection</th>
                  <th className="percent_15 date">Last used</th>
                  <th className="percent_15 date">Added</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>3TU.datacentre</td>
                  <td>Tu Delft</td>
                  <td className="yes">Yes</td>
                  <td className="no">Yes</td>
                  <td className="date">29 aug 2014</td>
                  <td className="date">29 aug 2014</td>
                </tr>
                <tr>
                  <td>3TU.datacentre</td>
                  <td>Tu Delft</td>
                  <td className="yes">Yes</td>
                  <td className="no">Yes</td>
                  <td className="date">29 aug 2014</td>
                  <td className="date">29 aug 2014</td>
                </tr>
                <tr>
                  <td>3TU.datacentre</td>
                  <td>Tu Delft</td>
                  <td className="yes">Yes</td>
                  <td className="no">Yes</td>
                  <td className="date">29 aug 2014</td>
                  <td className="date">29 aug 2014</td>
                </tr>
                <tr>
                  <td>3TU.datacentre</td>
                  <td>Tu Delft</td>
                  <td className="yes">Yes</td>
                  <td className="no">Yes</td>
                  <td className="date">29 aug 2014</td>
                  <td className="date">29 aug 2014</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    );
  }
});
