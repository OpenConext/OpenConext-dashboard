/** @jsx React.DOM */

App.Pages.AppOverview = React.createClass({
  mixins: [React.addons.LinkedStateMixin],

  getInitialState: function() {
    return {
      search: ""
    }
  },

  render: function () {
    return (
      <div className="l-main">
        <div className="l-left">
          <App.Components.Facets facets={this.props.facets} />
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
              {this.renderFilteredApps()}
              </tbody>
            </table>
          </div>
        </div>
      </div>
      );
  },

  renderFilteredApps: function() {
    var filteredApps = this.props.apps.filter(function(app) {
      return app.name.toLowerCase().indexOf(this.state.search.toLowerCase()) >= 0;
    }.bind(this));

    return filteredApps.map(this.renderApp);
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
  }
});
