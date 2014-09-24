/** @jsx React.DOM */

App.Pages.AppOverview = React.createClass({
  mixins: [React.addons.LinkedStateMixin],

  getInitialState: function() {
    return {
      search: "",
      activeFacets: {},
      sortAttribute: "name",
      sortAscending: false
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
                  {this.renderSortableHeader("percent_25", "name")}
                  {this.renderSortableHeader("percent_15", "license")}
                  {this.renderSortableHeader("percent_15", "connected")}
                  <th className="percent_10 right">
                    {I18n.t("apps.overview.connect")}
                  </th>
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

  renderSortableHeader: function(className, attribute) {
    if (this.state.sortAttribute == attribute) {
      var icon = this.renderSortDirection();
    } else {
      var icon = <i className="fa fa-sort"></i>;
    }

    return (
      <th className={className}>
        <a href="#" onClick={this.handleSort(attribute)}>
          {I18n.t("apps.overview." + attribute)}
          {icon}
        </a>
      </th>
    );
  },

  renderSortDirection: function() {
    if (this.state.sortAscending) {
      return <i className="fa fa-sort-asc"></i>;
    } else {
      return <i className="fa fa-sort-desc"></i>;
    }
  },

  renderApp: function(app) {
    return (
      <tr key={app.id} onClick={this.handleShowAppDetail(app)}>
        <td><a href={page.uri("/apps/:id", {id: app.id})}>{app.name}</a></td>
        {App.renderYesNo(app.license)}
        {App.renderYesNo(app.connected)}
        <td className="right">
          {this.renderConnectButton(app)}
        </td>
      </tr>
    );
  },

  renderConnectButton: function(app) {
    if (!app.connected) {
      return <a onClick={this.handleShowHowToConnect(app)} className="c-button narrow">{I18n.t("apps.overview.connect")}</a>;
    }
  },

  handleSort: function(attribute) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      if (this.state.sortAttribute == attribute) {
        this.setState({sortAscending: !this.state.sortAscending});
      } else {
        this.setState({
          sortAttribute: attribute,
          sortAscending: false
        });
      }
    }.bind(this);
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
  },

  handleResetFilters: function() {
    this.setState({
      search: "",
      activeFacets: {}
    });
  },

  sort: function(apps) {
    return apps.sort(function(a, b) {
      var aAttr = a[this.state.sortAttribute];
      var bAttr = b[this.state.sortAttribute];

      switch (this.state.sortAttribute) {
        case "name":
          aAttr = aAttr.toLowerCase();
          bAttr = bAttr.toLowerCase();
          break;
        case "license":
          aAttr = !!aAttr;
          bAttr = !!bAttr;
          break;
      }

      var result = this.compare(aAttr, bAttr);

      if (this.state.sortAscending) {
        return result * -1;
      }
      return result;
    }.bind(this));
  },

  compare: function(a, b) {
    if (a < b) {
      return -1;
    } else if (a > b) {
      return 1;
    }
    return 0;
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
