import React from "react";
import PropTypes from "prop-types";
import stopEvent from "../utils/stop";

class DownloadButton extends React.Component {
    onDownload(e) {
        stopEvent(e);
        this.props.genFile();
    }

    render() {
        return <a href="/download" className={this.props.className} onClick={e => this.onDownload(e)}>{this.props.title}</a>;
    }
}

DownloadButton.propTypes = {
    className: PropTypes.string,
    genFile: PropTypes.func.isRequired,
    title: PropTypes.string.isRequired
};

DownloadButton.defaultProps = {
    className: ""
};

export default DownloadButton;
