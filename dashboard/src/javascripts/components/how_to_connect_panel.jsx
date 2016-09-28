import React from "react";
import I18n from "i18n-js";

import { AppShape } from "../shapes";
import { makeConnection, removeConnection } from "../api";

class HowToConnectPanel extends React.Component {
  constructor() {
    super();

    this.state = {
      currentStep: "connect",
      accepted: false,
      comments: ""
    };
  }

  componentWillMount() {
    this.setState({ currentStep: this.props.app.connected ? "disconnect" : "connect" });
  }

  render() {
    switch (this.state.currentStep) {
    case "disconnect":
      return this.renderDisconnectStep();
    case "connect":
      return this.renderConnectStep();
    case "done":
      return this.renderDoneStep();
    case "done-disconnect":
      return this.renderDoneDisconnectStep();
    default:
      return null;
    }
  }

  renderConnectStep() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("how_to_connect_panel.connect_title", {app: this.props.app.name})}</h1>
          <p>{I18n.t("how_to_connect_panel.info_sub_title")}</p>
        </div>

        <div className="mod-connect">
          <div className="box">
            <div className="content">
              <div className="number">1</div>
              <h2>{I18n.t("how_to_connect_panel.checklist")}</h2>
              <ul>
                <li>
                  {I18n.t("how_to_connect_panel.check")}&nbsp;
                  <a onClick={e => this.props.onSwitchPanel(e, "license_info")} href="#">
                    {I18n.t("how_to_connect_panel.license_info")}
                  </a>
                </li>

                <li>
                  {I18n.t("how_to_connect_panel.check")}&nbsp;
                  <a onClick={e => this.props.onSwitchPanel(e, "attribute_policy")} href="#">
                    {I18n.t("how_to_connect_panel.attributes_policy")}
                  </a>
                </li>

                {this.renderWikiUrl()}
              </ul>
            </div>
            <hr />
            <div className="content">
              <div className="number">2</div>
              <h2>{I18n.t("how_to_connect_panel.terms_title")}</h2>
              <ul>
                <li>
                  {I18n.t("how_to_connect_panel.provide_attributes.before")}
                  <a onClick={e => this.props.onSwitchPanel(e, "attribute_policy")} href="#">
                    {I18n.t("how_to_connect_panel.attributes")}
                  </a>
                  {I18n.t("how_to_connect_panel.provide_attributes.after")}
                </li>

                <li>
                  {I18n.t("how_to_connect_panel.forward_permission.before")}
                  <a onClick={e => this.props.onSwitchPanel(e, "attribute_policy")} href="#">
                    {I18n.t("how_to_connect_panel.attributes")}
                  </a>
                  {I18n.t("how_to_connect_panel.forward_permission.after", { app: this.props.app.name })}
                </li>

                <li>
                  {I18n.t("how_to_connect_panel.obtain_license.before")}
                  <a onClick={e => this.props.onSwitchPanel(e, "license_info")} href="#">
                    {I18n.t("how_to_connect_panel.license")}
                  </a>
                  {I18n.t("how_to_connect_panel.obtain_license.after", { app: this.props.app.name })}
                </li>
              </ul>
              <br />
              <p>
                <label>
                  <input type="checkbox" checked={this.state.accepted} onChange={e => this.setState({ accepted: e.target.checked })} />
                  &nbsp;
                  {I18n.t("how_to_connect_panel.accept")}
                </label>
              </p>
            </div>
            { this.renderSingleTenantServiceWarning() }
            <hr />
            <div className="content">
              <div className="number">{this.props.app.exampleSingleTenant ? 4 : 3}</div>
              <h2>{I18n.t("how_to_connect_panel.comments_title")}</h2>
              <p>{I18n.t("how_to_connect_panel.comments_description")}</p>
              <textarea rows="5" value={this.state.comments} onChange={e => this.setState({ comments: e.target.value })} placeholder={I18n.t("how_to_connect_panel.comments_placeholder")} />
            </div>
          </div>
          <p className="cta">
            <a href="#" className={"c-button " + (this.state.accepted ? "" : "disabled")} onClick={this.handleMakeConnection.bind(this)}>{I18n.t("how_to_connect_panel.connect")}</a>
          </p>
        </div>
      </div>
    );
  }

  renderWikiUrl() {
    if (this.props.app.wikiUrl) {
      return (
        <li>
          {I18n.t("how_to_connect_panel.read")}&nbsp;
          <a href={this.props.app.wikiUrl} target="_blank">
            {I18n.t("how_to_connect_panel.wiki")}
          </a>
        </li>
      );
    }

    return null;
  }

  renderSingleTenantServiceWarning() {
    if (this.props.app.exampleSingleTenant) {
      return (
        <div>
          <hr />
          <div className="content">
            <div className="number">3</div>
            <h2>{I18n.t("overview_panel.single_tenant_service")}</h2>
            <p
              dangerouslySetInnerHTML={{ __html: I18n.t("overview_panel.single_tenant_service_html", {name: this.props.app.name}) }}/>
            <p>{I18n.t("how_to_connect_panel.single_tenant_service_warning")}</p>
          </div>
        </div>
      );
    }

    return null;
  }

  renderDoneStep() {
    const subtitle = this.state.action.jiraKey ?
      I18n.t("how_to_connect_panel.done_subtitle_with_jira_html", {jiraKey: this.state.action.jiraKey}) :
      I18n.t("how_to_connect_panel.done_subtitle_html");

    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("how_to_connect_panel.done_title")}</h1>
          <p dangerouslySetInnerHTML={{ __html: subtitle}} />
          <br />
          <p className="cta">
            <a href="/apps" className="c-button">{I18n.t("how_to_connect_panel.back_to_apps")}</a>
          </p>
        </div>
      </div>
    );
  }

  renderDoneDisconnectStep() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("how_to_connect_panel.done_disconnect_title")}</h1>
          <p dangerouslySetInnerHTML={{ __html: I18n.t("how_to_connect_panel.done_disconnect_subtitle_html") }} />
          <br />
          <p className="cta">
            <a href="/apps" className="c-button">{I18n.t("how_to_connect_panel.back_to_apps")}</a>
          </p>
        </div>
      </div>
    );
  }

  renderDisconnectStep() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("how_to_connect_panel.disconnect_title", {app: this.props.app.name})}</h1>
        </div>

        <div className="mod-connect">
          <div className="box">
            <div className="content">
              <h2>{I18n.t("how_to_connect_panel.comments_title")}</h2>
              <p>{I18n.t("how_to_connect_panel.comments_description")}</p>
              <textarea value={this.state.comments} onChange={(e) => this.setState({ comments: e.target.value })} placeholder={I18n.t("how_to_connect_panel.comments_placeholder")} />
              <label>
                <input type="checkbox" checked={this.state.checked} onChange={e => this.setState({ accepted: e.target.checked})} />

                {I18n.t("how_to_connect_panel.accept_disconnect", {app: this.props.app.name})}
              </label>
            </div>
          </div>
          <p className="cta">
            <a href="#" className={"c-button " + (this.state.accepted ? "" : "disabled")} onClick={this.handleDisconnect.bind(this)}>{I18n.t("how_to_connect_panel.disconnect")}</a>
          </p>
        </div>
      </div>
    );
  }

  handleGotoStep(step) {
    return function(e) {
      e.preventDefault();
      e.stopPropagation();
      this.setState({currentStep: step});
    }.bind(this);
  }

  handleMakeConnection(e) {
    e.preventDefault();
    if (this.state.accepted && this.context.currentUser.dashboardAdmin) {
      makeConnection(this.context.currentUser.getCurrentIdpId(), this.props.app, this.state.comments)
      .then(action => this.setState({currentStep: "done", action: action}));
    }
  }

  handleDisconnect(e) {
    e.preventDefault();
    if (this.state.accepted && this.context.currentUser.dashboardAdmin) {
      removeConnection(this.context.currentUser.getCurrentIdpId(), this.props.app, this.state.comments)
      .then(action => this.setState({currentStep: "done-disconnect", action: action}));
    }
  }
}

HowToConnectPanel.contextTypes = {
  currentUser: React.PropTypes.object
};

HowToConnectPanel.propTypes = {
  app: AppShape.isRequired,
  onSwitchPanel: React.PropTypes.func.isRequired
};

export default HowToConnectPanel;
