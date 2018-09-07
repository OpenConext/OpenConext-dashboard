import React from "react";
import I18n from "i18n-js";
import {setFlash} from "../utils/flash";

import {getInstitutionServiceProviders, sendChangeRequest} from "../api";
import ServiceFilter from "../components/service_filter";

const contactPersonTypes = ["administrative", "support", "technical"];

class EditMyIdp extends React.Component {

    constructor(props, context) {
        super();
        const {currentUser} = context;
        const currentIdp = currentUser.getCurrentIdp();
        this.state = {
            serviceProviderSettings: [],
            keywordsEn: currentIdp.keywords.en || "",
            keywordsNl: currentIdp.keywords.nl || "",
            logoUrl: currentIdp.logoUrl,
            state: currentIdp.state,
            displayNamesEn: currentIdp.displayNames.en || "",
            displayNamesNl: currentIdp.displayNames.nl || "",
            descriptionsEn: currentIdp.descriptions.en || "",
            descriptionsNl: currentIdp.descriptions.nl || "",
            publishedInEdugain: !!currentIdp.publishedInEdugain,
            comments: "",
            contactPersons: currentIdp.contactPersons.map(contactPerson => ({
                name: contactPerson.name || "",
                emailAddress: contactPerson.emailAddress || "",
                contactPersonType: contactPerson.contactPersonType,
                telephoneNumber: contactPerson.telephoneNumber || ""
            })),
            serviceFilters: {
                state: {
                    name: I18n.t("my_idp.state"),
                    tooltip: I18n.t("service_filter.state.tooltip"),
                    values: [
                        {name: I18n.t("my_idp.prodaccepted"), count: 0, checked: false, search: "prodaccepted"},
                        {name: I18n.t("my_idp.testaccepted"), count: 0, checked: false, search: "testaccepted"},
                    ]
                }
            },
            showInstitution: true,
            search: ""
        };
    }

    componentWillMount() {
        getInstitutionServiceProviders().then(data => {
            const serviceFilters = {...this.state.serviceFilters};
            serviceFilters.state.values.forEach(val => val.count = data.payload.filter(sp => sp.state === val.search).length);
            this.setState({serviceProviderSettings: data.payload, serviceFilters: serviceFilters});
        });
    }

    renderInput(fieldName) {
        return (
            <input
                type="text"
                value={this.state[fieldName] || ""}
                id={fieldName}
                onChange={this.changeField.bind(this)}
            />
        );
    }

    renderCheckbox(fieldName) {
        return (
            <input
                type="checkbox"
                checked={this.state[fieldName] || false}
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
                value={_.get(service, fieldName) || ""}
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
                checked={service[fieldName] || false}
                id={`${serviceId} ${fieldName}`}
                onChange={e => this.changeServiceCheckbox(serviceId, fieldName, e)}
            />
        );
    }

    changeField(e) {
        const {target: {id, value}} = e;
        this.setState({[id]: value});
    }

    changeCheckbox(e) {
        const {target: {id, checked}} = e;
        this.setState({[id]: checked});
    }

    changeServiceCheckbox(serviceId, fieldName, e) {
        const newServiceProviderSettings = [...this.state.serviceProviderSettings];
        const service = newServiceProviderSettings.find(s => s.id === serviceId);
        service[fieldName] = e.target.checked;
        this.setState({serviceProviderSettings: newServiceProviderSettings});
    }

    changeServiceField(serviceId, fieldName, e) {
        const newServiceProviderSettings = [...this.state.serviceProviderSettings];
        const service = newServiceProviderSettings.find(s => s.id === serviceId);
        _.set(service, fieldName, e.target.value);
        this.setState({serviceProviderSettings: newServiceProviderSettings});
    }

    renderIdpFields() {
        const {contactPersons} = this.state;
        const {currentUser} = this.context;
        const currentIdp = currentUser.getCurrentIdp();

        return (
            <div>
                <p>{I18n.t("my_idp.edit_message")}</p>
                <table className="institution">
                    <tbody>
                    <tr>
                        <td className="percent_40">{I18n.t("my_idp.keywords.en")}</td>
                        <td>
                            {this.renderInput("keywordsEn")}
                        </td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.keywords.nl")}</td>
                        <td>
                            {this.renderInput("keywordsNl")}
                        </td>
                    </tr>
                    <tr>
                        <td className="percent_40">{I18n.t("my_idp.description.en")}</td>
                        <td>
                            {this.renderInput("descriptionsEn")}
                        </td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.description.nl")}</td>
                        <td>
                            {this.renderInput("descriptionsNl")}
                        </td>
                    </tr>
                    <tr>
                        <td className="percent_40">{I18n.t("my_idp.displayName.en")}</td>
                        <td>
                            {this.renderInput("displayNamesEn")}
                        </td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.displayName.nl")}</td>
                        <td>
                            {this.renderInput("displayNamesNl")}
                        </td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.published_in_edugain")}</td>
                        <td>{this.renderCheckbox("publishedInEdugain")}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.new_logo_url")}</td>
                        <td>
                            {this.renderInput("logoUrl")}
                        </td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.state")}</td>
                        <td>
                            <select value={this.state.state}
                                    onChange={e => this.setState({state: e.target.value})}>
                                <option value="prodaccepted">{I18n.t("my_idp.prodaccepted")}</option>
                                <option value="testaccepted">{I18n.t("my_idp.testaccepted")}</option>
                            </select>
                        </td>
                    </tr>
                    </tbody>
                </table>
                {this.renderContactPersons(true, currentIdp.names[I18n.locale], contactPersons)}
            </div>
        );
    }

    renderContactPersons(contactPersonsOfIdp, name, contactPersons, service = null) {
        if (contactPersons && contactPersons.length > 0) {
            return (
                <div className="contact-persons">
                    <h3>{I18n.t("my_idp.contact", {name: name})}</h3>
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
                        {contactPersons.map(this.renderContactPerson.bind(this, contactPersonsOfIdp, service))}
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
                value={this.state.contactPersons[i][field] || ""}
                onChange={this.changeContactPersonField.bind(this, field, i, null)}
            />
        );
    }

    changeContactPersonField(field, i, service, e) {
        const newContactPersons = [...this.state.contactPersons];
        newContactPersons[i][field] = e.target.value;
        this.setState({contactPersons: newContactPersons});
    }

    renderServiceContactPersonInput(field, i, service) {
        return (
            <input
                type="text"
                value={service.contactPersons[i][field] || ""}
                onChange={this.changeServiceContactPersonField.bind(this, field, i, service)}
            />
        );
    }

    changeServiceContactPersonField(field, i, service, e) {
        const serviceProviderSettings = [...this.state.serviceProviderSettings];
        const serviceToUpdate = serviceProviderSettings.find(s => s.id === service.id);
        serviceToUpdate.contactPersons[i][field] = e.target.value;
        this.setState({serviceProviderSettings: serviceProviderSettings});
    }

    renderContactPerson(contactPersonsOfIdp, service, contactPerson, i) {
        const renderFunction = contactPersonsOfIdp ? this.renderContactPersonInput.bind(this) : this.renderServiceContactPersonInput.bind(this);
        const changeFunction = contactPersonsOfIdp ? this.changeContactPersonField : this.changeServiceContactPersonField;

        return (
            <tr key={i}>
                <td>{renderFunction("name", i, service)}</td>
                <td>{renderFunction("emailAddress", i, service)}</td>
                <td>{renderFunction("telephoneNumber", i, service)}</td>
                <td>
                    <select className="contact-person-type" value={contactPerson.contactPersonType}
                            onChange={changeFunction.bind(this, "contactPersonType", i, service)}>
                        {
                            contactPersonTypes.map(
                                type => <option key={type}
                                                value={type}>{I18n.t("my_idp.contact_types." + type)}</option>
                            )
                        }
                    </select>
                </td>
            </tr>
        );
    }

    renderServicesFields(serviceFilters, serviceProviderSettings, search) {
        const searchValues = serviceFilters.state.values.reduce((acc, val) => val.checked ? acc.concat(val.search) : acc, []);
        let providers = searchValues.length > 0 ? serviceProviderSettings.filter(sp => searchValues.includes(sp.state)) : serviceProviderSettings;
        if (search.trim().length > 0) {
            const searchString = search.toLowerCase();
            providers = providers.filter(sp => Object.values(sp.names).some(name => name.toLowerCase().indexOf(searchString) > -1) ||
                sp.spEntityId.toLowerCase().indexOf(searchString) > -1);
        }
        return (
            <div>
                <h2>{I18n.t("my_idp.services")}</h2>
                {providers.map((s, index) => this.renderService(s, index))}
            </div>
        );
    }

    renderService(service) {
        const url = `apps/${service.id}/${service.exampleSingleTenant ? "single_tenant_template" : "saml20_sp"}/overview`;
        return (
            <div key={service.id}>
                <h2><a href={`/${url}`} onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.context.router.transitionTo(url);
                }}>{service.name}</a></h2>
                <table className="services">
                    <tbody>
                    <tr>
                        <td className="percent_40">{I18n.t("my_idp.entity_id")}</td>
                        <td>{service.spEntityId}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.name.en")}</td>
                        <td>{service.names.en}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.name.nl")}</td>
                        <td>{service.names.nl}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.description.en")}</td>
                        <td>{this.renderServiceInput(service.id, "descriptions.en")}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.description.nl")}</td>
                        <td>{this.renderServiceInput(service.id, "descriptions.nl")}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.displayName.en")}</td>
                        <td>{this.renderServiceInput(service.id, "displayNames.en")}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.displayName.nl")}</td>
                        <td>{this.renderServiceInput(service.id, "displayNames.nl")}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.published_in_edugain")}</td>
                        <td>{this.renderServiceCheckbox(service.id, "publishedInEdugain")}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.guest_enabled")}</td>
                        <td>{this.renderServiceCheckbox(service.id, "guestEnabled")}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.no_consent_required")}</td>
                        <td>{this.renderServiceCheckbox(service.id, "noConsentRequired")}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.state")}</td>
                        <td>
                            <select value={service.state}
                                    onChange={e => this.changeServiceField(service.id, "state", e)}>
                                <option value="prodaccepted">{I18n.t("my_idp.prodaccepted")}</option>
                                <option value="testaccepted">{I18n.t("my_idp.testaccepted")}</option>
                            </select>
                        </td>
                    </tr>

                    </tbody>
                </table>
                {this.renderContactPersons(false, service.names[I18n.locale], service.contactPersons, service)}
            </div>
        );
    }

    onServiceFilterChange(key, index) {
        return e => {
            const serviceFilters = {...this.state.serviceFilters};
            serviceFilters[key].values[index].checked = e.target.checked;
            this.setState({serviceFilters: serviceFilters});
        };
    }

    saveRequest(e) {
        e.preventDefault();

        const request = _.cloneDeep(this.state);
        request.serviceProviderSettings = request.serviceProviderSettings.map(s => (
            {
                spEntityId: s.spEntityId,
                descriptionEn: s.descriptions.en,
                descriptionNl: s.descriptions.nl,
                displayNameEn: s.displayNames.en,
                displayNameNl: s.displayNames.nl,
                publishedInEdugain: s.publishedInEdugain,
                hasGuestEnabled: s.guestEnabled,
                noConsentRequired: s.noConsentRequired,
                contactPersons: s.contactPersons
            }));

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
        const {showInstitution, serviceFilters, serviceProviderSettings, search} = this.state;
        const hasServices = serviceProviderSettings.length > 0;
        return (
            <div className="l-main">
                <div className="l-left">
                    {hasServices && <ServiceFilter onChange={this.onServiceFilterChange.bind(this)}
                                   filters={serviceFilters}
                                   search={search}
                                   searchChange={e => this.setState({search: e.target.value})}/>}
                </div>
                <div className="l-right">
                    <div className="mod-idp">
                        <h1>{I18n.t("my_idp.settings_edit")}</h1>
                        <h2>{I18n.t("my_idp.institution")}
                            <i className={`fa fa-caret-${showInstitution ? "up" : "down"}`}
                               onClick={() => this.setState({showInstitution: !this.state.showInstitution})}/>
                        </h2>
                        {showInstitution && this.renderIdpFields()}
                        {hasServices && this.renderServicesFields(serviceFilters, serviceProviderSettings, search)}
                        <h2>{I18n.t("my_idp.comments")}</h2>
                        <textarea value={this.state.comments}
                                  onChange={e => this.setState({comments: e.target.value})}/>
                        <a href="#" className="t-button save policy-button"
                           onClick={e => this.saveRequest(e)}>{I18n.t("my_idp.save")}</a>
                    </div>
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
