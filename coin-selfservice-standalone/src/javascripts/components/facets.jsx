/** @jsx React.DOM */

App.Components.Facets = React.createClass({
  render: function() {
    var facets = this.staticFacets().concat(this.props.facets);

    return (
      <div className="mod-filters">
        <h1>{I18n.t("facets.title")}</h1>
        {facets.map(this.renderFacet)}
      </div>
    );
  },

  renderFacet: function(facet) {
    return (
      <fieldset key={facet.name}>
        <h2>{facet.name}</h2>
        {facet.values.map(this.renderFacetValue)}
      </fieldset>
    );
  },

  renderFacetValue: function(facetValue) {
    return (
      <label key={facetValue.value}>
        <input type="checkbox" name="license_information" />
        {facetValue.value}
      </label>
    );
  },

  staticFacets: function() {
    return [{
      name: I18n.t("facets.static.connection.name"),
      values: [
        { value: I18n.t("facets.static.connection.has_connection") },
        { value: I18n.t("facets.static.connection.no_connection") }
      ]
    }, {
      name: I18n.t("facets.static.license.name"),
      values: [
        { value: I18n.t("facets.static.license.has_license") },
        { value: I18n.t("facets.static.license.no_license") }
      ]
    }];
  }
});
