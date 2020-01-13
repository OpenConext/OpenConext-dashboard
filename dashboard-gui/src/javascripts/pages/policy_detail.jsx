import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import ReactTooltip from "react-tooltip";
import isEmpty from "lodash.isempty";
import find from "lodash.find";
import {withRouter} from 'react-router';
import {
    createPolicy,
    getAllowedAttributes,
    getConnectedServiceProviders,
    getInstitutionServiceProviders,
    getNewPolicy,
    getPolicy,
    updatePolicy
} from "../api";
import {setFlash} from "../utils/flash";

import Flash from "../components/flash";
import SelectWrapper from "../components/select_wrapper";
import PolicyAttributes from "../components/policy_attributes";
import PolicyDetailHelpEn from "../help/policy_detail_help_en";
import PolicyDetailHelpNl from "../help/policy_detail_help_nl";
import PolicyDetailHelpPt from "../help/policy_detail_help_pt";

import AutoFormat from "../utils/autoformat_policy";
import stopEvent from "../utils/stop";

class PolicyDetail extends React.Component {
    state = {
        autoFormat: false,
        allowedAttributes: [],
        connectedServiceProviders: [],
        institutionServiceProviders: [],
        policy: null,
        loaded: false
    };

    componentDidMount() {
        const {currentUser} = this.context;
        const promises = [
            getInstitutionServiceProviders(),
            getConnectedServiceProviders(currentUser.getCurrentIdpId()),
            getAllowedAttributes()
        ];
        if (this.props.match.params.id !== "new") {
            promises.push(getPolicy(this.props.match.params.id));
        } else {
            promises.push(getNewPolicy());
        }
        Promise.all(promises).then(res => {
            this.setState({
                institutionServiceProviders: res[0].payload,
                connectedServiceProviders: res[1].payload,
                allowedAttributes: res[2].payload,
                policy: res[3].payload,
                loaded: true
            });
        });
        // getInstitutionServiceProviders().then(data => this.setState({institutionServiceProviders: data.payload}));
        // getConnectedServiceProviders(currentUser.getCurrentIdpId())
        //     .then(data => this.setState({connectedServiceProviders: data.payload}));
        // getAllowedAttributes().then(data => this.setState({allowedAttributes: data.payload}));
        //
        // if (this.props.match.params.id !== "new") {
        //     getPolicy(this.props.match.params.id).then(data => this.setState({policy: data.payload}));
        // } else {
        //     getNewPolicy().then(data => this.setState({policy: data.payload}));
        // }
    }

    render() {
        const {policy, loaded} = this.state;
        if (policy && loaded) {
            const title = policy.id ? I18n.t("policy_detail.update_policy") : I18n.t("policy_detail.create_policy");
            return (
                <div className="l-main">
                    <Flash/>
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
                {I18n.locale === "en" ? <PolicyDetailHelpEn/> : I18n.locale === "pt" ? <PolicyDetailHelpPt/> : <PolicyDetailHelpNl/>}
            </div>
        );
    }

    renderName(policy) {
        const classNameStatus = isEmpty(policy.name) ? "failure" : "success";
        return (
            <div className="form-element">
                <fieldset className={classNameStatus}>
                    <p className="label">{I18n.t("policy_detail.name")}</p>
                    <input type="text" name="name" className="form-input" value={this.state.policy.name || ""}
                           onChange={this.handleOnChangeName.bind(this)}/>
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
                            <div id="ios_checkbox" className={classNameSelected + " ios-ui-select"}
                                 onClick={e => this.toggleDenyRule(e)}>
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
        const partialState = {denyRule: !this.state.policy.denyRule};
        if (!this.state.policydenyRule) {
            partialState.allAttributesMustMatch = true;
        }
        this.setState({policy: {...this.state.policy, ...partialState}});
    }

    renderAutoformatDescription(policy) {
        if (this.state.autoFormat) {
            return AutoFormat.description(policy);
        }

        return policy.description || "";
    }

    renderServiceProvider(policy) {
        const scopedSPs = isEmpty(policy.identityProviderIds);
        const classNameStatus = isEmpty(policy.serviceProviderId) ? "failure" : "success";
        const serviceProviders = (scopedSPs ? this.state.institutionServiceProviders : this.state.connectedServiceProviders).map(sp => {
            return {value: sp.spEntityId, display: sp.spName};
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

    handleChangeServiceProvider(newValue, newLabel) {
        this.setState({
            policy: {
                ...this.state.policy,
                serviceProviderId: newValue,
                serviceProviderName: newLabel
            }
        });
    }

    renderScopedWarning(scopedSPs) {
        if (scopedSPs) {
            return (<em className="note"><sup>*</sup>{I18n.t("policy_detail.spScopeInfo")} </em>);
        }

        return null;
    }

    renderIdentityProvider(policy) {
        const {currentUser} = this.context;
        const providers = currentUser.institutionIdps.map(idp => ({value: idp.id, display: idp.name}));
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
        this.setState({policy: {...this.state.policy, identityProviderIds: newValue}});
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
                <a href="/rule" className={className} onClick={this.handleChooseRule(value)}>{value}</a>
            </li>
        );
    }

    handleOnChangeDescription(e) {
        this.setState({policy: {...this.state.policy, description: e.target.value}});
    }

    handleOnChangeAutoFormat() {
        this.setState({autoFormat: !this.state.autoFormat});
    }

    handleOnChangeName(e) {
        this.setState({policy: {...this.state.policy, name: e.target.value}});
    }

    handleChooseRule(value) {
        return function (e) {
            stopEvent(e);
            const allAttributesMustMatch = (value === I18n.t("policy_detail.rule_and"));
            this.setState({policy: {...this.state.policy, allAttributesMustMatch}});
        }.bind(this);
    }

    renderAttributes() {
        const {policy, allowedAttributes} = this.state;
        return (<PolicyAttributes
            policy={policy}
            allowedAttributes={allowedAttributes}
            setAttributeState={this.setAttributeState.bind(this)}/>);
    }

    setAttributeState(newAttributeState) {
        this.setState({policy: {...this.state.policy, ...newAttributeState}});
    }

    renderDenyAdvice(policy) {
        const classNameStatus = isEmpty(policy.denyAdvice) || isEmpty(policy.denyAdvicePt) || isEmpty(policy.denyAdviceNl) ? "failure" : "success";
        return (
            <div className="form-element">
                <fieldset className={classNameStatus}>
                    <p className="label">{I18n.t("policy_detail.deny_message")}</p>
                    <em>{I18n.t("policy_detail.deny_message_info")}</em>
                    <input type="text" name="denyMessage" className="form-input"
                           value={this.state.policy.denyAdvice || ""}
                           onChange={e => this.setState({policy: {...this.state.policy, denyAdvice: e.target.value}})}/>
                    <p className="label">{I18n.t("policy_detail.deny_message_nl")}</p>
                    <input type="text" name="denyMessageNl" className="form-input"
                           value={this.state.policy.denyAdviceNl || ""}
                           onChange={e => this.setState({policy: {...this.state.policy, denyAdviceNl: e.target.value}})}/>
                    <p className="label">{I18n.t("policy_detail.deny_message_pt")}</p>
                    <input type="text" name="denyMessagePt" className="form-input"
                           value={this.state.policy.denyAdvicePt || ""}
                           onChange={e => this.setState({policy: {...this.state.policy, denyAdvicePt: e.target.value}})}/>
                </fieldset>
            </div>
        );
    }

    renderDescription(policy) {
        const description = this.renderAutoformatDescription(policy);
        const classNameStatus = isEmpty(description) ? "failure" : "success";
        return (
            <div className="form-element">
                <fieldset className={classNameStatus}>
                    <p className="label">{I18n.t("policy_detail.description")}</p>
                    <textarea rows="4" name="description"
                              value={description} className="form-input"
                              onChange={this.handleOnChangeDescription.bind(this)}/>
                    <input type="checkbox" id="autoFormatDescription" name="autoFormatDescription"
                           onChange={this.handleOnChangeAutoFormat.bind(this)}/>
                    <label className="note" htmlFor="autoFormatDescription">{I18n.t("policy_detail.autoFormat")}</label>
                </fieldset>
            </div>
        );
    }

    findServiceProvider = (serviceProviderId) => {
        const scopedSPs = isEmpty(this.state.policy.identityProviderIds);
        return find(scopedSPs ? this.state.institutionServiceProviders : this.state.connectedServiceProviders, sp => {
            return sp.spEntityId === serviceProviderId;
        });
    };

    renderActive(policy) {
        return (
            <div className="form-element">
                <fieldset className="success">
                    <p className="label">{I18n.t("policy_detail.isActive")}</p>
                    <input type="checkbox" id="isActive" name="isActive" checked={policy.active}
                           onChange={this.handleOnChangeIsActive.bind(this)}/>
                    <label htmlFor="isActive">{I18n.t("policy_detail.isActiveDescription")}
                        <a className="help-link" target="_blank" rel="noopener noreferrer" href={I18n.t("policies.pdp_active_link")}>
                            <i className="fa fa-question-circle" data-for="pdp_active_info" data-tip/>
                            <ReactTooltip id="pdp_active_info" type="info" class="tool-tip" effect="solid">
                                {I18n.t("policies.pdp_active_info")}
                            </ReactTooltip>
                        </a>
                    </label>
                    <em className="note"><sup>*</sup>{I18n.t("policy_detail.isActiveInfo")} </em>
                </fieldset>
            </div>
        );
    }

    handleOnChangeIsActive() {
        this.setState({policy: {...this.state.policy, active: !this.state.policy.active}});
    }

    renderActions() {
        const classNameSubmit = this.isValidPolicy() ? "" : "disabled";
        return (
            <div className="form-element">
                <fieldset>
                    <div className="l-grid">
                        <div className="l-col-3">
                            <a className={"c-button " + classNameSubmit} href="/submit"
                               onClick={this.submitForm}>{I18n.t("policy_detail.submit")}</a>
                        </div>
                        <div className="l-col-3">
                            <a className="n-button" href="/cancel"
                               onClick={this.cancelForm}>{I18n.t("policy_detail.cancel")}</a>
                        </div>
                    </div>
                </fieldset>
            </div>
        );
    }

    isValidPolicy() {
        const {policy} = this.state;
        const emptyAttributes = policy.attributes.filter(attr => {
            return isEmpty(attr.value);
        });
        const description = this.renderAutoformatDescription(policy);
        const inValid = isEmpty(policy.name) || isEmpty(description) || isEmpty(policy.serviceProviderId)
            || isEmpty(policy.attributes) || emptyAttributes.length > 0 || isEmpty(policy.denyAdvice) || isEmpty(policy.denyAdvicePt) || isEmpty(policy.denyAdviceNl);
        return !inValid;
    }

    submitForm = e => {
        stopEvent(e);
        const {policy} = this.state;
        policy.description = this.renderAutoformatDescription(policy);
        const policyEnforcementDecisionRequired = this.findServiceProvider(policy.serviceProviderId).policyEnforcementDecisionRequired;
        const apiCall = policy.id ? updatePolicy : createPolicy;
        const action = policy.id ? I18n.t("policies.flash_updated") : I18n.t("policies.flash_created");
        apiCall(policy).then(() => {
            if (policyEnforcementDecisionRequired) {
                setFlash(I18n.t("policies.flash", {policyName: policy.name, action}));
            } else {
                setFlash(I18n.t("policies.flash_first"));
            }
            this.props.history.replace("/policies");
        })
            .catch(e => {
                if (e.response && e.response.json) {
                    e.response.json().then(json => {
                        let message = "error";
                        if (json.exception && json.exception.indexOf("PolicyNameNotUniqueException") > 0) {
                            message = I18n.t("policies.policy_name_not_unique_exception");
                        }
                        setFlash(message, "error");
                    });
                } else {
                    setFlash(e, "error");
                }

            });
    };

    cancelForm = e => {
        stopEvent(e);
        if (window.confirm(I18n.t("policy_detail.confirmation"))) {
            this.props.history.replace("/policies");
        }
    }
}

PolicyDetail.contextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object
};

PolicyDetail.propTypes = {
    params: PropTypes.shape({
        id: PropTypes.string
    })
};

export default withRouter(PolicyDetail);
