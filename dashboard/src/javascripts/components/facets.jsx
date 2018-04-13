import React from "react";
import I18n from "i18n-js";
import { FacetShape } from "../shapes";

class Facets extends React.Component {
  render() {
    const {facets} = this.props;

    return (
      <div className="mod-filters">
        <div className="header">
          <h1>{I18n.t("facets.title")}</h1>
        </div>
        <form>
          <fieldset>
            {this.renderResetFilters()}
            {this.renderDownloadButton()}
          </fieldset>
          { facets.map(facet => this.renderFacet(facet)) }
          <fieldset>
            {this.renderTotals()}
          </fieldset>
        </form>
      </div>
    );
  }

  renderResetFilters() {
    return (
      <a
        className={"c-button" + (this.props.filteredCount >= this.props.totalCount ? " disabled" : "")}
        href="#"
        onClick={this.handleResetFilters.bind(this)}>{I18n.t("facets.reset")}</a>
    );
  }

  renderTotals() {
    const count = this.props.filteredCount;
    const total = this.props.totalCount;

    if (count === total) {
      return I18n.t("facets.totals.all", { total: total });
    }

    return I18n.t("facets.totals.filtered", { count: count, total: total });
  }

  renderFacet(facet) {
    return (
      <fieldset key={facet.name}>
        <a href="#" onClick={this.handleFacetToggle(facet)}>
          {this.renderDropDownIndicator(facet)}
        </a>

        <h2>{facet.name}</h2>
        {this.renderFacetOptions(facet)}
        {this.renderFacetInfo(facet)}
      </fieldset>
    );
  }

  renderFacetOptions(facet) {
    if (!this.props.hiddenFacets[facet.name]) {
      return (
        facet.values.map(value => {
          return this.renderFacetValue(facet, value);
        }));
    }
    return null;
  }

  renderFacetInfo(facet) {
    if (facet.name === I18n.t("facets.static.arp.name")) {
      const url = window.location.href;
      return (
        <em dangerouslySetInnerHTML={{ __html: I18n.t("facets.static.arp.info_html", {url: url.replace("dashboard", "profile").slice(0, url.lastIndexOf("/")-2)}) }} className="arp-info"></em>
      );
    }
    return null;
  }

  handleFacetToggle(facet) {
    return function(e) {
      e.stopPropagation();
      this.props.onHide(facet);
    }.bind(this);
  }

  renderDropDownIndicator(facet) {
    if (this.props.hiddenFacets[facet.name]) {
      return <i className="fa fa-caret-down float-right"/>;
    }

    return <i className="fa fa-caret-up float-right"/>;
  }

  renderFacetValue(facet, facetValue) {
    const facetName = facet.searchValue || facet.name;
    const value = facetValue.searchValue || facetValue.value;

    return (
      <label key={facetValue.value} className={facetValue.count === 0 ? "greyed-out" : ""}>
        <input
          checked={Array.isArray(this.props.selectedFacets[facetName]) && this.props.selectedFacets[facetName].indexOf(value) > -1}
          type="checkbox"
          onChange={this.handleSelectFacet(facetName, value)}/>
        {facetValue.value} ({facetValue.count})
      </label>
    );
  }

  renderDownloadButton() {
    return (
      <a href="#" className={"download-button c-button" + (this.props.filteredCount <= 0 ? " disabled" : "")}
         onClick={this.handleDownload.bind(this)}>{I18n.t("facets.download")}</a>
    );
  }

  handleDownload(e) {
    e.preventDefault();
    e.stopPropagation();
    this.props.onDownload();
  }

  handleSelectFacet(facet, facetValue) {
    return function(e) {
      e.stopPropagation();
      this.props.onChange(facet, facetValue, e.target.checked);
    }.bind(this);
  }

  handleResetFilters(e) {
    e.preventDefault();
    e.stopPropagation();
    this.props.onReset();
  }

}

Facets.propTypes = {
  onReset: React.PropTypes.func.isRequired,
  onDownload: React.PropTypes.func.isRequired,
  onChange: React.PropTypes.func.isRequired,
  onHide: React.PropTypes.func.isRequired,
  facets: React.PropTypes.arrayOf(FacetShape),
  filteredCount: React.PropTypes.number.isRequired,
  totalCount: React.PropTypes.number.isRequired,
  hiddenFacets: React.PropTypes.objectOf(React.PropTypes.bool),
  selectedFacets: React.PropTypes.objectOf(React.PropTypes.arrayOf(React.PropTypes.string))
};

export default Facets;
