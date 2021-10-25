import React from 'react'
import PropTypes from 'prop-types'
import I18n from 'i18n-js'
import { FacetShape } from '../shapes'
import ReactTooltip from 'react-tooltip'
import stopEvent from '../utils/stop'

class Facets extends React.Component {
  constructor(props) {
    super(props)

    this.state = {
      hiddenFacets: [],
      downloading: false,
    }
  }

  render() {
    const { facets } = this.props

    return (
      <div className="mod-filters">
        <div className="header">
          <h2>{I18n.t('facets.title')}</h2>
          {this.props.filteredCount < this.props.totalCount && (
            <button type="button" className="reset-filters" onClick={this.handleResetFilters.bind(this)}>
              {I18n.t('facets.clear_all')}
            </button>
          )}
        </div>
        <div>
          {this.renderTotals()}
          {facets.map((facet) => this.renderFacet(facet))}
          {this.renderTotals()}
        </div>
      </div>
    )
  }

  renderTotals() {
    const count = this.props.filteredCount
    const total = this.props.totalCount

    if (count === total) {
      return <div className="totals">{I18n.t('facets.totals.all', { total: total })}</div>
    }

    return <div className="totals">{I18n.t('facets.totals.filtered', { count: count, total: total })}</div>
  }

  renderFacet(facet) {
    return (
      <div className="facet" key={facet.name}>
        {this.renderDropDownIndicator(facet)}

        <div className="facet-name">
          <h3>{facet.name}</h3>
          {facet.tooltip && (
            <div>
              <i className="fa fa-info-circle" data-for={facet.searchValue} data-tip></i>
              <ReactTooltip
                id={facet.searchValue}
                type="info"
                class="tool-tip"
                effect="solid"
                multiline={true}
                delayHide={250}
              >
                <span dangerouslySetInnerHTML={{ __html: facet.tooltip }} />
              </ReactTooltip>
            </div>
          )}
        </div>
        {this.renderFacetOptions(facet)}
        {facet.extraContentRenderer && facet.extraContentRenderer()}
      </div>
    )
  }

  renderFacetOptions(facet) {
    if (!this.state.hiddenFacets[facet.name]) {
      return facet.values.map((value) => {
        return this.renderFacetValue(facet, value)
      })
    }
    return null
  }

  handleFacetToggle(facet) {
    return function (e) {
      stopEvent(e)
      const hiddenFacets = { ...this.state.hiddenFacets }
      if (hiddenFacets[facet.name]) {
        delete hiddenFacets[facet.name]
      } else {
        hiddenFacets[facet.name] = true
      }
      this.setState({ hiddenFacets: hiddenFacets })
    }.bind(this)
  }

  renderDropDownIndicator(facet) {
    if (this.state.hiddenFacets[facet.name]) {
      return <i className="fa fa-caret-down" onClick={this.handleFacetToggle(facet)} />
    }

    return <i className="fa fa-caret-up" onClick={this.handleFacetToggle(facet)} />
  }

  renderFacetValue(facet, facetValue) {
    const { apps } = this.props
    const facetName = facet.searchValue || facet.name
    const value = facetValue.searchValue || facetValue.value
    const facetValueLabel = facetValue.value === '1.3.6.1.4.1.1076.20.40.40.1' ? 'CollabPersonId' : facetValue.value
    const checked =
      Array.isArray(this.props.selectedFacets[facetName]) && this.props.selectedFacets[facetName].indexOf(value) > -1
    const count = facetValue.count(apps)
    return (
      <label key={facetValue.value} className={count === 0 ? 'greyed-out' : ''}>
        <input
          className={checked ? 'checked' : 'unchecked'}
          checked={checked}
          disabled={!checked && count === 0}
          type="checkbox"
          onChange={this.handleSelectFacet(facetName, value)}
        />
        {facetValueLabel} ({count})
      </label>
    )
  }

  handleSelectFacet(facet, facetValue) {
    return function (e) {
      // stopEvent(e);
      this.props.onChange(facet, facetValue, e.target.checked)
    }.bind(this)
  }

  handleResetFilters(e) {
    stopEvent(e)
    this.props.onReset()
  }
}

Facets.propTypes = {
  onReset: PropTypes.func.isRequired,
  onChange: PropTypes.func.isRequired,
  facets: PropTypes.arrayOf(FacetShape),
  filteredCount: PropTypes.number.isRequired,
  totalCount: PropTypes.number.isRequired,
  selectedFacets: PropTypes.objectOf(PropTypes.arrayOf(PropTypes.string)),
}

export default Facets
