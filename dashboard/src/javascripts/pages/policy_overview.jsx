/** @jsx React.DOM */

App.Pages.PolicyOverview = React.createClass({
  mixins: [
    React.addons.LinkedStateMixin,
    App.Mixins.SortableTable("policies.overview", "name")
  ],

  getInitialState: function () {
    return {
      search: ""
    }
  },

  componentWillReceiveProps: function (nextProps) {
    if (!_.isEmpty(this.props) && this.props.flash !== nextProps.flash) {
      this.setState({hideFlash: false});
    }
  },

  render: function () {
    var filteredPolicies = this.filterPolicies(this.props.policies);

    return (
      <div className="l-main">
        {this.renderFlash()}
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
          <div className="l-col-2 text-right no-gutter">
            <a href={page.uri("/policies/new")} className="t-button new-policy">
              <i className="fa fa-plus"/> {I18n.t("policies.new_policy")}</a>
          </div>
        </div>
        <div className="mod-policy-list">
          <table>
            <thead>
              <tr>
                {this.renderSortableHeader("percent_20", "name")}
                {this.renderSortableHeader("percent_20", "description")}
                {this.renderSortableHeader("percent_20", "serviceProviderName")}
                {this.renderSortableHeader("percent_20", "identityProviderNames")}
                {this.renderSortableHeader("percent_10", "numberOfRevisions")}
                <th className="percent_5"></th>
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

  renderFlash: function () {
    var flash = this.props.flash;

    if (flash && !this.state.hideFlash) {
      return (
          <div className="flash">
            <p className={flash.type} dangerouslySetInnerHTML={{__html: flash.message }}></p>
            <a className="close" href="#" onClick={this.closeFlash}><i className="fa fa-remove"></i></a>
          </div>
      );
    }
  },

  closeFlash: function () {
    this.setState({hideFlash: true});
  },

  renderPolicy: function (policy, i) {
    return (
      <tr key={i}>
        <td>{policy.name}</td>
        <td>{policy.description}</td>
        <td>{policy.serviceProviderName}</td>
        <td>{this.renderIdpNames(policy)}</td>
        <td className="number">{this.renderRevisionsLink(policy)}</td>
        <td>{this.renderControls(policy)}</td>
      </tr>
    );
  },

  renderIdpNames: function (policy) {
    return policy.identityProviderNames.map(function (name) {
      return (<p key={name}>{name}</p>)
    });
  },

  renderRevisionsLink: function (policy) {
    var numberOfRevisions = (policy.numberOfRevisions + 1)
    return (
      <a href={page.uri("/policies/:id/revisions", {id: policy.id})}
        onClick={this.handleShowRevisions(policy)}>{numberOfRevisions}</a>
    );
  },

  handleShowRevisions: function (policy) {
    return function (e) {
       e.preventDefault();
       e.stopPropagation();
       page("/policies/:id/revisions", {id: policy.id});
     }
  },

  renderControls: function(policy) {
    if (policy.actionsAllowed) {
      return (
          <div className="controls">
            <a href={page.uri("/policies/:id", {id: policy.id})} onClick={this.handleShowPolicyDetail(policy)}
               data-tooltip={I18n.t("policies.edit")}> <i className="fa fa-edit"></i>
            </a>
            <a href="#" data-tooltip={I18n.t("policies.delete")} onClick={this.handleDeletePolicyDetail(policy)}>
              <i className="fa fa-remove"></i>
            </a>
          </div>
      );
    }
  },

  handleShowPolicyDetail: function (policy) {
    return  function (e) {
      e.preventDefault();
      e.stopPropagation();
      page("/policies/:id", {id: policy.id});
    };
  },

  handleDeletePolicyDetail: function (policy) {
    return function (e) {
      e.preventDefault();
      e.stopPropagation();
      if (confirm(I18n.t("policies.confirmation", {policyName: policy.name}))) {
        App.Controllers.Policies.deletePolicy(policy);
      }
    };
  },

  filterPolicies: function (policies) {
    return policies.filter(function (policy) {
      return policy.name.toLowerCase().indexOf(this.state.search.toLowerCase()) >= 0;
    }.bind(this));
  }

});
