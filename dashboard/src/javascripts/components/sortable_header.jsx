import React from "react";
import I18n from "i18n-js";

class SortableHeader extends React.Component {
  renderSortDirection() {
    if (this.props.sortAscending) {
      return <i className="fa fa-sort-asc"></i>;
    }

    return <i className="fa fa-sort-desc"></i>;
  }

  handleSort(e) {
    e.preventDefault();
    e.stopPropagation();

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
        <a href="#" onClick={e => this.handleSort(e)}>
          {I18n.t(this.props.localeKey + "." + this.props.attribute)}
          {icon}
        </a>
      </th>
    );
  }
}

SortableHeader.propTypes = {
  sortAscending: React.PropTypes.bool,
  onSort: React.PropTypes.func.isRequired,
  attribute: React.PropTypes.string.isRequired,
  sortAttribute: React.PropTypes.string.isRequired,
  className: React.PropTypes.string,
  localeKey: React.PropTypes.string.isRequired
};

SortableHeader.defaultProps = {
  className: ""
};

export default SortableHeader;
