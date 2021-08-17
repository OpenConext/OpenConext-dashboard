import React from 'react'
import PropTypes from 'prop-types'

import Select from 'react-select'

const colourStyles = {
  control: (base, state) => ({
    ...base,
    backgroundColor: state.isDisabled ? 'white' : 'white',
    borderColor: '#676767',
  }),
  indicatorsContainer: (base) => ({ ...base, backgroundColor: '#94d6ff' }),
  dropdownIndicator: (base) => ({ ...base, padding: '11px', svg: { fill: '#004c97' } }),
  valueContainer: (base) => ({ ...base }),
}

class SelectWrapper extends React.Component {
  onChange(val) {
    const handleChange = this.props.handleChange
    if (handleChange) {
      if (Array.isArray(val)) {
        return handleChange(val.map((v) => v.value))
      }
      return handleChange(val ? val.value : null, val ? val.label : null)
    }
  }

  render() {
    const defaultValue = this.props.defaultValue || (this.props.multiple ? [] : '')
    const data = (this.props.options || []).map((option) => ({ label: option.display, value: option.value }))
    const minimumResultsForSearch = this.props.minimumResultsForSearch || 7
    const valueFromId = (opts, id) =>
      Array.isArray(id) ? opts.filter((o) => id.includes(o.value)) : opts.find((o) => o.value === id)
    return (
      <Select
        className="react-select"
        value={valueFromId(data, defaultValue) || null}
        options={data}
        isDisabled={this.props.isDisabled}
        isMulti={this.props.multiple}
        onChange={(val) => this.onChange(val)}
        styles={colourStyles}
        placeholder={this.props.placeholder}
        isSearchable={data.length >= minimumResultsForSearch}
        isClearable={this.props.isClearable || false}
      />
    )
  }
}

SelectWrapper.propTypes = {
  handleChange: PropTypes.func,
  defaultValue: PropTypes.oneOfType([PropTypes.string, PropTypes.arrayOf(PropTypes.string)]),
  multiple: PropTypes.bool,
  isClearable: PropTypes.bool,
  isDisabled: PropTypes.bool,
  options: PropTypes.arrayOf(
    PropTypes.shape({
      display: PropTypes.string,
      value: PropTypes.string,
    })
  ),
  placeholder: PropTypes.string,
  minimumResultsForSearch: PropTypes.number,
}

export default SelectWrapper
