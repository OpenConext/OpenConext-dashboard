/** @jsx React.DOM */

App.Components.Facets = React.createClass({
  render: function() {
    var facets = this.staticFacets().concat(this.props.facets);

    return (
      <div className="mod-filters">
        <h1>{this.renderTitle()}</h1>
        {facets.map(this.renderFacet)}
        <fieldset>
          {this.renderTotals()}
        </fieldset>
        <fieldset>
          {this.renderDownloadButton()}
        </fieldset>
      </div>
    );
  },

  renderTitle: function() {
    return (
      <span>
        {I18n.t("facets.title")}
        {this.renderResetFilters()}
      </span>
    );
  },

  renderResetFilters: function() {
    if (this.props.filteredCount < this.props.totalCount) {
      return (
        <small>
          <a href="#" onClick={this.handleResetFilters}>{I18n.t("facets.reset")}</a>
        </small>
      );
    }
  },

  renderTotals: function() {
    var count = this.props.filteredCount;
    var total = this.props.totalCount;

    if (count == total) {
      return I18n.t("facets.totals.all", { total: total })
    } else {
      return I18n.t("facets.totals.filtered", { count: count, total: total })
    }
  },

  renderFacet: function(facet) {
    return (
      <fieldset key={facet.name}>
        <h2>{facet.name}</h2>
        {
          facet.values.map(function(value) {
            return this.renderFacetValue(facet, value);
          }.bind(this))
        }
      </fieldset>
    );
  },

  renderFacetValue: function(facet, facetValue) {
    var facet = facet.searchValue || facet.name;
    var value = facetValue.searchValue || facetValue.value;

    return (
      <label key={facetValue.value}>
        <input
          checked={Array.isArray(this.props.selectedFacets[facet]) && this.props.selectedFacets[facet].indexOf(value) > -1}
          type="checkbox"
          onChange={this.handleSelectFacet(facet, value)} />
        {facetValue.value}
      </label>
    );
  },

  renderDownloadButton: function() {
    return (
      <a href="#" className={"c-button" + (this.props.filteredCount <= 0 ? " disabled" : "")} onClick={this.handleDownload}>{I18n.t("facets.download")}</a>
    );
  },

  handleDownload: function(e) {
    e.preventDefault();
    e.stopPropagation();

    this.props.onDownload();
  },

  handleSelectFacet: function(facet, facetValue) {
    return function(e) {
      e.stopPropagation();
      this.props.onChange(facet, facetValue, e.target.checked);
    }.bind(this);
  },

  handleResetFilters: function(e) {
    e.preventDefault();
    e.stopPropagation();
    this.props.onReset();
  },

  staticFacets: function() {
    return [{
      name: I18n.t("facets.static.connection.name"),
      searchValue: "connection",
      values: [
        { value: I18n.t("facets.static.connection.has_connection"), searchValue: "yes" },
        { value: I18n.t("facets.static.connection.no_connection"), searchValue: "no" }
      ]
    }, {
      name: I18n.t("facets.static.license.name"),
      searchValue: "license",
      values: [
        { value: I18n.t("facets.static.license.has_license"), searchValue: "yes" },
        { value: I18n.t("facets.static.license.no_license"), searchValue: "no" },
        { value: I18n.t("facets.static.license.unknown_license"), searchValue: "unknown" }
      ]
    }, {
      name: I18n.t("facets.static.used_by_idp.name"),
      searchValue: "used_by_idp",
      values: [
        { value: I18n.t("facets.static.used_by_idp.yes"), searchValue: "yes" },
        { value: I18n.t("facets.static.used_by_idp.no"), searchValue: "no" }
      ]
    }];
  },

});
