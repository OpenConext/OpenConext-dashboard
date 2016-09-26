import React from "react";
import I18n from "../lib/i18n";

import Link from 'react-router/Link';

import { getPolicies } from "../api";
import sort from "../utils/sort";

import SortableHeader from "../components/sortable_header";
  // mixins: [
  //   React.addons.LinkedStateMixin,
  //   App.Mixins.SortableTable("policies.overview", "name")
  // ],
class PolicyOverview extends React.Component {
  constructor() {
    super();

    this.state = {
      search: "",
      sortAttribute: "name",
      sortAscending: undefined,
      policies: []
    }
  }

  componentWillMount() {
    getPolicies().then(data => this.setState({ policies: data.payload }));

  }

  componentWillReceiveProps(nextProps) {
    if (!_.isEmpty(this.props) && this.props.flash !== nextProps.flash) {
      this.setState({hideFlash: false});
    }
  }

  render() {
    var filteredPolicies = this.filterPolicies(this.state.policies);
    const { currentUser } = this.context;

    return (
      <div className="l-main">
        {this.renderFlash()}
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
              {sort(filteredPolicies).map(this.renderPolicy)}
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
    var search = (
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

  renderFlash() {
    var flash = this.props.flash;

    if (flash && !this.state.hideFlash) {
      return (
          <div className="flash">
            <p className={flash.type} dangerouslySetInnerHTML={{__html: flash.message }}></p>
            <a className="close" href="#" onClick={this.closeFlash}><i className="fa fa-remove"></i></a>
          </div>
      );
    }
  }

  closeFlash() {
    this.setState({hideFlash: true});
  }

  renderPolicy(policy, i) {
    return (
      <tr key={i}>
        <td>{policy.name}</td>
        <td>{policy.description}</td>
        <td>{policy.serviceProviderName}</td>
        <td>{this.renderIdpNames(policy)}</td>
        <td><input type="checkbox" defaultChecked={policy.active} disabled="true"/></td>
        <td>{this.renderRevisionsLink(policy)}</td>
        {App.currentUser.dashboardAdmin ? (<td>{this.renderControls(policy)}</td>) : null}
      </tr>
    );
  }

  renderIdpNames(policy) {
    return policy.identityProviderNames.map(function (name) {
      return (<p key={name}>{name}</p>)
    });
  }

  renderRevisionsLink(policy) {
    var numberOfRevisions = (policy.numberOfRevisions + 1)
    return (
      <a href={page.uri("/policies/:id/revisions", {id: policy.id})}
        onClick={this.handleShowRevisions(policy)}>{numberOfRevisions}</a>
    );
  }

  handleShowRevisions(policy) {
    return function (e) {
       e.preventDefault();
       e.stopPropagation();
       page("/policies/:id/revisions", {id: policy.id});
     }
  }

  renderControls(policy) {
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
  }

  handleShowPolicyDetail(policy) {
    return  function (e) {
      e.preventDefault();
      e.stopPropagation();
      page("/policies/:id", {id: policy.id});
    };
  }

  handleDeletePolicyDetail(policy) {
    return function (e) {
      e.preventDefault();
      e.stopPropagation();
      if (confirm(I18n.t("policies.confirmation", {policyName: policy.name}))) {
        App.Controllers.Policies.deletePolicy(policy);
      }
    };
  }

  filterPolicies(policies) {
    return policies.filter(function (policy) {
      return policy.name.toLowerCase().indexOf(this.state.search.toLowerCase()) >= 0;
    }.bind(this));
  }
}

PolicyOverview.contextTypes = {
  currentUser: React.PropTypes.object
};

export default PolicyOverview;
