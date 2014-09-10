/** @jsx React.DOM */

App.Pages.AppDetail = React.createClass({
  render: function () {
    return (
      <div className="l-center">

        <div className="l-left">
          <div className="mod-app-nav">
            <ul>
              <li><a href="#">Overview</a></li>
              <li><a href="#">License info</a></li>
              <li><a href="#">Application usage</a></li>
              <li><a href="#">Attribute policy</a></li>
              <li><a href="#">How to connect</a></li>
            </ul>
          </div>
        </div>

        <div className="l-right">
          <div className="mod-app-meta">
            <div className="logo">
              <img src="http://placehold.it/200x150" />
            </div>
            <div className="contact">
              <h2>Administrative contact</h2>
              <address>
                Frans Ward<br />
                06 50 868 666<br />
                <a href="mailto:fransward@surfconext.nl">fransward@surfconext.nl</a>
              </address>
            </div>
            <div className="contact">
              <h2>Administrative contact</h2>
              <address>
                Frans Ward<br />
                06 50 868 666<br />
                <a href="mailto:fransward@surfconext.nl">fransward@surfconext.nl</a>
              </address>
            </div>
            <div className="contact">
              <h2>Administrative contact</h2>
              <address>
                Frans Ward<br />
                06 50 868 666<br />
                <a href="mailto:fransward@surfconext.nl">fransward@surfconext.nl</a>
              </address>
            </div>
          </div>
        </div>

        <div className="l-middle">
          <div className="mod-title">
            <h1>3TU Datacentrum</h1>
            <h2>Application by TU Delft</h2>
          </div>

          <div className="mod-connection">
            <div className="technical yes">
              <h2>No technical connection</h2>
              <p>Read <a href="#">how to connect</a></p>
            </div>
            <div className="license no">
              <h2>License information unknown</h2>
              <p>Read <a href="#">how to connect</a></p>
            </div>
          </div>

          <div className="mod-description">
            <h2>Description</h2>
            <p>3TU.datacentrum offers the knowledge, experience and the tools to archive research data in a standardized, secure and well-documented manner. It provides the research community with:</p>
            <ul>
              <li>A long term archive for strogin scientificidata</li>
              <li>Permanent access to and tools for reuse of research data</li>
              <li>Advice and support on data management</li>
            </ul>
            <p>3TU.Datacentrum currently hosts about 5000 datasets. To see examples please visit: http://data.3tu.nl</p>
          </div>

          <div className="mod-screenshots">
            <a href="#"><img src="http://placehold.it/400x300" /></a>
            <a href="#"><img src="http://placehold.it/400x300" /></a>
            <a href="#"><img src="http://placehold.it/400x300" /></a>
          </div>
        </div>

      </div>
    );
  }
});
