/** @jsx React.DOM */

App.Components.LicenseInfoPanel = React.createClass({
  render: function() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>License information</h1>
          <p>Licences should be obtained at SURFmarket. If we have any information about a license, we show it here.</p>
        </div>

        <div className="mod-connection">
          <div className="license no">
            <i className="fa fa-file-text-o"></i>
            <h2>License information unknown</h2>
            <p>Read <a href="#">how to connect</a></p>
          </div>
        </div>

        <div className="mod-description">
          <h2>Do you need a license?</h2>
          <p>3TU.datacentrum offers the knowledge, experience and the tools to archive research data in a standardized, secure and well-documented manner. It provides the research community with:</p>
          <p><a href="#" className="c-button">Obtain a license</a></p>

          <h2>How to obtain a license</h2>
          <p>3TU.datacentrum offers the knowledge, experience and the tools to archive research data in a standardized, secure and well-documented manner. It provides the research community with:</p>
          <p><a href="#" className="c-button">Obtain a license</a></p>
        </div>
      </div>
    );
  }
});
