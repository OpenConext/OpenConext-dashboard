import React from "react";

import Select2 from "react-select2-wrapper";

class Select2Selector extends React.Component {
  render() {
    const defaultValue = this.props.defaultValue || (this.props.multiple ? [] : "");
    const data = this.props.options.map((option) => ({ text: option.display, id: option.value }));
    const minimumResultsForSearch = this.props.minimumResultsForSearch || 7;

    return (
      <Select2
        value={defaultValue}
        data={data}
        multiple={this.props.multiple}
        onSelect={(e) => this.props.handleChange(e.target.value)}
        options={{
          placeholder: this.props.placeholder,
          width: "100%",
          allowClear: false,
          forceBelow: true,
          minimumResultsForSearch: minimumResultsForSearch
        }}
      />
    );
  }
}

export default Select2Selector;
