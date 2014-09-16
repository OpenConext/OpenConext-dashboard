/** @jsx React.DOM */

App.Components.Facets = React.createClass({
  render: function() {
    var facets = this.staticFacets().concat(this.props.facets);

    return (
      <div className="mod-filters">
        <h1 className="ugly">{this.renderResetFilters()}</h1>
        {facets.map(this.renderFacet)}
        <fieldset>
          {this.renderTotals()}
        </fieldset>
      </div>
    );
  },

  renderResetFilters: function() {
    return (
      <span>
        {I18n.t("facets.title")}
        <small>
          &nbsp;(<a href="#" onClick={this.handleResetFilters}>{I18n.t("facets.reset")}</a>)
        </small>
      </span>
    );
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
          checked={this.props.selectedFacets[facet] == value}
          type="checkbox"
          onChange={this.handleSelectFacet(facet, value)} />
        {facetValue.value}
      </label>
    );
  },

  handleSelectFacet: function(facet, facetValue) {
    return function(e) {
      e.stopPropagation();
      this.props.onChange(facet, facetValue);
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
        { value: I18n.t("facets.static.license.no_license"), searchValue: "no" }
      ]
    }];
  },

});
