/** @jsx React.DOM */

App.Components.Screenshot = React.createClass({
  render: function () {
    var keyValue = "screenshot-" + this.props.index;
    return (
        <a key={keyValue} href="#">
          <img src={this.props.screenshotUrl} />
        </a>
      );
  }
});


App.Components.Screenshots = React.createClass({
  render: function() {
    var screenshotUrls = this.props.screenshotUrls.map(function(value, index) {
      return <App.Components.Screenshot screenshotUrl={value} index={index}/>;
    });
    return (
      <div className="mod-screenshots">
        {screenshotUrls}
      </div>
      );
  }
});
