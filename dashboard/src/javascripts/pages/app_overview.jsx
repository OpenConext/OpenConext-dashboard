import React from "react";

  // mixins: [
  //   React.addons.LinkedStateMixin,
  //   App.Mixins.SortableTable("apps.overview", "name")
  // ],


class AppOverview extends React.Component {
  constructor() {
    super();

    this.state = {
      search: "",
      activeFacets: App.store.activeFacets || {},
      hiddenFacets: App.store.hiddenFacets || {}
    }
  }

  render() {
    var filteredExclusiveApps = this.filterAppsForExclusiveFilters(this.props.apps);

    if (App.currentUser.dashboardAdmin && App.currentIdp().institutionId) {
      var connect = (
        <th className="percent_10 right">
          {I18n.t("apps.overview.connect")}
        </th>
      );
    }

    var facets = this.staticFacets().concat(this.props.facets);
    this.addNumbers(filteredExclusiveApps, facets);
    var filteredApps = this.filterAppsForInclusiveFilters(filteredExclusiveApps);

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
            <form>
              <fieldset>
                <i className="fa fa-search"/>
                <input
                  type="search"
                  valueLink={this.linkState("search")}
                  placeholder={I18n.t("apps.overview.search_hint")}/>

                <button type="submit">{I18n.t("apps.overview.search")}</button>
              </fieldset>
            </form>
          </div>
          <div className="mod-app-list">
            <table>
              <thead>
                <tr>
                  {this.renderSortableHeader("percent_25", "name")}
                  {this.renderSortableHeader("percent_15", "licenseStatus")}
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
  }

  renderEmpty() {
    return <td className="empty" colSpan="4">{I18n.t("apps.overview.no_results")}</td>;
  }

  renderApp(app) {
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
  }

  licenseStatusClassName(app) {
    switch (app.licenseStatus) {
      case "HAS_LICENSE_SURFMARKET":
      case "HAS_LICENSE_SP":
        return "yes"
      case "NO_LICENSE":
        return "no";
      default:
        return "";
    }
  }

  renderLicenseNeeded(app) {
    return (
      <td
        className={this.licenseStatusClassName(app)}>{I18n.t("facets.static.license." + app.licenseStatus.toLowerCase())}</td>
    );
  }

  renderLicensePresent(app) {
    switch (app.licenseStatus) {
      case "HAS_LICENSE_SURFMARKET":
        if (!app.hasCrmLink) {
          licensePresent = "unknown";
        } else {
          licensePresent = app.license ? "yes" : "no";
        }
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
  }

  renderConnectButton(app) {
    if (!app.connected) {
      return <a onClick={this.handleShowHowToConnect(app)} className="c-button narrow">{I18n.t("apps.overview.connect_button")}</a>;
    }
  }

  handleShowAppDetail(app) {
    return function (e) {
      e.preventDefault();
      e.stopPropagation();
      page("/apps/:id", {id: app.id});
    }
  }

  handleShowHowToConnect(app) {
    return function (e) {
      e.preventDefault();
      e.stopPropagation();
      page("/apps/:id/how_to_connect", {id: app.id});
    }
  }

  /*
   * this.state.activeFacets is a object with facet names and the values are arrays with all select values
   */
  handleFacetChange(facet, facetValue, checked) {
    var selectedFacets = $.extend({}, this.state.activeFacets);
    var facetValues = selectedFacets[facet];

    if (_.isUndefined(facetValues)) {
      facetValues = selectedFacets[facet] = [facetValue];
    } else {
      checked ? facetValues.push(facetValue) : facetValues.splice(facetValues.indexOf(facetValue), 1);
    }

    this.setState({activeFacets: selectedFacets});

    App.store.activeFacets = selectedFacets;
  }

  handleFacetHide(facet) {
    var hiddenFacets = $.extend({}, this.state.hiddenFacets);
    if (hiddenFacets[facet.name]) {
      delete hiddenFacets[facet.name];
    } else {
      hiddenFacets[facet.name] = true;
    }
    this.setState({hiddenFacets: hiddenFacets});
    App.store.hiddenFacets = hiddenFacets;
  }

  handleResetFilters() {
    this.setState({
      search: "",
      activeFacets: {},
      hiddenFacets: {}
    });

    App.store.activeFacets = null;
    App.store.hiddenFacets = null;
  }

  handleDownloadOverview() {
    var filteredApps = this.filterAppsForInclusiveFilters(this.filterAppsForExclusiveFilters(this.props.apps));
    App.Controllers.Apps.downloadOverview(filteredApps);
  }

  filterAppsForExclusiveFilters(apps) {
    return apps.filter(this.filterBySearchQuery);
  }

  filterAppsForInclusiveFilters(apps) {
    var filteredApps = apps;

    if (!$.isEmptyObject(this.state.activeFacets)) {
      filteredApps = filteredApps.filter(this.filterByFacets(this.state.activeFacets));
      this.staticFacets().forEach(function (facetObject) {
        filteredApps = filteredApps.filter(facetObject.filterApp);
      });
    }

    return filteredApps;
  }

  addNumbers(filteredApps, facets) {
    var me = this;
    var filter = function (facet, filterFunction) {
      var activeFacetsWithoutCurrent = _.pick(this.state.activeFacets, function (value, key, object) {
        return key !== facet.name;
      });
      var filteredWithoutCurrentFacetApps = filteredApps.filter(this.filterByFacets(activeFacetsWithoutCurrent));

      this.staticFacets().filter(function (facetObject) {
        return facetObject.searchValue != facet.searchValue;
      }).forEach(function (facetObject) {
        filteredWithoutCurrentFacetApps = filteredWithoutCurrentFacetApps.filter(facetObject.filterApp);
      });

      facet.values.forEach(function (facetValue) {
        facetValue.count = filteredWithoutCurrentFacetApps.filter(function (app) {
          return filterFunction(app, facetValue);
        }).length;
      });
    }.bind(this);

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
  }

  filterBySearchQuery(app) {
    return app.name.toLowerCase().indexOf(this.state.search.toLowerCase()) >= 0;
  }

  filterYesNoFacet(name, yes) {
    var values = this.state.activeFacets[name] || [];
    return values.length === 0
      || (yes && _.contains(values, "yes"))
      || (!yes && _.contains(values, "no"));
  }

  filterByFacets(facets) {
    return function (app) {
      var normalizedCategories = this.normalizeCategories(app);
      for (var facet in facets) {
        var facetValues = facets[facet] || [];
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
    }.bind(this);
  }

  normalizeCategories(app) {
    var normalizedCategories = {}
    app.categories.forEach(function (category) {
      normalizedCategories[category.name] = category.values.map(function (categoryValue) {
        return categoryValue.value;
      });
    });
    return normalizedCategories;
  }

  convertLicenseForSort(value, app) {
    return app.licenseStatus;
  }

  convertNameForSort(value) {
    return value.toLowerCase();
  }

  staticFacets() {
    return [{
      name: I18n.t("facets.static.connection.name"),
      searchValue: "connection",
      values: [
        {value: I18n.t("facets.static.connection.has_connection"), searchValue: "yes"},
        {value: I18n.t("facets.static.connection.no_connection"), searchValue: "no"},
      ],
      filterApp: function (app) {
        return this.filterYesNoFacet("connection", app.connected);
      }.bind(this),
    }, {
      name: I18n.t("facets.static.used_by_idp.name"),
      searchValue: "used_by_idp",
      values: [
        {value: I18n.t("facets.static.used_by_idp.yes"), searchValue: "yes"},
        {value: I18n.t("facets.static.used_by_idp.no"), searchValue: "no"},
      ],
      filterApp: function (app) {
        return this.filterYesNoFacet("used_by_idp", App.currentIdp().institutionId === app.institutionId);
      }.bind(this),
    }, {
      name: I18n.t("facets.static.published_edugain.name"),
      searchValue: "published_edugain",
      values: [
        {value: I18n.t("facets.static.published_edugain.yes"), searchValue: "yes"},
        {value: I18n.t("facets.static.published_edugain.no"), searchValue: "no"},
      ],
      filterApp: function (app) {
        return this.filterYesNoFacet("published_edugain", app.publishedInEdugain);
      }.bind(this),
    }, {
      name: I18n.t("facets.static.license.name"),
      searchValue: "license",
      values: [
        {value: I18n.t("facets.static.license.has_license_surfmarket"), searchValue: "HAS_LICENSE_SURFMARKET"},
        {value: I18n.t("facets.static.license.has_license_sp"), searchValue: "HAS_LICENSE_SP"},
        {value: I18n.t("facets.static.license.not_needed"), searchValue: "NOT_NEEDED"},
        {value: I18n.t("facets.static.license.unknown"), searchValue: "UNKNOWN"},
      ],
      filterApp: function (app) {
        var licenseFacetValues = this.state.activeFacets["license"] || [];
        return licenseFacetValues.length === 0 || licenseFacetValues.indexOf(app.licenseStatus) > -1;
      }.bind(this)
    }];
  }

}

export default AppOverview;
