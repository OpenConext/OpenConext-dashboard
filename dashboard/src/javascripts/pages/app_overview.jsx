/** @jsx React.DOM */

App.Pages.AppOverview = React.createClass({
  mixins: [
    React.addons.LinkedStateMixin,
    App.Mixins.SortableTable("apps.overview", "name")
  ],

  getInitialState: function () {
    return {
      search: "",
      activeFacets: App.store.activeFacets || {},
      hiddenFacets: App.store.hiddenFacets || {}
    }
  },

  render: function () {
    var filteredApps = this.filterAppsForExclusiveFilters(this.props.apps);

    if (App.currentUser.dashboardAdmin && App.currentIdp().institutionId) {
      var connect = (
        <th className="percent_10 right">
          {I18n.t("apps.overview.connect")}
        </th>
      );
    }

    var facets = this.staticFacets().concat(this.props.facets);
    this.addNumbers(filteredApps, facets);
    filteredApps = this.filterAppsForInclusiveFilters(filteredApps);

    return (
      <div className="l-main">
        <div className="l-left">
          <App.Components.Facets
            facets={facets}
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
                {this.renderSortableHeader("percent_15", "license_needed")}
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
    if (App.currentUser.dashboardAdmin && App.currentIdp().institutionId) {
      var connect = (
        <td className="right">
          {this.renderConnectButton(app)}
        </td>
      );
    }

    return (
      <tr key={app.id} onClick={this.handleShowAppDetail(app)}>
        <td><a href={page.uri("/apps/:id", {id: app.id})}>{app.name}</a></td>
        {this.renderLicenseNeeded(app)}
        {this.renderLicensePresent(app)}
        {App.renderYesNo(app.connected)}
        {connect}
      </tr>
    );
  },

  licenseStatusClassName: function (app) {
    switch (app.licenseStatus) {
      case "HAS_LICENSE_SURFMARKET":
      case "HAS_LICENSE_SP":
        return "yes"
      case "NO_LICENSE":
        return "no";
      default:
        return "";
    }
  },

  renderLicenseNeeded: function (app) {
    return (
      <td
        className={this.licenseStatusClassName(app)}>{I18n.t("facets.static.license." + app.licenseStatus.toLowerCase())}</td>
    );
  },

  renderLicensePresent: function (app) {
    switch (app.licenseStatus) {
      case "HAS_LICENSE_SURFMARKET":
        licensePresent = app.license ? "yes" : "no";
        break;
      case "HAS_LICENSE_SP":
        licensePresent = "unknown";
        break;
      case "NOT_NEEDED":
        licensePresent = "na";
        break;
      default:
        licensePresent = "unknown";
        break;
    }

    return (
      <td className={licensePresent}>{I18n.t("apps.overview.license_present." + licensePresent)}</td>
    );
  },

  renderConnectButton: function (app) {
    if (!app.connected) {
      return <a onClick={this.handleShowHowToConnect(app)} className="c-button narrow">{I18n.t("apps.overview.connect_button")}</a>;
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

    if (_.isUndefined(facetValues)) {
      facetValues = selectedFacets[facet] = [facetValue];
    } else {
      checked ? facetValues.push(facetValue) : facetValues.splice(facetValues.indexOf(facetValue), 1);
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
      activeFacets: {},
      hiddenFacets: {}
    });

    App.store.activeFacets = null;
    App.store.hiddenFacets = null;
  },

  handleDownloadOverview: function () {
    var filteredApps = this.filterAppsForInclusiveFilters(this.filterAppsForExclusiveFilters(this.props.apps));
    App.Controllers.Apps.downloadOverview(filteredApps);
  },

  filterAppsForExclusiveFilters: function (apps) {
    return apps.filter(this.filterBySearchQuery);
  },

  filterAppsForInclusiveFilters: function (apps) {
    var filteredApps = apps;

    if (!$.isEmptyObject(this.state.activeFacets)) {
      filteredApps = filteredApps.filter(this.filterByFacets);
      filteredApps = filteredApps.filter(this.filterLicenseFacet);
      filteredApps = filteredApps.filter(this.filterConnectionFacet);
      filteredApps = filteredApps.filter(this.filterIdpService);
      filteredApps = filteredApps.filter(this.filterPublishedEdugain);
    }

    return filteredApps;
  },

  addNumbers: function (filteredApps, facets) {
    var me = this;
    var filter = function (facet, filterFunction) {
      facet.values.forEach(function (facetValue) {
        facetValue.count = filteredApps.filter(function (app) {
          return filterFunction(app, facetValue);
        }).length;
      });
    };

    facets.forEach(function (facet) {
      switch (facet.searchValue) {
        case "connection":
          filter(facet, function (app, facetValue) {
            return facetValue.searchValue === "yes" ? app.connected : !app.connected;
          });
          break;
        case "license":
          filter(facet, function (app, facetValue) {
            return app.licenseStatus === facetValue.searchValue;
          });
          break;
        case "used_by_idp":
          filter(facet, function (app, facetValue) {
            var usedByIdp = App.currentIdp().institutionId === app.institutionId;
            return facetValue.searchValue === "yes" ? usedByIdp : !usedByIdp;
          });
          break;
        case "published_edugain":
          filter(facet, function (app, facetValue) {
            var published = app.publishedInEdugain || false;
            return facetValue.searchValue === "yes" ? published : !published;
          });
          break;
        default:
          filter(facet, function (app, facetValue) {
            var categories = me.normalizeCategories(app);
            var appTags = categories[facet.name] || [];
            return appTags.indexOf(facetValue.value) > -1;
          });
      }
    });
  },

  filterBySearchQuery: function (app) {
    return app.name.toLowerCase().indexOf(this.state.search.toLowerCase()) >= 0;
  },

  filterLicenseFacet: function (app) {
    var licenseFacetValues = this.state.activeFacets["license"] || [];
    return licenseFacetValues.length === 0 || licenseFacetValues.indexOf(app.licenseStatus) > -1;
  },

  filterConnectionFacet: function (app) {
    return this.filterYesNoFacet("connection", app.connected);
  },

  filterIdpService: function (app) {
    return this.filterYesNoFacet("used_by_idp", App.currentIdp().institutionId === app.institutionId);
  },

  filterPublishedEdugain: function (app) {
    return this.filterYesNoFacet("published_edugain", app.publishedInEdugain);
  },

  filterYesNoFacet: function (name, yes) {
    var values = this.state.activeFacets[name] || [];
    return values.length === 0
      || (yes && _.contains(values, "yes"))
      || (!yes && _.contains(values, "no"));
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
    return app.licenseStatus;
  },

  convertNameForSort: function (value) {
    return value.toLowerCase();
  },

  staticFacets: function () {
    return [{
      name: I18n.t("facets.static.connection.name"),
      searchValue: "connection",
      values: [
        {value: I18n.t("facets.static.connection.has_connection"), searchValue: "yes"},
        {value: I18n.t("facets.static.connection.no_connection"), searchValue: "no"},
      ]
    }, {
      name: I18n.t("facets.static.used_by_idp.name"),
      searchValue: "used_by_idp",
      values: [
        {value: I18n.t("facets.static.used_by_idp.yes"), searchValue: "yes"},
        {value: I18n.t("facets.static.used_by_idp.no"), searchValue: "no"},
      ]
    }, {
      name: I18n.t("facets.static.published_edugain.name"),
      searchValue: "published_edugain",
      values: [
        {value: I18n.t("facets.static.published_edugain.yes"), searchValue: "yes"},
        {value: I18n.t("facets.static.published_edugain.no"), searchValue: "no"},
      ]
    }, {
      name: I18n.t("facets.static.license.name"),
      searchValue: "license",
      values: [
        {value: I18n.t("facets.static.license.has_license_surfmarket"), searchValue: "HAS_LICENSE_SURFMARKET"},
        {value: I18n.t("facets.static.license.has_license_sp"), searchValue: "HAS_LICENSE_SP"},
        {value: I18n.t("facets.static.license.not_needed"), searchValue: "NOT_NEEDED"},
        {value: I18n.t("facets.static.license.unknown"), searchValue: "UNKNOWN"},
      ]
    }];
  }

});
