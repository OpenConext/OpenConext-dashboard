import React from "react";
import I18n from "i18n-js";
import SortableHeader from "../components/sortable_header";
import Link from "react-router/Link";

import {apiUrl, getApps, getFacets} from "../api";
import sort from "../utils/sort";

import Facets from "../components/facets";
import YesNo from "../components/yes_no";

const store = {
  activeFacets: null,
  hiddenFacets: null
};

class AppOverview extends React.Component {
  constructor() {
    super();

    this.state = {
      apps: [],
      facets: [],
      arpAttributes: [],
      search: "",
      activeFacets: store.activeFacets || {},
      hiddenFacets: store.hiddenFacets || {},
      sortAttribute: "name",
      sortAscending: undefined
    };
  }

  componentWillMount() {
    Promise.all([
      getFacets().then(data => data.payload),
      getApps().then(data => data.payload)
    ]).then(data => {
      const [facets, apps] = data;

      // We need to sanitize the categories data for each app to ensure the facet totals are correct
      const unknown = {value: I18n.t("facets.unknown")};
      facets.forEach(facet => {
        apps.forEach(app => {
          app.categories = app.categories || [];
          const appCategory = app.categories.filter(category => {
            return category.name === facet.name;
          });
          if (appCategory.length === 0) {
            app.categories.push({name: facet.name, values: [unknown]});
            const filtered = facet.values.filter(facetValue => {
              return facetValue.value === unknown.value;
            });
            if (!filtered[0]) {
              facet.values.push(Object.assign({}, unknown));
            }
          }
        });
      });
      const attributes = apps.reduce((acc, app) => {
        Object.keys(app.arp.attributes).forEach(attr => acc.add(attr));
        return acc;
      }, new Set());
      this.setState({apps: apps, facets: facets, arpAttributes: [...attributes]});
    });
  }

  handleSort(sortObject) {
    this.setState({
      sortAttribute: sortObject.sortAttribute,
      sortAscending: sortObject.sortAscending
    });
  }

  render() {
    const {currentUser} = this.context;
    const {sortAttribute, sortAscending} = this.state;
    const filteredExclusiveApps = this.filterAppsForExclusiveFilters(this.state.apps);
    let connect = null;

    if (currentUser.dashboardAdmin && currentUser.getCurrentIdp().institutionId) {
      connect = (
        <th className="percent_10 right">
          {I18n.t("apps.overview.connect")}
        </th>
      );
    }

    const facets = this.staticFacets().concat(this.state.facets);
    this.addNumbers(filteredExclusiveApps, facets);
    const filteredApps = this.filterAppsForInclusiveFilters(filteredExclusiveApps);

    return (
      <div className="l-main">
        <div className="l-left">
          <Facets
            facets={facets}
            selectedFacets={this.state.activeFacets}
            hiddenFacets={this.state.hiddenFacets}
            filteredCount={filteredApps.length}
            totalCount={this.state.apps.length}
            onChange={this.handleFacetChange.bind(this)}
            onHide={this.handleFacetHide.bind(this)}
            onReset={this.handleResetFilters.bind(this)}
            onDownload={this.handleDownloadOverview.bind(this)}/>
        </div>
        <div className="l-right">
          <div className="mod-app-search">
            <div>
              <i className="fa fa-search"/>
              <input
                type="search"
                value={this.state.search}
                onChange={e => this.setState({search: e.target.value})}
                placeholder={I18n.t("apps.overview.search_hint")}/>
              <button type="submit">{I18n.t("apps.overview.search")}</button>
            </div>
          </div>
          <div className="mod-app-list">
            <table>
              <thead>
              <tr>
                {this.renderSortableHeader("percent_50", "name")}
                {this.renderSortableHeader("percent_25", "licenseStatus")}
                {this.renderSortableHeader("percent_25", "connected")}
                {connect}
              </tr>
              </thead>
              <tbody>
              {filteredApps.length > 0 ? sort(filteredApps, sortAttribute, sortAscending).map(app => this.renderApp(app)) : this.renderEmpty()}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    );
  }


  renderSortableHeader(className, attribute) {
    return (
      <SortableHeader
        sortAttribute={this.state.sortAttribute}
        attribute={attribute}
        sortAscending={this.state.sortAscending}
        localeKey="apps.overview"
        className={className}
        onSort={this.handleSort.bind(this)}
      />
    );
  }

  renderEmpty() {
    return (
      <tr>
        <td className="empty" colSpan="4">{I18n.t("apps.overview.no_results")}</td>
      </tr>
    );
  }

  renderApp(app) {
    const {currentUser} = this.context;
    let connect = null;
    if (currentUser.dashboardAdmin && currentUser.getCurrentIdp().institutionId) {
      connect = (
        <td className="right">
          {this.renderConnectButton(app)}
        </td>
      );
    }

    return (
      <tr key={app.id} onClick={e => this.handleShowAppDetail(e, app)}>
        <td><Link to={`apps/${app.id}/overview`}>{app.name}</Link></td>
        {this.renderLicenseNeeded(app)}
        <YesNo value={app.connected}/>
        {connect}
      </tr>
    );
  }

  licenseStatusClassName(app) {
    switch (app.licenseStatus) {
      case "HAS_LICENSE_SURFMARKET":
      case "HAS_LICENSE_SP":
        return "yes";
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
    let licensePresent = "unknown";

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
      return <Link to={`/apps/${app.id}/how_to_connect`} className="c-button narrow"
                   onClick={e => e.stopPropagation()}>{I18n.t("apps.overview.connect_button")}</Link>;
    }
    return null;
  }

  handleShowAppDetail(e, app) {
    e.preventDefault();
    e.stopPropagation();
    this.context.router.transitionTo(`/apps/${app.id}/overview`);
  }

  /*
   * this.state.activeFacets is a object with facet names and the values are arrays with all select values
   */
  handleFacetChange(facet, facetValue, checked) {
    const selectedFacets = _.merge({}, this.state.activeFacets);
    let facetValues = selectedFacets[facet];

    if (_.isUndefined(facetValues)) {
      facetValues = selectedFacets[facet] = [facetValue];
    } else {
      checked ? facetValues.push(facetValue) : facetValues.splice(facetValues.indexOf(facetValue), 1);
    }

    this.setState({activeFacets: selectedFacets});

    store.activeFacets = selectedFacets;
  }

  handleFacetHide(facet) {
    const hiddenFacets = _.merge({}, this.state.hiddenFacets);
    if (hiddenFacets[facet.name]) {
      delete hiddenFacets[facet.name];
    } else {
      hiddenFacets[facet.name] = true;
    }
    this.setState({hiddenFacets: hiddenFacets});
    store.hiddenFacets = hiddenFacets;
  }

  handleResetFilters() {
    this.setState({
      search: "",
      activeFacets: {},
      hiddenFacets: {}
    });

    store.activeFacets = null;
    store.hiddenFacets = null;
  }

  handleDownloadOverview() {
    const {currentUser} = this.context;
    const filteredApps = this.filterAppsForInclusiveFilters(this.filterAppsForExclusiveFilters(this.state.apps));
    const ids = filteredApps.map(app => app.id);
    window.open(apiUrl(`/services/download?idpEntityId=${encodeURIComponent(currentUser.getCurrentIdpId())}&ids=${ids.join(",")}`));
  }

  filterAppsForExclusiveFilters(apps) {
    return apps.filter(this.filterBySearchQuery.bind(this));
  }

  filterAppsForInclusiveFilters(apps) {
    let filteredApps = apps;

    if (!_.isEmpty(this.state.activeFacets)) {
      filteredApps = filteredApps.filter(this.filterByFacets(this.state.activeFacets));
      this.staticFacets().forEach(facetObject => {
        filteredApps = filteredApps.filter(facetObject.filterApp);
      });
    }

    return filteredApps;
  }

  addNumbers(filteredApps, facets) {
    const {currentUser} = this.context;
    const me = this;
    const filter = function (facet, filterFunction) {
      const activeFacetsWithoutCurrent = _.pick(this.state.activeFacets, (value, key) => {
        return key !== facet.name;
      });
      let filteredWithoutCurrentFacetApps = filteredApps.filter(this.filterByFacets(activeFacetsWithoutCurrent));

      this.staticFacets().filter(facetObject => {
        return facetObject.searchValue !== facet.searchValue;
      }).forEach(facetObject => {
        filteredWithoutCurrentFacetApps = filteredWithoutCurrentFacetApps.filter(facetObject.filterApp);
      });

      facet.values.forEach(facetValue => {
        facetValue.count = filteredWithoutCurrentFacetApps.filter(app => {
          return filterFunction(app, facetValue);
        }).length;
      });
    }.bind(this);

    facets.forEach(facet => {
      switch (facet.searchValue) {
        case "connection":
          filter(facet, (app, facetValue) => {
            return facetValue.searchValue === "yes" ? app.connected : !app.connected;
          });
          break;
        case "license":
          filter(facet, (app, facetValue) => {
            return app.licenseStatus === facetValue.searchValue;
          });
          break;
        case "interfed_source":
          filter(facet, (app, facetValue) => {
            return app.interfedSource === facetValue.searchValue;
          });
          break;
        case "entity_category":
          filter(facet, (app, facetValue) => {
            return app.entityCategories1 === facetValue.searchValue || app.entityCategories2 === facetValue.searchValue;
          });
          break;
        case "used_by_idp":
          filter(facet, (app, facetValue) => {
            const usedByIdp = currentUser.getCurrentIdp().institutionId === app.institutionId;
            return facetValue.searchValue === "yes" ? usedByIdp : !usedByIdp;
          });
          break;
        case "published_edugain":
          filter(facet, (app, facetValue) => {
            const published = app.publishedInEdugain || false;
            return facetValue.searchValue === "yes" ? published : !published;
          });
          break;
        case "strong_authentication":
          filter(facet, (app, facetValue) => {
            const strongAuthentication = app.strongAuthentication || false;
            return facetValue.searchValue === "yes" ? strongAuthentication : !strongAuthentication;
          });
          break;
        case "attributes":
          filter(facet, (app, facetValue) => {
            const requiredAttributes = Object.keys(app.arp.attributes);
            return requiredAttributes.length === 0 || requiredAttributes.indexOf(facetValue.searchValue) > -1;
          });
          break;
        default:
          filter(facet, (app, facetValue) => {
            const categories = me.normalizeCategories(app);
            const appTags = categories[facet.name] || [];
            return appTags.indexOf(facetValue.value) > -1;
          });
      }
    });
  }

  filterBySearchQuery(app) {
    const searchString = this.state.search.toLowerCase();
    return Object.values(app.names).some(name => name.toLowerCase().indexOf(searchString) > -1) ||
      app.spEntityId.toLowerCase().indexOf(searchString) > -1;
  }

  filterYesNoFacet(name, yes) {
    const values = this.state.activeFacets[name] || [];
    return values.length === 0
      || (yes && _.includes(values, "yes"))
      || (!yes && _.includes(values, "no"));
  }

  filterByFacets(facets) {
    return function (app) {
      const normalizedCategories = this.normalizeCategories(app);
      for (const facet in facets) {
        if (facets.hasOwnProperty(facet)) {
          const facetValues = facets[facet] || [];
          if (normalizedCategories[facet] && facetValues.length > 0) {
            const hits = normalizedCategories[facet].filter(facetValue => {
              return facetValues.indexOf(facetValue) > -1;
            });
            if (hits.length === 0) {
              return false;
            }
          }
        }
      }
      return true;
    }.bind(this);
  }

  normalizeCategories(app) {
    const normalizedCategories = {};
    app.categories.forEach(category => {
      normalizedCategories[category.name] = category.values.map(categoryValue => {
        return categoryValue.value;
      });
    });
    return normalizedCategories;
  }

  staticFacets() {
    const {currentUser} = this.context;
    const {arpAttributes} = this.state;

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
        return this.filterYesNoFacet("used_by_idp", currentUser.getCurrentIdp().institutionId === app.institutionId);
      }.bind(this),
    }, {
      name: I18n.t("facets.static.interfed_source.name"),
      searchValue: "interfed_source",
      values: [
        {value: I18n.t("facets.static.interfed_source.surfconext"), searchValue: "SURFconext"},
        {value: I18n.t("facets.static.interfed_source.edugain"), searchValue: "eduGAIN"},
        {value: I18n.t("facets.static.interfed_source.entree"), searchValue: "Entree"},
      ],
      filterApp: function (app) {
        const sourceFacetValues = this.state.activeFacets["interfed_source"] || [];
        return sourceFacetValues.length === 0 || sourceFacetValues.indexOf(app.interfedSource) > -1;
      }.bind(this)
    }, {
      name: I18n.t("facets.static.entity_category.name"),
      searchValue: "entity_category",
      values: [
        {
          value: I18n.t("facets.static.entity_category.code_of_conduct"),
          searchValue: "http://www.geant.net/uri/dataprotection-code-of-conduct/v1"
        },
        {
          value: I18n.t("facets.static.entity_category.research_and_scholarship"),
          searchValue: "http://refeds.org/category/research-and-scholarship"
        }
      ],
      filterApp: function (app) {
        const sourceFacetValues = this.state.activeFacets["entity_category"] || [];
        return sourceFacetValues.length === 0 || sourceFacetValues.indexOf(app.entityCategories1) > -1 || sourceFacetValues.indexOf(app.entityCategories2) > -1;
      }.bind(this)
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
        const licenseFacetValues = this.state.activeFacets["license"] || [];
        return licenseFacetValues.length === 0 || licenseFacetValues.indexOf(app.licenseStatus) > -1;
      }.bind(this)
    }, {
      name: I18n.t("facets.static.strong_authentication.name"),
      searchValue: "strong_authentication",
      values: [
        {value: I18n.t("facets.static.strong_authentication.yes"), searchValue: "yes"},
        {value: I18n.t("facets.static.strong_authentication.no"), searchValue: "no"}
      ],
      filterApp: function (app) {
        return this.filterYesNoFacet("strong_authentication", app.strongAuthentication);
      }.bind(this)
    },
      {
        name: I18n.t("facets.static.arp.name"),
        searchValue: "attributes",
        values: arpAttributes.map(attr => ({value: attr, searchValue: attr})),
        filterApp: function (app) {
          const attrFacetValues = this.state.activeFacets["attributes"] || [];
          const attributes = Object.keys(app.arp.attributes);
          const res = attributes.length === 0 || attrFacetValues.filter(attr => attributes.indexOf(attr) > -1).length === attrFacetValues.length;
          return res;
        }.bind(this)
      }
    ];
  }

}

AppOverview.contextTypes = {
  currentUser: React.PropTypes.object,
  router: React.PropTypes.object
};

export default AppOverview;
