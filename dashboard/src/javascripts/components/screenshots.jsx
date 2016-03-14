/** @jsx React.DOM */

App.Components.Screenshots = React.createClass({
  getInitialState: function() {
    return {
      open: false
    };
  },

  render: function() {
    var screenshotsUrls = this.props.screenshotUrls;
    if (!screenshotsUrls) {
      screenshotsUrls = [];
    }
    return (
      <div className="mod-screenshots">
        {screenshotsUrls.map(this.renderScreenshotThumbnail)}
        {this.renderOpenScreenshot()}
      </div>
    );
  },

  renderScreenshotThumbnail: function(screenshot, index) {
    return (
      <a key={index} className="screenshot-thumb" href="#" onClick={this.showScreenshot(index)}>
        <img src={screenshot} />
      </a>
    );
  },

  showScreenshot: function (index) {
    return function (event) {
      event.preventDefault();
      event.stopPropagation();
      console.log("Clicked..", index);
      this.setState({open: true, index: index});
    }.bind(this);
  },

  renderOpenScreenshot: function() {
    if (this.state.open) {
      return (
        <div className="lightbox-overlay">
          <a href="#" className="close-btn" onClick={this.closeScreenshot}>{I18n.t("apps.detail.close_screenshot")}</a>
          <a href="#" onClick={this.closeScreenshot}>
            <img src={this.props.screenshotUrls[this.state.index]}/>
          </a>
        </div>
      );
    }
  },

  closeScreenshot: function(event) {
    event.preventDefault();
    event.stopPropagation();
    this.setState({open: false});
  }

});
