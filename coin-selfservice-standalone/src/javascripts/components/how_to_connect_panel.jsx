/** @jsx React.DOM */

App.Components.HowToConnectPanel = React.createClass({
  render: function() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>How to connect</h1>
          <p>You can establish a connection from this dashboard. We advise you to follow the checklist and check the specific information for this app before you connect.</p>
        </div>

        <div className="mod-connect">
          <div className="box">
            <h2>General checklist</h2>
            <ul>
              <li>Check the <a href="#">license information</a></li>
              <li>Check the <a href="#">attribute policy</a></li>
              <li>Read the <a href="#">wiki for this application</a></li>
            </ul>
            <h2>Specific information</h2>
            <p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae.</p>
            <ul>
              <li>Read the <a href="#">wiki for this application</a></li>
            </ul>
          </div>
          <p className="cta">
            <a href="#" className="c-button">Connect application</a>
            <em>(will take you to the form to establish the connection)</em>
          </p>
        </div>
      </div>
    );
  }
});
