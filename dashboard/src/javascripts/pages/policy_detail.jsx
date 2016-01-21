/** @jsx React.DOM */

App.Pages.PolicyDetail = React.createClass({
  render: function () {
    return (
      <div className="l-grid main">
        <div className="l-col-6">
          <div className="mod-policy-detail">
            <h2>{I18n.t("policy_detail.title")}</h2>
            {this.renderName()}
            {this.renderDenyPermitRule()}
            {this.renderServiceProvider()}
            {this.renderIdentityProvider()}
            {this.renderLogicalRule()}
            {this.renderAttributes()}
            {this.renderDenyAdvice()}
            {this.renderDescription()}
            {this.renderActions()}
          </div>
        </div>
        <div className="l-col-6">
          {this.renderHelp()}
        </div>
      </div>
    );
  },

  renderHelp: function () {
    return (
      <div className="mod-policy-detail-help">
        {I18n.locale === "en" ? <App.Help.PolicyDetailHelpEn/> : <App.Help.PolicyDetailHelpNl/>}
      </div>
    );
  },

  renderName: function () {
    var classNameStatus = "success";
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.name")}</p>
          <input type="text" name="name" className="form-input" value="" onChange={this.handleOnChangeName}/>
        </fieldset>
      </div>
    );
  },

  handleOnChangeName: function () {
  },

  renderDenyPermitRule: function () {
    //var classNameSelected = policy.denyRule ? "checked" : "";
    var classNameSelected = true ? "checked" : "";
    //var classNamePermit = policy.denyRule ? "not-selected" : "";
    var classNamePermit = true ? "not-selected" : "";
    //var classNameDeny = !policy.denyRule ? "not-selected" : "";
    var classNameDeny = true ? "not-selected" : "";
    //var policyPermit = policy.denyRule ? I18n.t("policy_detail.deny") : I18n.t("policy_detail.permit");
    var policyPermit = true ? I18n.t("policy_detail.deny") : I18n.t("policy_detail.permit");

    return (
      <div className="form-element">
        <fieldset className="success">
          <div className="l-grid">
            <div className="l-col-4">
              <p className="label">{I18n.t("policy_detail.access")}</p>
              <div id="ios_checkbox" className={classNameSelected + " ios-ui-select"} onClick={this.toggleDenyRule}>
                <div className="inner"></div>
                <p>{policyPermit}</p>
              </div>
            </div>
            <div className="l-col-4">
              <p className={"info " + classNamePermit}>{I18n.t("policy_detail.permit")}</p>
              <em className={classNamePermit}>{I18n.t("policy_detail.permit_info")}</em>
            </div>
            <div className="l-col-4">
              <p className={"info "+classNameDeny}>{I18n.t("policy_detail.deny")}</p>
              <em className={classNameDeny}>{I18n.t("policy_detail.deny_info")}</em>
            </div>
          </div>
        </fieldset>
      </div>
    );
  },

  toggleDenyRule: function () {
  },

  renderServiceProvider: function () {
    var classNameStatus = "failure";
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.service")}</p>
          {/*<App.Components.Select2Selector
            defaultValue={policy.serviceProviderId}
            placeholder={I18n.t("policy_detail.sp_placeholder")}
            select2selectorId={"serviceProvider"}
            options={serviceProviders}
            dataChanged={policy.spDataChanged}
            handleChange={this.handleChangeServiceProvider}/>*/}
          {this.renderScopedWarning([])}
        </fieldset>
      </div>
    );
  },

  renderScopedWarning: function (scopedSPs) {
  },

  renderIdentityProvider: function () {
    var classNameStatus = "failure";
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.institutions")}</p>
        </fieldset>
      </div>
    );
  },

  renderLogicalRule: function () {
    var classNameStatus = "failure";
    //var classNameAnd = !policy.allAttributesMustMatch ? "not-selected" : "";
    var classNameAnd = true ? "not-selected" : "";
    //var classNameOr = policy.allAttributesMustMatch ? "not-selected" : "";
    var classNameOr = true ? "not-selected" : "";

    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <div className="l-grid">
            <div className="l-col-4">
              <p className="label">{I18n.t("policy_detail.rule")}</p>
              <ul className="logical-rule">
                {[
                  this.renderRule(I18n.t("policy_detail.rule_and"), true),
                  this.renderRule(I18n.t("policy_detail.rule_or"), !true)
                ]}
              </ul>
            </div>
            <div className="l-col-4">
              <p className={"info "+classNameAnd}>{I18n.t("policy_detail.rule_and")}</p>
              <em className={classNameAnd}>{I18n.t("policy_detail.rule_and_info")}</em>
            </div>
            <div className="l-col-4">
              <p className={"info "+classNameOr}>{I18n.t("policy_detail.rule_or")}</p>
              <em className={classNameOr}>{I18n.t("policy_detail.rule_or_info")}</em>
            </div>
          </div>
        </fieldset>
      </div>
    );
  },

  renderRule: function (value, selected) {
    var className = value + " " + (selected ? "selected" : "");
    //if (this.state.denyRule) {
    if (true) {
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
  },

  renderAttributes: function () {
    var classNameStatus = "failure";
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.attribute")}</p>
        </fieldset>
      </div>
    );
  },

  renderDenyAdvice: function ()  {
    var classNameStatus = "failure";
    var policy = {denyAdvice: "todo", denyAdivceNl: "todo"};
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.deny_message")}</p>
          <em>{I18n.t("policy_detail.deny_message_info")}</em>
          <input type="text" name="denyMessage" className="form-input" value={policy.denyAdvice}
                 onChange={this.handleOnDenyAdvice}/>

          <p className="label">{I18n.t("policy_detail.deny_message_nl")}</p>
          <input type="text" name="denyMessageNl" className="form-input" value={policy.denyAdviceNl}
                 onChange={this.handleOnDenyAdviceNl}/>
        </fieldset>
      </div>
    );
  },

  handleOnDenyAdvice: function () {
  },

  handleOnDenyAdviceNL: function () {
  },

  renderDescription: function () {
    var classNameStatus = "failure";
    var policy = {description: ""};
    return (
      <div className="form-element">
        <fieldset className={classNameStatus}>
          <p className="label">{I18n.t("policy_detail.description")}</p>
          <textarea rows="2" name="description" className="form-input" value={policy.description}
            onChange={this.handleOnChangeDescription}/>
          <input type="checkbox" id="autoFormatDescription" name="autoFormatDescription"
            onChange={this.handleOnChangeAutoFormat}/>
          <label className="note" htmlFor="autoFormatDescription">{I18n.t("policy_detail.autoFormat")}</label>
        </fieldset>
      </div>
    );
  },

  handleOnChangeDescription: function () {
  },

  handleOnChangeautoFormat: function () {
  },

  renderActions: function() {
    return (
      <div className="form-element">
        <fieldset>
          <div className="l-grid">
            <div className="l-col-3">
              <a className="c-button disabled">{I18n.t("policy_detail.submit")}</a>
            </div>
            <div className="l-col-3">
              <a className="n-button">{I18n.t("policy_detail.cancel")}</a>
            </div>
          </div>
        </fieldset>
      </div>
    );
  }
})
