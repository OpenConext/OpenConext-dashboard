import React from "react";
import I18n from "i18n-js";
import { setFlash } from "../utils/flash";

import { getInstitutionServiceProviders, getGuestEnabledServices, sendChangeRequest } from "../api";

class EditMyIdp extends React.Component {
  constructor(props, context) {
    super();

    const { currentUser } = context;
    const currentIdp = currentUser.getCurrentIdp();
    this.state = {
      serviceProviderSettings: [],
      keywordsEn: currentIdp.keywords.en || "",
      keywordsNl: currentIdp.keywords.nl || "",
      publishedInEdugain: !!currentIdp.publishedInEdugain,
      comments: "",
      contactPersons: currentIdp.contactPersons.map(contactPerson => {
        return {
          name: contactPerson.name || "",
          emailAddress: contactPerson.emailAddress || "",
          contactPersonType: contactPerson.contactPersonType,
          telephoneNumber: contactPerson.telephoneNumber || ""
        };
      })
    };
  }

  componentWillMount() {
    Promise.all([
      getInstitutionServiceProviders(),
      getGuestEnabledServices()
    ]).then(([institutionServiceProvidersData, guestEnabledServicesData]) => {
      institutionServiceProvidersData.payload.forEach(sp => {
        sp.hasGuestEnabled = guestEnabledServicesData.payload.find(guestService => guestService.id === sp.id) !== undefined;
        return sp;
      });
      this.setState({
        serviceProviderSettings: institutionServiceProvidersData.payload
      });
    });
  }

  renderInput(fieldName) {
    return (
      <input
        type="text"
        value={this.state[fieldName]}
        id={fieldName}
        onChange={this.changeField.bind(this)}
      />
    );
  }

  renderCheckbox(fieldName) {
    return (
      <input
        type="checkbox"
        checked={this.state[fieldName]}
        id={fieldName}
        onChange={this.changeCheckbox.bind(this)}
      />
    );
  }

  getService(serviceId) {
    return this.state.serviceProviderSettings.find(s => s.id === serviceId);
  }

  renderServiceInput(serviceId, fieldName) {
    const service = this.getService(serviceId);

    return (
      <input
        type="text"
        value={_.get(service, fieldName)}
        id={`${serviceId} ${fieldName}`}
        onChange={e => this.changeServiceField(serviceId, fieldName, e)}
      />
    );
  }

  renderServiceCheckbox(serviceId, fieldName) {
    const service = this.getService(serviceId);

    return (
      <input
        type="checkbox"
        checked={service[fieldName]}
        id={`${serviceId} ${fieldName}`}
        onChange={e => this.changeServiceCheckbox(serviceId, fieldName, e)}
      />
    );
  }

  changeField(e) {
    const { target: { id, value } } = e;
    this.setState({ [id]: value });
  }

  changeCheckbox(e) {
    const { target: { id, checked } } = e;
    this.setState({ [id]: checked });
  }

  changeServiceCheckbox(serviceId, fieldName, e) {
    const { target: { checked } } = e;

    this.setState(currentState => {
      const service = currentState.serviceProviderSettings.find(s => s.id === serviceId);
      service[fieldName] = checked;
      return currentState;
    });
  }

  changeServiceField(serviceId, fieldName, e) {
    const { target: { value } } = e;

    this.setState(currentState => {
      const service = currentState.serviceProviderSettings.find(s => s.id === serviceId);
      _.set(service, fieldName, value);
      return currentState;
    });
  }

  renderIdpFields() {
    // const { currentUser } = this.context;
    // const currentIdp = currentUser.getCurrentIdp();

    return (
      <div>
        <h2>{ I18n.t("my_idp.institution") }</h2>
        <p>{ I18n.t("my_idp.edit_message") }</p>
        <table className="institution">
          <tbody>
            <tr>
              <td className="percent_40">{ I18n.t("my_idp.keywords.en") }</td>
              <td>
                { this.renderInput("keywordsEn") }
              </td>
            </tr>
            <tr>
              <td>{ I18n.t("my_idp.keywords.nl") }</td>
              <td>
                { this.renderInput("keywordsNl") }
              </td>
            </tr>
            <tr>
              <td>{ I18n.t("my_idp.published_in_edugain") }</td>
              <td>{ this.renderCheckbox("publishedInEdugain") }</td>
            </tr>
          </tbody>
        </table>

        { this.renderContactPersons(null) }
      </div>
    );
  }

  renderContactPersons(contactPersons) {
    if (contactPersons && contactPersons.length > 0) {
      return (
        <div>
          <h2>{ I18n.t("my_idp.contact") }</h2>
          <table>
            <thead>
            <tr>
              <th className="percent_25">{I18n.t("my_idp.contact_name")}</th>
              <th className="percent_25">{I18n.t("my_idp.contact_email")}</th>
              <th className="percent_25">{I18n.t("my_idp.contact_telephone")}</th>
              <th className="percent_25">{I18n.t("my_idp.contact_type")}</th>
            </tr>
            </thead>
            <tbody>
              { contactPersons.map(this.renderContactPerson.bind(this)) }
            </tbody>
          </table>
        </div>
      );
    }


    return null;
  }

  renderContactPersonInput(field, i) {
    return (
      <input
        type="text"
        value={this.state.contactPersons[i][field]}
        onChange={e => this.changeContactPersonField(e, field, i)}
      />
    );
  }

  changeContactPersonField(e, field, i) {
    const { value } = e.target;
    this.setState(newState => {
      newState.contactPersons[i][field] = value;
      return newState;
    });
  }

  renderContactPerson(contactPerson, i) {
    return (
      <tr key={i}>
        <td>{ this.renderContactPersonInput("name", i) }</td>
        <td>{ this.renderContactPersonInput("emailAddress", i) }</td>
        <td>{ this.renderContactPersonInput("telephoneNumber", i) }</td>
        <td>
          <select onChange={e => this.changeContactPersonField(e, "contactPersonType", i)}>
            { 
              ["support", "administrative", "technical"].map(type => {
                return <option key={type} value={type}>{ I18n.t("my_idp.contact_types." + type) }</option>;
              })
            }
          </select>
        </td>
      </tr>
    );
  }

  renderServicesFields() {
    return (
      <div>
        <h2>{ I18n.t("my_idp.services") }</h2>
        { this.state.serviceProviderSettings.map(s => this.renderService(s))}
      </div>
    );
  }

  renderService(service) {
    return (
      <table key={service.id} className="services">
        <tbody>
          <tr>
            <td className="percent_40">{ I18n.t("my_idp.entity_id") }</td>
            <td>{ service.spEntityId}</td>
          </tr>
          <tr>
            <td>{ I18n.t("my_idp.name.en") }</td>
            <td>{ service.names.en }</td>
          </tr>
          <tr>
            <td>{ I18n.t("my_idp.name.nl") }</td>
            <td>{ service.names.nl }</td>
          </tr>
          <tr>
            <td>{ I18n.t("my_idp.description.en") }</td>
            <td>{ this.renderServiceInput(service.id, "descriptions.en") }</td>
          </tr>
          <tr>
            <td>{ I18n.t("my_idp.description.nl") }</td>
            <td>{ this.renderServiceInput(service.id, "descriptions.nl") }</td>
          </tr>
          <tr>
            <td>{ I18n.t("my_idp.published_in_edugain") }</td>
            <td>{ this.renderServiceCheckbox(service.id, "publishedInEdugain") }</td>
          </tr>
          <tr>
            <td>{ I18n.t("my_idp.guest_enabled") }</td>
            <td>{ this.renderServiceCheckbox(service.id, "hasGuestEnabled") }</td>
          </tr>
          <tr>
            <td>{ I18n.t("my_idp.no_consent_required") }</td>
            <td>{ this.renderServiceCheckbox(service.id, "noConsentRequired") }</td>
          </tr>
        </tbody>
      </table>
    );
  }

  saveRequest(e) {
    e.preventDefault();

    const request = _.cloneDeep(this.state);
    request.serviceProviderSettings = request.serviceProviderSettings.map(s => {
      return {
        spEntityId: s.spEntityId,
        descriptionEn: s.descriptions.en,
        descriptionNl: s.descriptions.nl,
        publishedInEdugain: s.publishedInEdugain,
        hasGuestEnabled: s.hasGuestEnabled,
        noConsentRequired: s.noConsentRequired
      };
    });

    sendChangeRequest(request)
    .then(res => {
      res.json().then(action => {
        if (action.payload["no-changes"]) {
          setFlash(I18n.t("my_idp.no_change_request_created"), "warning");
        } else {
          setFlash(I18n.t("my_idp.change_request_created"));
        }
        window.scrollTo(0, 0);
        this.context.router.transitionTo("/my-idp");
      });
    })
    .catch(() => setFlash(I18n.t("my_idp.change_request_failed", "error")));
  }

  render() {
    return (
      <div className="l-mini">
        <div className="mod-idp">
          <h1>{ I18n.t("my_idp.settings") }</h1>
          { this.renderIdpFields() }
          { this.renderServicesFields() }
          <h2>{ I18n.t("my_idp.comments") }</h2>
          <textarea value={this.state.comments} onChange={e => this.setState({ comments: e.target.value })} />
          <a href="#" className="t-button save policy-button" onClick={e => this.saveRequest(e)}>{ I18n.t("my_idp.save") }</a>
        </div>
      </div>
    );
  }
}

EditMyIdp.contextTypes = {
  currentUser: React.PropTypes.object,
  router: React.PropTypes.object
};

export default EditMyIdp;
