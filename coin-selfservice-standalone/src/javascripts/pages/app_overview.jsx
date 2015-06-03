/** @jsx React.DOM */

App.Pages.AppOverview = React.createClass({
  mixins: [
    React.addons.LinkedStateMixin,
    App.Mixins.SortableTable("apps.overview", "name")
  ],

  getInitialState: function () {
    return {
      search: "",
      radioButtonFacets: ["connection", "used_by_idp", "published_edugain"],
      activeFacets: App.store.activeFacets || {},
      hiddenFacets: App.store.hiddenFacets || {}
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
            hiddenFacets={this.state.hiddenFacets}
            filteredCount={filteredApps.length}
            totalCount={this.props.apps.length}
            onChange={this.handleFacetChange}
            onHide={this.handleFacetHide}
            onReset={this.handleResetFilters}
            onDownload={this.handleDownloadOverview}/>
        </div>
        <div className="l-right">
          <div className="mod-app-search">
            <fieldset>
              <i className="fa fa-search"/>
              <input
                type="search"
                valueLink={this.linkState("search")}
                placeholder={I18n.t("apps.overview.search_hint")}/>

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
              {filteredApps.length > 0 ? this.sort(filteredApps).map(this.renderApp) : this.renderEmpty()}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    );
  },

  renderEmpty: function () {
    return <td className="empty" colSpan="4">{I18n.t("apps.overview.no_results")}</td>;
  },

  renderApp: function (app) {
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

  renderLicenseStatus: function (app) {
    if (app.hasCrmLink) {
      return App.renderYesNo(app.license);
    } else {
      return (
        <td>{I18n.t("apps.overview.license_unknown")}</td>
      );
    }
  },

  renderConnectButton: function (app) {
    if (!app.connected) {
      return <a onClick={this.handleShowHowToConnect(app)}
                className="c-button narrow">{I18n.t("apps.overview.connect_button")}</a>;
    }
  },

  handleShowAppDetail: function (app) {
    return function (e) {
      e.preventDefault();
      e.stopPropagation();
      page("/apps/:id", {id: app.id});
    }
  },

  handleShowHowToConnect: function (app) {
    return function (e) {
      e.preventDefault();
      e.stopPropagation();
      page("/apps/:id/how_to_connect", {id: app.id});
    }
  },

  /*
   * this.state.activeFacets is a object with facet names and the values are arrays with all select values
   */
  handleFacetChange: function (facet, facetValue, checked) {
    var selectedFacets = $.extend({}, this.state.activeFacets);
    var facetValues = selectedFacets[facet];
    if (facetValues) {
      checked ? facetValues.push(facetValue) : facetValues.splice(facetValues.indexOf(facetValue), 1);
    } else {
      facetValues = selectedFacets[facet] = [facetValue];
    }
    /*
     * Special case. For some static facets we only want one value (e.g. either 'yes' or 'no')
     */
    if (this.state.radioButtonFacets.indexOf(facet) > -1 && checked && facetValues.length === 2) {
      //we use radio buttons for one-value-facets, but we do want the ability to de-select them
      var nbr = (facetValues[0] === facetValues[1] ? 2 : 1);
      facetValues.splice(0, nbr);
    }
    this.setState({activeFacets: selectedFacets});
    App.store.activeFacets = selectedFacets;
  },


  handleFacetHide: function (facet) {
    var hiddenFacets = $.extend({}, this.state.hiddenFacets);
    if (hiddenFacets[facet.name]) {
      delete hiddenFacets[facet.name];
    } else {
      hiddenFacets[facet.name] = true;
    }
    this.setState({hiddenFacets: hiddenFacets});
    App.store.hiddenFacets = hiddenFacets;
  },

  handleResetFilters: function () {
    this.setState({
      search: "",
      activeFacets: {}
    });

    App.store.activeFacets = null;
  },

  handleDownloadOverview: function () {
    App.Controllers.Apps.downloadOverview(this.filteredApps());
  },

  filteredApps: function () {
    var filteredApps = this.props.apps;
    filteredApps = filteredApps.filter(this.filterBySearchQuery);

    if (!$.isEmptyObject(this.state.activeFacets)) {
      filteredApps = filteredApps.filter(this.filterByFacets);
      filteredApps = filteredApps.filter(this.filterConnectionFacet);
      filteredApps = filteredApps.filter(this.filterLicenseFacet);
      filteredApps = filteredApps.filter(this.filterIdpService);
      filteredApps = filteredApps.filter(this.filterPublishedEdugain);
    }

    return filteredApps;
  },

  filterBySearchQuery: function (app) {
    return app.name.toLowerCase().indexOf(this.state.search.toLowerCase()) >= 0;
  },

  filterConnectionFacet: function (app) {
    var connectionFacetValues = this.state.activeFacets["connection"] || [];
    if (connectionFacetValues.length > 0) {
      return app.connected ? connectionFacetValues[0] === "yes" : connectionFacetValues[0] === "no";
    }
    return true;
  },

  filterLicenseFacet: function (app) {
    var licenseFacetValues = this.state.activeFacets["license"] || [];
    return licenseFacetValues.length === 0 || licenseFacetValues.indexOf(app.licenseInfo) > -1;
  },

  filterByFacets: function (app) {
    var normalizedCategories = this.normalizeCategories(app);

    for (var facet in this.state.activeFacets) {
      var facetValues = this.state.activeFacets[facet] || [];
      if (normalizedCategories[facet] && facetValues.length > 0) {
        var hits = normalizedCategories[facet].filter(function (facetValue) {
          return facetValues.indexOf(facetValue) > -1;
        });
        if (hits.length === 0) {
          return false;
        }
      }
    }
    return true;
  },

  filterIdpService: function (app) {
    var usedByIdpFacetValues = this.state.activeFacets["used_by_idp"] || [];
    if (usedByIdpFacetValues.length > 0) {
      var institutionIdIdp = App.currentIdp().institutionId;
      var institutionIdSp = app.institutionId;
      return institutionIdIdp === institutionIdSp ? usedByIdpFacetValues[0] === "yes" : usedByIdpFacetValues[0] === "no";
    }
    return true;
  },

  filterPublishedEdugain: function (app) {
    var edugainFacetValues = this.state.activeFacets["published_edugain"] || [];
    var published = app.publishedInEdugain || false;
    return edugainFacetValues.length === 0 || edugainFacetValues.indexOf(published.toString()) > -1;
  },

  normalizeCategories: function (app) {
    var normalizedCategories = {}
    app.categories.forEach(function (category) {
      normalizedCategories[category.name] = category.values.map(function (categoryValue) {
        return categoryValue.value;
      });
    });
    return normalizedCategories;
  },

  convertLicenseForSort: function (value, app) {
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

  convertNameForSort: function (value) {
    return value.toLowerCase();
  }
});
