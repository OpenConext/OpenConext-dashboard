/** @jsx React.DOM */

App.Components.Screenshots = React.createClass({
  render: function() {
    return (
      <div className="mod-screenshots">
        {this.props.screenshotUrls.map(this.renderScreenshot)}
      </div>
    );
  },

  renderScreenshot: function(screenshot, index) {
    return (
      <a key={index} href="#">
        <img src={screenshot} />
      </a>
    );
  }
});
