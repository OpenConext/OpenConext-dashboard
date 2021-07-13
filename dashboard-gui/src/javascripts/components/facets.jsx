import React from 'react'
import PropTypes from 'prop-types'
import I18n from 'i18n-js'
import { FacetShape } from '../shapes'
import ReactTooltip from 'react-tooltip'
import stopEvent from '../utils/stop'
import { exportApps } from '../api'

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
          <h3>{I18n.t('facets.title')}</h3>
        </div>
        <form>
          <fieldset>{this.renderTotals()}</fieldset>
          <fieldset>
            {this.renderResetFilters()}
            {this.renderDownloadButton()}
          </fieldset>
          {facets.map((facet) => this.renderFacet(facet))}
          <fieldset>{this.renderTotals()}</fieldset>
        </form>
      </div>
    )
  }

  renderResetFilters() {
    return (
      <a
        className={'c-button' + (this.props.filteredCount >= this.props.totalCount ? ' disabled' : '')}
        href="/reset"
        onClick={this.handleResetFilters.bind(this)}
      >
        {I18n.t('facets.reset')}
      </a>
    )
  }

  renderTotals() {
    const count = this.props.filteredCount
    const total = this.props.totalCount

    if (count === total) {
      return I18n.t('facets.totals.all', { total: total })
    }

    return I18n.t('facets.totals.filtered', { count: count, total: total })
  }

  renderFacet(facet) {
    return (
      <fieldset key={facet.name}>
        <a href="/dropdown" onClick={this.handleFacetToggle(facet)}>
          {this.renderDropDownIndicator(facet)}
        </a>

        <span className="facet-name">
          <h3>{facet.name}</h3>
          {facet.tooltip && (
            <span>
              <i className="fa fa-info-circle" data-for={facet.name} data-tip></i>
              <ReactTooltip
                id={facet.name}
                type="info"
                class="tool-tip"
                effect="solid"
                multiline={true}
                delayHide={250}
              >
                <span dangerouslySetInnerHTML={{ __html: facet.tooltip }} />
              </ReactTooltip>
            </span>
          )}
        </span>
        {this.renderFacetOptions(facet)}
        {facet.extraContentRenderer && facet.extraContentRenderer()}
      </fieldset>
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
      return <i className="fa fa-caret-down" />
    }

    return <i className="fa fa-caret-up" />
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

  renderDownloadButton() {
    if ('msSaveBlob' in window.navigator || 'download' in HTMLAnchorElement.prototype) {
      const { downloading } = this.props
      const className = this.props.filteredCount <= 0 || downloading ? ' disabled' : ''
      return (
        <span onClick={this.handleDownload.bind(this)} className={'download-button c-button' + className}>
          {I18n.t('facets.download')}
        </span>
      )
    }
    return null
  }

  fake_click = (obj) => {
    const ev = document.createEvent('MouseEvents')
    ev.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null)
    obj.dispatchEvent(ev)
  }

  handleDownload(e) {
    stopEvent(e)
    if (this.state.downloading) {
      return
    }
    this.setState({ downloading: true })
    const { currentUser } = this.props
    const ids = this.props.apps.map((app) => app.id)
    exportApps(currentUser.getCurrentIdpId(), ids).then((res) => {
      const urlObject = window.URL || window.webkitURL || window
      const lines = res.reduce((acc, arr) => {
        acc.push(arr.join(','))
        return acc
      }, [])
      const csvContent = lines.join('\n')
      const export_blob = new Blob([csvContent])
      if ('msSaveBlob' in window.navigator) {
        window.navigator.msSaveBlob(export_blob, 'services.csv')
      } else if ('download' in HTMLAnchorElement.prototype) {
        const save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a')
        save_link.href = urlObject.createObjectURL(export_blob)
        save_link.download = 'services.csv'
        this.fake_click(save_link)
      }

      this.setState({ downloading: false })
    })
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
