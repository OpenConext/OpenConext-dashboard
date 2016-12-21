import React from "react";

class DownloadButton extends React.Component {
  onDownload(e) {
    e.preventDefault();
    e.stopPropagation();
    this.props.genFile();
  }

  render() {
    return <a href="#" className={this.props.className} onClick={e => this.onDownload(e)}>{this.props.title}</a>;
  }
}

DownloadButton.propTypes = {
  className: React.PropTypes.string,
  genFile: React.PropTypes.func.isRequired,
  title: React.PropTypes.string.isRequired
};

DownloadButton.defaultProps = {
  className: ""
};

export default DownloadButton;
