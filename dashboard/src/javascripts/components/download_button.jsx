import React from "react";

class DownloadButton extends React.Component {
  onDownload(e) {
    e.preventDefault();
    e.stopPropagation();
    this.props.genFile();
  }

  render() {
    return <a href="#" className={this.props.className} onClick={this.onDownload}>{this.props.title}</a>;
  }
}

export default DownloadButton;
