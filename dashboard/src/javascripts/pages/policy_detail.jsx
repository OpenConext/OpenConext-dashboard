import React from "react";
import I18n from "../lib/i18n";

import { getInstitutionServiceProviders, getConnectedServiceProviders, getAllowedAttributes, getNewPolicy } from "../api";

import Select2Selector from "../components/select2_selector";
import PolicyAttributes from "../components/policy_attributes";
import PolicyDetailHelpEn from "../help/policy_detail_help_en";
import PolicyDetailHelpNl from "../help/policy_detail_help_nl";

  // mixins: [React.addons.LinkedStateMixin],
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

    getNewPolicy().then(data => this.setState({ policy: data.payload }));
    getInstitutionServiceProviders().then(data => this.setState({ institutionServiceProviders: data.payload }));
    getConnectedServiceProviders(currentUser.getCurrentIdpId())
      .then(data => this.setState({ connectedServiceProviders: data.payload }));
    getAllowedAttributes().then(data => this.setState({ allowedAttributes: data.payload }));
  }

  render() {
    const { policy } = this.state;
    if (policy) {
      var title = policy.id ? I18n.t("policy_detail.update_policy") : I18n.t("policy_detail.create_policy");
      return (
        <div className="l-main">
          {this.renderFlash()}
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

  renderFlash() {
    var flash = this.state.flash;

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

  renderHelp() {
    return (
      <div className="mod-policy-detail-help">
        {I18n.locale === "en" ? <PolicyDetailHelpEn/> : <PolicyDetailHelpNl/>}
      </div>
    );
  }

  renderName(policy) {
    var classNameStatus = _.isEmpty(policy.name) ? "failure" : "success";
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.name")}</p>
          <input type="text" name="name" className="form-input" value={this.state.name} onChange={e => this.setState({ name: e.target.value })}/>
        </fieldset>
      </div>
    );
  }

  renderDenyPermitRule(policy) {
    var classNameSelected = policy.denyRule ? "checked" : "";
    var classNamePermit = policy.denyRule ? "not-selected" : "";
    var classNameDeny = !policy.denyRule ? "not-selected" : "";
    var policyPermit = policy.denyRule ? I18n.t("policy_detail.deny") : I18n.t("policy_detail.permit");

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

  toggleDenyRule(e) {
    var partialState = {denyRule: !this.state.denyRule};
    if (!this.state.denyRule) {
      partialState.allAttributesMustMatch = true;
    }
    partialState.description = this.buildAutoFormattedDescription(partialState);
    this.setState({...this.state, ...partialState});
  }

  buildAutoFormattedDescription(partialState) {
    if (this.state.autoFormat) {
      this.provideProviderNames(partialState);
      //we don't want to merge the partialState and this.state before the update
      var policy = {
        identityProviderNames: this.state.identityProviderNames,
        serviceProviderName: this.state.serviceProviderName,
        attributes: partialState.attributes || this.state.attributes,
        denyRule: partialState.denyRule !== undefined ? partialState.denyRule : this.state.denyRule,
        allAttributesMustMatch: partialState.allAttributesMustMatch !== undefined ? partialState.allAttributesMustMatch : this.state.allAttributesMustMatch
      }
      return App.Utils.AutoFormat.description(policy);
    } else {
      return this.state.description;
    }
  }

  renderServiceProvider(policy) {
    var scopedSPs = _.isEmpty(policy.identityProviderIds);
    var classNameStatus = _.isEmpty(policy.serviceProviderId) ? "failure" : "success";
    var serviceProviders = (scopedSPs ? this.state.institutionServiceProviders : this.state.connectedServiceProviders).map(function (sp) {
      return {value: sp.spEntityId, display: sp.spName};
    });

    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.service")}</p>
          <Select2Selector
            defaultValue={policy.serviceProviderId}
            placeholder={I18n.t("policy_detail.sp_placeholder")}
            select2selectorId="serviceProvider"
            options={serviceProviders}
            multiple={false}
            handleChange={this.handleChangeServiceProvider}/>
          {this.renderScopedWarning(scopedSPs)}
        </fieldset>
      </div>
    );
  }

  handleChangeServiceProvider(newValue) {
    var partialState = {serviceProviderId: newValue};
    partialState.description = this.buildAutoFormattedDescription(partialState);
    partialState.policyEnforcementDecisionRequired = this.findServiceProvider(newValue).policyEnforcementDecisionRequired;
    this.setState(partialState);
  }

  renderScopedWarning(scopedSPs) {
    if (scopedSPs) {
      return (<em className="note"><sup>*</sup>{I18n.t("policy_detail.spScopeInfo")} </em>);
    }
  }

  renderIdentityProvider(policy) {
    const { currentUser } = this.context;
    var providers = currentUser.institutionIdps.map(function (idp) { return { value: idp.id, display: idp.name }});
    return (
      <div className="form-element">
        <fieldset className="success">
          <p className="label">{I18n.t("policy_detail.institutions")}</p>
            <Select2Selector
                defaultValue={policy.identityProviderIds}
                placeholder={I18n.t("policy_detail.idps_placeholder")}
                select2selectorId={"identityProvider"}
                options={providers}
                multiple={true}
                handleChange={this.handleChangeIdentityProvider}/>
        </fieldset>
      </div>
    );
  }

  handleChangeIdentityProvider(newValue) {
    var partialState = {identityProviderIds: newValue};

    var noIdpSelected = _.isEmpty(newValue);

    if (noIdpSelected) {
      var serviceProviders = this.props.institutionServiceProviders.map(function (sp) {
        return {value: sp.spEntityId, display: sp.spName};
      });

      if (this.state.serviceProviderId && !_.some(serviceProviders, function (sp) { return sp.value === this.state.serviceProviderId; }.bind(this))) {
        //Unfortunately we have to set the current value manually as the integration with select2 is done one-way
        var select2ServiceProvider = $('[data-select2selector-id="serviceProvider"]');
        select2ServiceProvider.val("").trigger("change");
      }
    }

    partialState.description = this.buildAutoFormattedDescription(partialState);

    this.setState(partialState);
  }

  renderLogicalRule(policy) {
    var allAttributesMustMatch = policy.allAttributesMustMatch;
    var classNameAnd = !policy.allAttributesMustMatch ? "not-selected" : "";
    var classNameOr = policy.allAttributesMustMatch ? "not-selected" : "";

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
    var className = value + " " + (selected ? "selected" : "");
    if (this.state.denyRule) {
      return (
        <li key={value}>
          <span className={className}>{value}</span>
        </li>
      );
    } else {
      return (
        <li key={value}>
          <a href="#" className={className} onClick={this.handleChooseRule(value)}>{value}</a>
        </li>
      );
    }
  }

  handleChooseRule(value) {
    return function (e) {
      e.preventDefault();
      e.stopPropagation();
      var allAttributesMustMatch = (value === I18n.t("policy_detail.rule_and"));
      var partialState = {allAttributesMustMatch: allAttributesMustMatch};
      partialState.description = this.buildAutoFormattedDescription(partialState);
      this.setState(partialState);
    }.bind(this);
  }

  renderAttributes(policy) {
    return (<PolicyAttributes
        policy={this.state.policy}
        allowedAttributes={this.state.allowedAttributes}
        setAttributeState={this.setAttributeState}/>);
  }

  setAttributeState(newAttributeState) {
    newAttributeState.description = this.buildAutoFormattedDescription(newAttributeState);
    this.setState(newAttributeState);
  }

  renderDenyAdvice(policy)  {
    var classNameStatus = _.isEmpty(policy.denyAdvice) || _.isEmpty(policy.denyAdviceNl) ? "failure" : "success";
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.deny_message")}</p>
          <em>{I18n.t("policy_detail.deny_message_info")}</em>
          <input type="text" name="denyMessage" className="form-input"
            value={this.state.denyAdvice} onChange={e => this.setState({ denyAdvice: e.target. value })} />
          <p className="label">{I18n.t("policy_detail.deny_message_nl")}</p>
          <input type="text" name="denyMessageNl" className="form-input"
            value={this.state.denyAdviceNl} onChange={e => this.setState({ denyAdviceNl: e.target.value})} />
        </fieldset>
      </div>
    );
  }

  renderDescription(policy) {
    var classNameStatus = _.isEmpty(policy.description) ? "failure" : "success";
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.description")}</p>
          <textarea rows="4" name="description" value={this.state.description} className="form-input" onChange={e => this.setState({ description: e.target.value })} />
          <input type="checkbox" id="autoFormatDescription" name="autoFormatDescription"
            onChange={this.handleOnChangeAutoFormat}/>
          <label className="note" htmlFor="autoFormatDescription">{I18n.t("policy_detail.autoFormat")}</label>
        </fieldset>
      </div>
    );
  }

  handleOnChangeAutoFormat(e) {
    var partialState = {autoFormat: !this.state.autoFormat};
    if (partialState.autoFormat) {
      partialState.savedDescription = this.state.description;
      this.provideProviderNames(partialState);
      partialState.description = App.Utils.AutoFormat.description(this.state);
    } else {
      partialState.description = this.state.savedDescription || "";
    }
    this.setState(partialState);
  }

  provideProviderNames(partialState) {
    var identityProviderIds = _.isUndefined(partialState.identityProviderIds) ? this.state.identityProviderIds : partialState.identityProviderIds;

    if (_.isEmpty(identityProviderIds)) {
      this.state.identityProviderNames = [];
    } else {
      this.state.identityProviderNames = identityProviderIds.map(function (idp) {
        var provider = _.find(this.props.identityProviders, function (provider) { return provider.id === idp; });
        return provider.name;
      }.bind(this));
    }

    var serviceProviderId = _.isUndefined(partialState.serviceProviderId) ? this.state.serviceProviderId : partialState.serviceProviderId;
    if (_.isEmpty(serviceProviderId)) {
      this.state.serviceProviderName = null;
    } else {
      var scopedSPs = _.isEmpty(identityProviderIds);
      var serviceProvider = _.find(scopedSPs ? this.props.institutionServiceProviders : this.props.connectedServiceProviders, function (sp) {
        return sp.spEntityId === serviceProviderId;
      });
      this.state.serviceProviderName = serviceProvider.name;
    }
  }

  findServiceProvider(serviceProviderId) {
      var scopedSPs = _.isEmpty(this.state.identityProviderIds);
      return _.find(scopedSPs ? this.props.institutionServiceProviders : this.props.connectedServiceProviders, function (sp) {
        return sp.spEntityId === serviceProviderId;
      });
  }

  renderActive(policy) {
    return (
      <div className="form-element">
        <fieldset className="success">
          <p className="label">{I18n.t("policy_detail.isActive")}</p>
          <input type="checkbox" id="isActive" name="isActive" checked={policy.active}
                 onChange={this.handleOnChangeIsActive}/>
          <label htmlFor="isActive">{I18n.t("policy_detail.isActiveDescription")}</label>
          <em className="note"><sup>*</sup>{I18n.t("policy_detail.isActiveInfo")} </em>
        </fieldset>
      </div>
    );
  }

  handleOnChangeIsActive(e) {
    this.setState({active: !this.state.active});
  }

  renderActions(policy) {
    var classNameSubmit = this.isValidPolicy() ? "" : "disabled";
    return (
      <div className="form-element">
        <fieldset>
          <div className="l-grid">
            <div className="l-col-3">
              <a className={"c-button " + classNameSubmit} href="#" onClick={this.submitForm}>{I18n.t("policy_detail.submit")}</a>
            </div>
            <div className="l-col-3">
              <a className="n-button" href="#" onClick={this.cancelForm}>{I18n.t("policy_detail.cancel")}</a>
            </div>
          </div>
        </fieldset>
      </div>
    );
  }

  isValidPolicy() {
    const { policy } = this.state;
    var emptyAttributes = policy.attributes.filter(function (attr) {
      return _.isEmpty(attr.value);
    });
    var inValid = _.isEmpty(policy.name) || _.isEmpty(policy.description) || _.isEmpty(policy.serviceProviderId)
        || _.isEmpty(policy.attributes) || emptyAttributes.length > 0 || _.isEmpty(policy.denyAdvice) || _.isEmpty(policy.denyAdviceNl);
    return !inValid;
  }

  submitForm() {
    App.Controllers.Policies.saveOrUpdatePolicy(this.state, function (jqxhr) {
      jqxhr.isConsumed = true;
      this.setState({flash: { type: "error", message: jqxhr.responseJSON.message}});
    }.bind(this));
  }

  cancelForm() {
    if (confirm(I18n.t("policy_detail.confirmation"))) {
      page("/policies");
    }
  }
}

PolicyDetail.contextTypes = {
  currentUser: React.PropTypes.object
};

export default PolicyDetail;
