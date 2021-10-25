import React from 'react'
import PropTypes from 'prop-types'

export default class HighChartContainer extends React.Component {
  componentDidMount() {
    const { highcharts, constructorType = 'chart', options } = this.props
    options.time = { timezoneOffset: new Date().getTimezoneOffset() }
    this.chart = highcharts[constructorType](this.container, options)
  }

  shouldComponentUpdate() {
    return this.props.update || true
  }

  componentDidUpdate() {
    const { options, oneToOne = false } = this.props
    this.chart.update(options, true, oneToOne)
  }

  componentWillUnmount() {
    this.chart.destroy()
  }

  render() {
    const { containerProps = {} } = this.props

    // Add ref to div props
    containerProps.ref = (container) => (this.container = container)

    // Create container for our chart
    return React.createElement('div', containerProps)
  }
}

HighChartContainer.propTypes = {
  highcharts: PropTypes.object.isRequired,
  options: PropTypes.object.isRequired,
  constructorType: PropTypes.string,
  update: PropTypes.bool,
  oneToOne: PropTypes.bool,
}
