import React from "react";

import Select2 from "react-select";

class Select2Selector extends React.Component {
  onChange(val) {
    if (_.isArray(val)) {
      return this.props.handleChange(val.map(v => v.value));
    } else {
      return this.props.handleChange(val.value);
    }
  }

  render() {
    const defaultValue = this.props.defaultValue || (this.props.multiple ? [] : '');
    const data = this.props.options.map((option) => ({ label: option.display, value: option.value }));
    const minimumResultsForSearch = this.props.minimumResultsForSearch || 7;

    return (
      <Select2
        value={defaultValue}
        options={data}
        multi={this.props.multiple}
        onChange={val => this.onChange(val)}
        style={{width: '100%'}}
        placeholder={this.props.placeholder}
        searchable={data.length >= minimumResultsForSearch}
        clearable={false}
      />
    );
  }
}

export default Select2Selector;
