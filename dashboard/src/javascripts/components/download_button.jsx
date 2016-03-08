/** @jsx React.DOM */

App.Components.DownloadButton = React.createClass({

  onDownload: function (e) {
    e.preventDefault();
    e.stopPropagation();
    this.props.genFile(this.donePreparing);
  },

  donePreparing: function (data) {
    var blob = new Blob([data], {type: this.props.mimeType}),
        url = window.URL.createObjectURL(blob);

    this.saveAs(url);

    // revoke the url with a delay otherwise download will fail in FF
    setTimeout(function () {
      window.URL.revokeObjectURL(url);
    }, 200);
  },

  saveAs: function(url) {
    var link = document.createElement("a");
    if (typeof link.download === "string") {
      document.body.appendChild(link);
      link.style = "display: none";
      link.href = url;
      link.download = this.props.fileName;
      link.click();
      document.body.removeChild(link);
    } else {
      location.replace(url);
    }
  },

  render: function () {
    return <a href="#" className={this.props.className} onClick={this.onDownload}>{this.props.title}</a>;
  }
});
