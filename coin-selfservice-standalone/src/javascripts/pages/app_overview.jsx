/** @jsx React.DOM */

App.Pages.AppOverview = React.createClass({
  mixins: [React.addons.LinkedStateMixin],

  getInitialState: function() {
    return {
      search: "",
      activeFacets: {}
    }
  },

  render: function () {
    var filteredApps = this.filteredApps();

    return (
      <div className="l-main">
        <div className="l-left">
          <App.Components.Facets
            facets={this.props.facets}
            selectedFacets={this.state.activeFacets}
            filteredCount={filteredApps.length}
            totalCount={this.props.apps.length}
            onChange={this.handleFacetChange}
            onReset={this.handleResetFilters} />
        </div>
        <div className="l-right">
          <div className="mod-app-search">
            <fieldset>
              <input
                type="search"
                valueLink={this.linkState("search")}
                placeholder={I18n.t("apps.overview.search_hint")} />

              <button type="submit">{I18n.t("apps.overview.search")}</button>
            </fieldset>
          </div>
          <div className="mod-app-list">
            <table>
              <thead>
                <tr>
                  <th className="percent_25">{I18n.t("apps.overview.application")}</th>
                  <th className="percent_25">{I18n.t("apps.overview.provider")}</th>
                  <th className="percent_10">{I18n.t("apps.overview.license")}</th>
                  <th className="percent_10">{I18n.t("apps.overview.connection")}</th>
                  <th className="percent_15 date">{I18n.t("apps.overview.added")}</th>
                </tr>
              </thead>
              <tbody>
              {filteredApps.map(this.renderApp)}
              </tbody>
            </table>
          </div>
        </div>
      </div>
      );
  },

  renderApp: function(app) {
    return (
      <tr key={app.id} onClick={this.handleShowAppDetail(app)}>
        <td>{app.name}</td>
        <td>###</td>
        {this.renderYesNo(app.license)}
        {this.renderYesNo(app.connected)}
        <td className="date">###</td>
      </tr>
    );
  },

  renderYesNo: function(value) {
    var word = value ? "yes" : "no";
    return <td className={word}>{I18n.t("boolean." + word)}</td>;
  },

  handleShowAppDetail: function(app) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      page("/apps/:id", {id: app.id});
    }
  },

  handleFacetChange: function(facet, facetValue) {
    var selectedFacet = $.extend({}, this.state.activeFacets);
    if (selectedFacet[facet] == facetValue) {
      delete selectedFacet[facet];
    } else {
      selectedFacet[facet] = facetValue;
    }
    this.setState({activeFacets: selectedFacet});
  },

  handleResetFilters: function() {
    this.setState({
      search: "",
      activeFacets: {}
    });
  },

  filteredApps: function() {
    var filteredApps = this.props.apps;
    filteredApps = filteredApps.filter(this.filterBySearchQuery);

    if (!$.isEmptyObject(this.state.activeFacets)) {
      filteredApps = filteredApps.filter(this.filterByFacets);
      filteredApps = filteredApps.filter(this.filterConnectionFacet);
      filteredApps = filteredApps.filter(this.filterLicenseFacet);
    }

    return filteredApps;
  },

  filterBySearchQuery: function(app) {
    return app.name.toLowerCase().indexOf(this.state.search.toLowerCase()) >= 0;
  },

  filterConnectionFacet: function(app) {
    if (this.state.activeFacets["connection"]) {
      var check = this.state.activeFacets["connection"] == "yes";
      if (app.connected != check) {
        return false;
      }
    }

    return true;
  },

  filterLicenseFacet: function(app) {
    if (this.state.activeFacets["license"]) {
      if (this.state.activeFacets["license"] == "yes") {
        if (!app.license) {
          return false;
        }
      } else {
        if (app.license) {
          return false;
        }
      }
    }

    return true;
  },

  filterByFacets: function(app) {
    var normalizedCategories = this.normalizeCategories(app);
    var match = true;

    for (facet in this.state.activeFacets) {
      if (normalizedCategories[facet]) {
        if (normalizedCategories[facet].indexOf(this.state.activeFacets[facet]) < 0) {
          match = false;
        }
      }
    }

    return match;
  },

  normalizeCategories: function(app) {
    var normalizedCategories = {}
    app.categories.forEach(function(category) {
      normalizedCategories[category.name] = category.values.map(function(categoryValue) {
        return categoryValue.value;
      });
    });
    return normalizedCategories;
  }

});
