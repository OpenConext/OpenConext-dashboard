import React from "react";
import PropTypes from "prop-types";

import Select from "react-select";

class SelectWrapper extends React.Component {
    onChange(val) {
        if (Array.isArray(val)) {
            return this.props.handleChange(val.map(v => v.value));
        }

        return this.props.handleChange(val ? val.value : null, val ? val.label : null);
    }

    render() {
        const defaultValue = this.props.defaultValue || (this.props.multiple ? [] : "");
        const data = (this.props.options || []).map(option => ({label: option.display, value: option.value}));
        const minimumResultsForSearch = this.props.minimumResultsForSearch || 7;
        const valueFromId = (opts, id) => Array.isArray(id) ? opts.filter(o => id.includes(o.value)) : opts.find(o => o.value === id);
        return (
            <Select
                value={valueFromId(data, defaultValue)}
                options={data}
                isMulti={this.props.multiple}
                onChange={val => this.onChange(val)}
                placeholder={this.props.placeholder}
                isSearchable={data.length >= minimumResultsForSearch}
                isClearable={this.props.isClearable || false}
            />
        );
    }
}

SelectWrapper.propTypes = {
    handleChange: PropTypes.func.isRequired,
    defaultValue: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.arrayOf(PropTypes.string)
    ]),
    multiple: PropTypes.bool,
    isClearable: PropTypes.bool,
    options: PropTypes.arrayOf(PropTypes.shape({
        display: PropTypes.string,
        value: PropTypes.string
    })),
    placeholder: PropTypes.string,
    minimumResultsForSearch: PropTypes.number
};

export default SelectWrapper;
