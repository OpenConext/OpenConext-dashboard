import React from "react";
import I18n from "i18n-js";

import { createPolicy, updatePolicy, getPolicy, getInstitutionServiceProviders, getConnectedServiceProviders, getAllowedAttributes, getNewPolicy } from "../api";
import { setFlash } from "../utils/flash";

import Flash from "../components/flash";
import SelectWrapper from "../components/select_wrapper";
import PolicyAttributes from "../components/policy_attributes";
import PolicyDetailHelpEn from "../help/policy_detail_help_en";
import PolicyDetailHelpNl from "../help/policy_detail_help_nl";

import AutoFormat from "../utils/autoformat_policy";

class PolicyDetail extends React.Component {
  constructor() {
    super();

    this.state = {
      institutionServiceProviders: [],
      connectedServiceProviders: [],
      allowedAttributes: [],
      policy: null
    };
  }

  componentWillMount() {
    const { currentUser } = this.context;

    getInstitutionServiceProviders().then(data => this.setState({ institutionServiceProviders: data.payload }));
    getConnectedServiceProviders(currentUser.getCurrentIdpId())
      .then(data => this.setState({ connectedServiceProviders: data.payload }));
    getAllowedAttributes().then(data => this.setState({ allowedAttributes: data.payload }));

    if (this.props.params.id !== "new") {
      getPolicy(this.props.params.id).then(data => this.setState({ policy: data.payload }));
    } else {
      getNewPolicy().then(data => this.setState({ policy: data.payload }));
    }
  }

  render() {
    const { policy } = this.state;
    if (policy) {
      const title = policy.id ? I18n.t("policy_detail.update_policy") : I18n.t("policy_detail.create_policy");
      return (
        <div className="l-main">
          <Flash />
          <div className="l-grid">
            <div className="l-col-6">
              <div className="mod-policy-detail">
                <h1>{title}</h1>
                <form>
                  {this.renderName(policy)}
                  {this.renderDenyPermitRule(policy)}
                  {this.renderIdentityProvider(policy)}
                  {this.renderServiceProvider(policy)}
                  {this.renderLogicalRule(policy)}
                  {this.renderAttributes(policy)}
                  {this.renderDenyAdvice(policy)}
                  {this.renderDescription(policy)}
                  {this.renderActive(policy)}
                  {this.renderActions(policy)}
                </form>
              </div>
            </div>
            <div className="l-col-6 no-gutter">
              {this.renderHelp()}
            </div>
          </div>
        </div>
      );
    }
    return null;
  }

  renderHelp() {
    return (
      <div className="mod-policy-detail-help">
        {I18n.locale === "en" ? <PolicyDetailHelpEn/> : <PolicyDetailHelpNl/>}
      </div>
    );
  }

  renderName(policy) {
    const classNameStatus = _.isEmpty(policy.name) ? "failure" : "success";
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.name")}</p>
          <input type="text" name="name" className="form-input" value={this.state.policy.name || ""} onChange={e => this.setState({ policy: { ...this.state.policy, name: e.target.value } })}/>
        </fieldset>
      </div>
    );
  }

  renderDenyPermitRule(policy) {
    const classNameSelected = policy.denyRule ? "checked" : "";
    const classNamePermit = policy.denyRule ? "not-selected" : "";
    const classNameDeny = !policy.denyRule ? "not-selected" : "";
    const policyPermit = policy.denyRule ? I18n.t("policy_detail.deny") : I18n.t("policy_detail.permit");

    return (
      <div className="form-element">
        <fieldset className="success">
          <div className="l-grid">
            <div className="l-col-4">
              <p className="label">{I18n.t("policy_detail.access")}</p>
              <div id="ios_checkbox" className={classNameSelected + " ios-ui-select"} onClick={e => this.toggleDenyRule(e)}>
                <div className="inner"></div>
                <p>{policyPermit}</p>
              </div>
            </div>
            <div className="l-col-4">
              <p className={"info " + classNamePermit}>{I18n.t("policy_detail.permit")}</p>
              <em className={classNamePermit}>{I18n.t("policy_detail.permit_info")}</em>
            </div>
            <div className="l-col-4 no-gutter">
              <p className={"info " + classNameDeny}>{I18n.t("policy_detail.deny")}</p>
              <em className={classNameDeny}>{I18n.t("policy_detail.deny_info")}</em>
            </div>
          </div>
        </fieldset>
      </div>
    );
  }

  toggleDenyRule() {
    const partialState = { denyRule: !this.state.policy.denyRule };
    if (!this.state.policydenyRule) {
      partialState.allAttributesMustMatch = true;
    }
    partialState.description = this.buildAutoFormattedDescription(partialState);
    this.setState({ policy: { ...this.state.policy, ...partialState } });
  }

  buildAutoFormattedDescription(partialState) {
    const { policy } = this.state;
    if (policy.autoFormat) {
      this.provideProviderNames(partialState);
      //we don't want to merge the partialState and this.state before the update
      const newPolicy = {
        identityProviderNames: policy.identityProviderNames,
        serviceProviderName: policy.serviceProviderName,
        attributes: partialState.attributes || policy.attributes,
        denyRule: partialState.denyRule !== undefined ? partialState.denyRule : policy.denyRule,
        allAttributesMustMatch: partialState.allAttributesMustMatch !== undefined ? partialState.allAttributesMustMatch : policy.allAttributesMustMatch
      };
      return AutoFormat.description(newPolicy);
    }

    return this.state.description;
  }

  renderServiceProvider(policy) {
    const scopedSPs = _.isEmpty(policy.identityProviderIds);
    const classNameStatus = _.isEmpty(policy.serviceProviderId) ? "failure" : "success";
    const serviceProviders = (scopedSPs ? this.state.institutionServiceProviders : this.state.connectedServiceProviders).map(sp => {
      return { value: sp.spEntityId, display: sp.spName };
    });

    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.service")}</p>
          <SelectWrapper
            defaultValue={policy.serviceProviderId}
            placeholder={I18n.t("policy_detail.sp_placeholder")}
            options={serviceProviders}
            multiple={false}
            handleChange={this.handleChangeServiceProvider.bind(this)}/>
          {this.renderScopedWarning(scopedSPs)}
        </fieldset>
      </div>
    );
  }

  handleChangeServiceProvider(newValue) {
    const partialState = { serviceProviderId: newValue };
    partialState.description = this.buildAutoFormattedDescription(partialState);
    partialState.policyEnforcementDecisionRequired = this.findServiceProvider(newValue).policyEnforcementDecisionRequired;
    this.setState({ policy: { ...this.state.policy, ...partialState } });
  }

  renderScopedWarning(scopedSPs) {
    if (scopedSPs) {
      return (<em className="note"><sup>*</sup>{I18n.t("policy_detail.spScopeInfo")} </em>);
    }

    return null;
  }

  renderIdentityProvider(policy) {
    const { currentUser } = this.context;
    const providers = currentUser.institutionIdps.map(idp => ({ value: idp.id, display: idp.name }));
    return (
      <div className="form-element">
        <fieldset className="success">
          <p className="label">{I18n.t("policy_detail.institutions")}</p>
            <SelectWrapper
                defaultValue={policy.identityProviderIds}
                placeholder={I18n.t("policy_detail.idps_placeholder")}
                options={providers}
                multiple={true}
                handleChange={this.handleChangeIdentityProvider.bind(this)}/>
        </fieldset>
      </div>
    );
  }

  handleChangeIdentityProvider(newValue) {
    const partialState = { identityProviderIds: newValue };

    partialState.description = this.buildAutoFormattedDescription(partialState);

    this.setState({ policy: { ...this.state.policy, ...partialState } });
  }

  renderLogicalRule(policy) {
    const allAttributesMustMatch = policy.allAttributesMustMatch;
    const classNameAnd = !policy.allAttributesMustMatch ? "not-selected" : "";
    const classNameOr = policy.allAttributesMustMatch ? "not-selected" : "";

    return (
      <div className="form-element">
        <fieldset className="success">
          <div className="l-grid">
            <div className="l-col-4">
              <p className="label">{I18n.t("policy_detail.rule")}</p>
              <ul className="logical-rule">
                {[
                  this.renderRule(I18n.t("policy_detail.rule_and"), allAttributesMustMatch),
                  this.renderRule(I18n.t("policy_detail.rule_or"), !allAttributesMustMatch)
                ]}
              </ul>
            </div>
            <div className="l-col-4">
              <p className={"info " + classNameAnd}>{I18n.t("policy_detail.rule_and")}</p>
              <em className={classNameAnd}>{I18n.t("policy_detail.rule_and_info")}</em>
            </div>
            <div className="l-col-4 no-gutter">
              <p className={"info " + classNameOr}>{I18n.t("policy_detail.rule_or")}</p>
              <em className={classNameOr}>{I18n.t("policy_detail.rule_or_info")}</em>
            </div>
          </div>
        </fieldset>
      </div>
    );
  }

  renderRule(value, selected) {
    const className = value + " " + (selected ? "selected" : "");

    if (this.state.policy.denyRule) {
      return (
        <li key={value}>
          <span className={className}>{value}</span>
        </li>
      );
    } 

    return (
      <li key={value}>
        <a href="#" className={className} onClick={this.handleChooseRule(value)}>{value}</a>
      </li>
    );
  }

  handleChooseRule(value) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      const allAttributesMustMatch = (value === I18n.t("policy_detail.rule_and"));
      const partialState = { allAttributesMustMatch: allAttributesMustMatch };
      partialState.description = this.buildAutoFormattedDescription(partialState);
      this.setState({ policy: { ...this.state.policy, ...partialState } });
    }.bind(this);
  }

  renderAttributes() {
    return (<PolicyAttributes
        policy={this.state.policy}
        allowedAttributes={this.state.allowedAttributes}
        setAttributeState={this.setAttributeState.bind(this)}/>);
  }

  setAttributeState(newAttributeState) {
    newAttributeState.description = this.buildAutoFormattedDescription(newAttributeState);
    this.setState({ policy: { ...this.state.policy, ...newAttributeState } });
  }

  renderDenyAdvice(policy)  {
    const classNameStatus = _.isEmpty(policy.denyAdvice) || _.isEmpty(policy.denyAdviceNl) ? "failure" : "success";
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.deny_message")}</p>
          <em>{I18n.t("policy_detail.deny_message_info")}</em>
          <input type="text" name="denyMessage" className="form-input"
            value={this.state.policy.denyAdvice || ""} onChange={e => this.setState({ policy: { ...this.state.policy, denyAdvice: e.target. value } })} />
          <p className="label">{I18n.t("policy_detail.deny_message_nl")}</p>
          <input type="text" name="denyMessageNl" className="form-input"
            value={this.state.policy.denyAdviceNl || ""} onChange={e => this.setState({ policy: { ...this.state.policy, denyAdviceNl: e.target.value } })} />
        </fieldset>
      </div>
    );
  }

  renderDescription(policy) {
    const classNameStatus = _.isEmpty(policy.description) ? "failure" : "success";
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.description")}</p>
          <textarea rows="4" name="description" value={this.state.policy.description || ""} className="form-input" onChange={e => this.setState({ policy: { ...this.state.policy, description: e.target.value } })} />
          <input type="checkbox" id="autoFormatDescription" name="autoFormatDescription"
            onChange={this.handleOnChangeAutoFormat.bind(this)}/>
          <label className="note" htmlFor="autoFormatDescription">{I18n.t("policy_detail.autoFormat")}</label>
        </fieldset>
      </div>
    );
  }

  handleOnChangeAutoFormat() {
    const { policy } = this.state;
    const partialState = { autoFormat: !policy.policyautoFormat };
    if (partialState.autoFormat) {
      partialState.savedDescription = policy.description;
      this.provideProviderNames(partialState);
      partialState.description = AutoFormat.description(policy);
    } else {
      partialState.description = policy.savedDescription || "";
    }
    this.setState({ policy: { ...this.state.policy, ...partialState } });
  }

  provideProviderNames(partialState) {
    const { currentUser } = this.context;
    const identityProviderIds = _.isUndefined(partialState.identityProviderIds) ? this.state.policy.identityProviderIds : partialState.identityProviderIds;

    if (_.isEmpty(identityProviderIds)) {
      this.setState({ policy: { ...this.state.policy, identityProviderNames: [] } });
    } else {
      this.setState({ policy: { ...this.state.policy, identityProviderNames: identityProviderIds.map(idp => {
        const provider = _.find(currentUser.institutionIdps, provider => provider.id === idp);
        return provider.name;
      }) } });
    }

    const serviceProviderId = _.isUndefined(partialState.serviceProviderId) ? this.state.policy.serviceProviderId : partialState.serviceProviderId;
    if (_.isEmpty(serviceProviderId)) {
      this.setState({ policy: { ...this.state.policy, serviceProviderName: null } });
    } else {
      const scopedSPs = _.isEmpty(identityProviderIds);
      const serviceProvider = _.find(scopedSPs ? this.state.institutionServiceProviders : this.state.connectedServiceProviders, sp => {
        return sp.spEntityId === serviceProviderId;
      });
      this.setState({ policy: { ...this.state.policy, serviceProviderName: serviceProvider.name } });
    }
  }

  findServiceProvider(serviceProviderId) {
    const scopedSPs = _.isEmpty(this.state.policy.identityProviderIds);
    return _.find(scopedSPs ? this.state.institutionServiceProviders : this.state.connectedServiceProviders, sp => {
      return sp.spEntityId === serviceProviderId;
    });
  }

  renderActive(policy) {
    return (
      <div className="form-element">
        <fieldset className="success">
          <p className="label">{I18n.t("policy_detail.isActive")}</p>
          <input type="checkbox" id="isActive" name="isActive" checked={policy.active}
                 onChange={this.handleOnChangeIsActive.bind(this)}/>
          <label htmlFor="isActive">{I18n.t("policy_detail.isActiveDescription")}</label>
          <em className="note"><sup>*</sup>{I18n.t("policy_detail.isActiveInfo")} </em>
        </fieldset>
      </div>
    );
  }

  handleOnChangeIsActive() {
    this.setState({ policy: { ...this.state.policy, active: !this.state.policy.active } });
  }

  renderActions() {
    const classNameSubmit = this.isValidPolicy() ? "" : "disabled";
    return (
      <div className="form-element">
        <fieldset>
          <div className="l-grid">
            <div className="l-col-3">
              <a className={"c-button " + classNameSubmit} href="#" onClick={this.submitForm.bind(this)}>{I18n.t("policy_detail.submit")}</a>
            </div>
            <div className="l-col-3">
              <a className="n-button" href="#" onClick={this.cancelForm.bind(this)}>{I18n.t("policy_detail.cancel")}</a>
            </div>
          </div>
        </fieldset>
      </div>
    );
  }

  isValidPolicy() {
    const { policy } = this.state;
    const emptyAttributes = policy.attributes.filter(attr => {
      return _.isEmpty(attr.value);
    });
    const inValid = _.isEmpty(policy.name) || _.isEmpty(policy.description) || _.isEmpty(policy.serviceProviderId)
        || _.isEmpty(policy.attributes) || emptyAttributes.length > 0 || _.isEmpty(policy.denyAdvice) || _.isEmpty(policy.denyAdviceNl);
    return !inValid;
  }

  submitForm() {
    const { policy } = this.state;

    const apiCall = policy.id ? updatePolicy : createPolicy;
    const action = policy.id ? I18n.t("policies.flash_updates") : I18n.t("policies.flash_created");

    apiCall(policy).then(() => {
      if (policy.policyEnforcementDecisionRequired) {
        setFlash(I18n.t("policies.flash", { policyName: policy.name, action }));
      } else {
        setFlash(I18n.t("policies.flash_first"));
      }
      this.context.router.transitionTo("/policies");
    })
    .catch(e => {
      setFlash(e, "error");
    });
  }

  cancelForm() {
    if (confirm(I18n.t("policy_detail.confirmation"))) {
      this.context.router.transitionTo("/policies");
    }
  }
}

PolicyDetail.contextTypes = {
  currentUser: React.PropTypes.object,
  router: React.PropTypes.object
};

PolicyDetail.propTypes = {
  params: React.PropTypes.shape({
    id: React.PropTypes.string
  })
};

export default PolicyDetail;
