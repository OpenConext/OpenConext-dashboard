import React from "react";
import I18n from "../lib/i18n";

class SortableHeader extends React.Component {
  renderSortDirection() {
    if (this.props.sortAscending) {
      return <i className="fa fa-sort-asc"></i>;
    } else {
      return <i className="fa fa-sort-desc"></i>;
    }
  }

  handleSort(e) {
    e.preventDefault();
    e.stopPropagation();

    this.props.onSort({
      sortAttribute: this.props.attribute,
      sortAscending: this.props.sortAttribute == this.props.attribute ? !this.props.sortAscending : false
    })
  }

  render() {
    if (this.props.sortAttribute == this.props.attribute) {
      var icon = this.renderSortDirection();
    } else {
      var icon = <i className="fa fa-sort"></i>;
    }

    return (
      <th className={this.props.className}>
        <a href="#" onClick={(e) => this.handleSort(e)}>
          {I18n.t(this.props.localeKey + "." + this.props.attribute)}
          {icon}
          </a>
        </th>
    );
  }
}

export default SortableHeader;
