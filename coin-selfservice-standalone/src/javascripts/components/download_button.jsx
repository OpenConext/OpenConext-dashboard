/** @jsx React.DOM */

App.Components.DownloadButton = React.createClass({

  onDownload: function (e) {
    e.preventDefault();
    e.stopPropagation();
    this.props.genFile(this.donePreparing);
  },

  donePreparing: function (data) {
    console.log("Done download..");
    var blob = new Blob([data], {type: this.props.mimeType}),
        url = window.URL.createObjectURL(blob);

    this.saveAs(url);

    window.URL.revokeObjectURL(url);
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
      location.replace(uri);
    }
  },

  render: function () {
    return <a href="#" className={this.props.className} onClick={this.onDownload}>{this.props.title}</a>;
  }
});
