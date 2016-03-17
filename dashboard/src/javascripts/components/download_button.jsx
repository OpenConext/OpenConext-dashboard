/** @jsx React.DOM */

App.Components.DownloadButton = React.createClass({

  onDownload: function (e) {
    e.preventDefault();
    e.stopPropagation();
    this.props.genFile();
  },

  render: function () {
    return <a href="#" className={this.props.className} onClick={this.onDownload}>{this.props.title}</a>;
  }
});
