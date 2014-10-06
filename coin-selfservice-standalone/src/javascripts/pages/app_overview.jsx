/** @jsx React.DOM */

App.Pages.AppOverview = React.createClass({
  mixins: [
    React.addons.LinkedStateMixin,
    App.Mixins.SortableTable("apps.overview", "name")
  ],

  getInitialState: function() {
    return {
      search: "",
      activeFacets: App.store.activeFacets || {}
    }
  },

  render: function () {
    var filteredApps = this.filteredApps();

    if (App.currentUser.dashboardAdmin) {
      var connect = (
        <th className="percent_10 right">
          {I18n.t("apps.overview.connect")}
        </th>
      );
    }

    return (
      <div className="l-main">
        <div className="l-left">
          <App.Components.Facets
            facets={this.props.facets}
            selectedFacets={this.state.activeFacets}
            filteredCount={filteredApps.length}
            totalCount={this.props.apps.length}
            onChange={this.handleFacetChange}
            onReset={this.handleResetFilters}
            onDownload={this.handleDownloadOverview} />
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
                  {this.renderSortableHeader("percent_25", "name")}
                  {this.renderSortableHeader("percent_15", "license")}
                  {this.renderSortableHeader("percent_15", "connected")}
                  {connect}
                </tr>
              </thead>
              <tbody>
              {this.sort(filteredApps).map(this.renderApp)}
              </tbody>
            </table>
          </div>
        </div>
      </div>
      );
  },

  renderApp: function(app) {
    if (App.currentUser.dashboardAdmin) {
      var connect = (
        <td className="right">
          {this.renderConnectButton(app)}
        </td>
      );
    }

    return (
      <tr key={app.id} onClick={this.handleShowAppDetail(app)}>
        <td><a href={page.uri("/apps/:id", {id: app.id})}>{app.name}</a></td>
        {this.renderLicenseStatus(app)}
        {App.renderYesNo(app.connected)}
        {connect}
      </tr>
    );
  },

  renderLicenseStatus: function(app) {
    if (app.hasCrmLink) {
      return App.renderYesNo(app.license);
    } else {
      return (
        <td>{I18n.t("apps.overview.license_unknown")}</td>
      );
    }
  },

  renderConnectButton: function(app) {
    if (!app.connected) {
      return <a onClick={this.handleShowHowToConnect(app)} className="c-button narrow">{I18n.t("apps.overview.connect")}</a>;
    }
  },

  handleShowAppDetail: function(app) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      page("/apps/:id", {id: app.id});
    }
  },

  handleShowHowToConnect: function(app) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      page("/apps/:id/how_to_connect", {id: app.id});
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
    App.store.activeFacets = selectedFacet;
  },

  handleResetFilters: function() {
    this.setState({
      search: "",
      activeFacets: {}
    });

    App.store.activeFacets = null;
  },

  handleDownloadOverview: function() {
    App.Controllers.Apps.downloadOverview(this.filteredApps());
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
      switch (this.state.activeFacets["license"]) {
        case "yes":
          if (!app.license) {
            return false;
          }
          break;
        case "no":
          if (app.license || !app.hasCrmLink) {
            return false;
          }
          break;
        case "unknown":
          if (app.hasCrmLink) {
            return false;
          }
          break;
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
  },

  convertLicenseForSort: function(value, app) {
    if (app.hasCrmLink) {
      if (value) {
        return 2;
      } else {
        return 1;
      }
    } else {
      return 0;
    }
  },

  convertNameForSort: function(value) {
    return value.toLowerCase();
  }
});
