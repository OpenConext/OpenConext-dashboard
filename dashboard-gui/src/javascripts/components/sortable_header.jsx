import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import stopEvent from "../utils/stop";

class SortableHeader extends React.Component {
  renderSortDirection() {
    if (this.props.sortAscending) {
      return <i className="fa fa-sort-asc"></i>;
    }

    return <i className="fa fa-sort-desc"></i>;
  }

  handleSort(e) {
    stopEvent(e);
    this.props.onSort({
      sortAttribute: this.props.attribute,
      sortAscending: this.props.sortAttribute === this.props.attribute ? !this.props.sortAscending : false
    });
  }

  render() {
    let icon;
    if (this.props.sortAttribute === this.props.attribute) {
      icon = this.renderSortDirection();
    } else {
      icon = <i className="fa fa-sort"></i>;
    }

    return (
      <th className={this.props.className}>
        <a href="/sort" onClick={e => this.handleSort(e)}>
          {I18n.t(this.props.localeKey + "." + this.props.attribute)}
          {icon}
        </a>
      </th>
    );
  }
}

SortableHeader.propTypes = {
  sortAscending: PropTypes.bool,
  onSort: PropTypes.func.isRequired,
  attribute: PropTypes.string.isRequired,
  sortAttribute: PropTypes.string.isRequired,
  className: PropTypes.string,
  localeKey: PropTypes.string.isRequired
};

SortableHeader.defaultProps = {
  className: ""
};

export default SortableHeader;
