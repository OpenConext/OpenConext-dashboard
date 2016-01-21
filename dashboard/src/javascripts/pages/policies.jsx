/** @jsx React.DOM */

App.Pages.Policies = React.createClass({
  mixins: [
    React.addons.LinkedStateMixin,
    App.Mixins.SortableTable("policies.overview", "name")
  ],

  getInitialState: function () {
    return {
      search: ""
    }
  },

  render: function () {
    var filteredPolicies = this.filterPolicies(this.props.policies);

    return (
      <div className="l-main">
        <div className="l-grid">
          <div className="l-col-10">
            <div className="mod-policy-search">
              <fieldset>
                  <i className="fa fa-search"/>
                  <input
                    type="search"
                    valueLink={this.linkState("search")}
                    placeholder={I18n.t("policies.overview.search_hint")}/>
                  <button type="submit">{I18n.t("policies.overview.search")}</button>
              </fieldset>
            </div>
          </div>
          <div className="l-col-2 l-push-right">
            <a href={page.uri("/policies/new")} className="t-button">
              <i className="fa fa-plus"/> {I18n.t("policies.new_policy")}</a>
          </div>
        </div>
        <div className="mod-policy-list">
          <table>
            <thead>
              <tr>
                {this.renderSortableHeader("percent_25", "name")}
                {this.renderSortableHeader("percent_25", "description")}
                {this.renderSortableHeader("percent_25", "service")}
              </tr>
            </thead>
            <tbody>
              {this.sort(filteredPolicies).map(this.renderPolicy)}
            </tbody>
          </table>
        </div>
      </div>
    );
  },

  renderPolicy: function (policy, i) {
    return (
      <tr key={i}>
        <td>{policy.name}</td>
        <td>{policy.description}</td>
        <td>-</td>
      </tr>
    );
  },

  filterPolicies: function (policies) {
    return policies.filter(function (policy) {
      return policy.name.toLowerCase().indexOf(this.state.search.toLowerCase()) >= 0;
    }.bind(this));
  }

});
