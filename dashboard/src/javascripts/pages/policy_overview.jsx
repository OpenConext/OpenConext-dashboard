import React from "react";
import I18n from "i18n-js";

import Link from "react-router/Link";

import { deletePolicy, getPolicies } from "../api";
import { setFlash } from "../utils/flash";
import sort from "../utils/sort";

import Flash from "../components/flash";
import SortableHeader from "../components/sortable_header";

class PolicyOverview extends React.Component {
  constructor() {
    super();

    this.state = {
      search: "",
      sortAttribute: "name",
      sortAscending: undefined,
      policies: []
    };
  }

  componentWillMount() {
    getPolicies().then(data => this.setState({ policies: data.payload }));
  }

  render() {
    const filteredPolicies = this.filterPolicies(this.state.policies);
    const { currentUser } = this.context;

    return (
      <div className="l-main">
        <Flash />
        {this.renderHeader()}
        <div className="mod-policy-list">
          <table>
            <thead>
              <tr>
                {this.renderSortableHeader("percent_20", "name")}
                {this.renderSortableHeader("percent_20", "description")}
                {this.renderSortableHeader("percent_20", "serviceProviderName")}
                {this.renderSortableHeader("percent_20", "identityProviderNames")}
                {this.renderSortableHeader("percent_10", "active")}
                {this.renderSortableHeader("percent_10", "numberOfRevisions")}
                {currentUser.dashboardAdmin ? (<th className="percent_5"></th>) : null}
              </tr>
            </thead>
            <tbody>
              {sort(filteredPolicies, this.state.sortAttribute, this.state.sortAscending).map(this.renderPolicy.bind(this))}
            </tbody>
          </table>
        </div>
      </div>
    );
  }

  handleSort(sortObject) {
    this.setState({
      sortAttribute: sortObject.sortAttribute,
      sortAscending: sortObject.sortAscending
    });
  }

  renderSortableHeader(className, attribute) {
    return (
      <SortableHeader
        sortAttribute={this.state.sortAttribute}
        attribute={attribute}
        sortAscending={this.state.sortAscending}
        className={className}
        localeKey="policies.overview"
        onSort={this.handleSort.bind(this)}
        />
    );
  }

  renderHeader() {
    const search = (
      <div className="mod-policy-search">
        <fieldset>
            <i className="fa fa-search"/>
            <input
              type="search"
              value={this.state.search}
              onChange={e => this.setState({ search: e.target.value })}
              placeholder={I18n.t("policies.overview.search_hint")}/>
            <button type="submit">{I18n.t("policies.overview.search")}</button>
        </fieldset>
      </div>
    );

    const { currentUser } = this.context;
    return currentUser.dashboardAdmin ?
      (
        <div className="l-grid">
          <div className="l-col-9">
            {search}
          </div>
          <div className="l-col-3 text-right no-gutter">
            <Link to={"/policies/new"} className="t-button new-policy">
              <i className="fa fa-plus"/> {I18n.t("policies.new_policy")}
            </Link>
          </div>
        </div>
     ) : (
       <div className="l-grid">
         <div className="l-col-12 no-gutter">
           {search}
         </div>
       </div>
     );
  }

  renderPolicy(policy, i) {
    const { currentUser } = this.context;
    return (
      <tr key={i}>
        <td>{policy.name}</td>
        <td>{policy.description}</td>
        <td>{policy.serviceProviderName}</td>
        <td>{this.renderIdpNames(policy)}</td>
        <td><input type="checkbox" defaultChecked={policy.active} disabled="true"/></td>
        <td>{this.renderRevisionsLink(policy)}</td>
        {currentUser.dashboardAdmin ? (<td>{this.renderControls(policy)}</td>) : null}
      </tr>
    );
  }

  renderIdpNames(policy) {
    return policy.identityProviderNames.map(name => {
      return (<p key={name}>{name}</p>);
    });
  }

  renderRevisionsLink(policy) {
    const numberOfRevisions = (policy.numberOfRevisions + 1);
    return (
      <Link to={`/policies/${policy.id}/revisions`}>{numberOfRevisions}</Link>
    );
  }

  renderControls(policy) {
    if (policy.actionsAllowed) {
      return (
          <div className="controls">
            <Link to={`/policies/${policy.id}`} data-tooltip={I18n.t("policies.edit")}>
              <i className="fa fa-edit"></i>
            </Link>
            <a href="#" data-tooltip={I18n.t("policies.delete")} onClick={this.handleDeletePolicyDetail(policy).bind(this)}>
              <i className="fa fa-remove"></i>
            </a>
          </div>
      );
    }

    return null;
  }

  handleDeletePolicyDetail(policy) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      if (confirm(I18n.t("policies.confirmation", { policyName: policy.name }))) {
        deletePolicy(policy.id).then(() => {
          getPolicies().then(data => {
            this.setState({ policies: data.payload });
            setFlash(I18n.t("policies.flash", { policyName: policy.name, action: I18n.t("policies.flash_deleted") }));
          });
        });
      }
    };
  }

  filterPolicies(policies) {
    return policies.filter(policy => {
      return policy.name.toLowerCase().indexOf(this.state.search.toLowerCase()) >= 0;
    });
  }
}

PolicyOverview.contextTypes = {
  currentUser: React.PropTypes.object
};

export default PolicyOverview;
