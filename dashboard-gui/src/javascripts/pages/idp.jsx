import React from "react";
import PropTypes from "prop-types";
import I18n from "i18n-js";
import {Link} from "react-router-dom";
import Flash from "../components/flash";
import moment from "moment";

import {getIdpRolesWithUsers, getInstitutionServiceProviders} from "../api";
import ServiceFilter from "../components/service_filter";
import ReactTooltip from "react-tooltip";
import stopEvent from "../utils/stop";

class MyIdp extends React.Component {

    constructor() {
        super();
        this.state = {
            roles: {},
            institutionServiceProviders: [],
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
            showRoles: true,
            showInstitution: true,
            search: ""
        };
    }

    componentWillMount() {
        getIdpRolesWithUsers().then(data => {
            this.setState({roles: data.payload});
            getInstitutionServiceProviders().then(data => {
                const serviceFilters = {...this.state.serviceFilters};
                serviceFilters.state.values.forEach(val => val.count = data.payload.filter(sp => sp.state === val.search).length);
                this.setState({institutionServiceProviders: data.payload, serviceFilters: serviceFilters});
            });
        });
    }

    onServiceFilterChange(key, index) {
        return e => {
            const serviceFilters = {...this.state.serviceFilters};
            serviceFilters[key].values[index].checked = e.target.checked;
            this.setState({serviceFilters: serviceFilters});
        };
    }

    renderServicesFields(institutionServiceProviders, serviceFilters, search) {
        const searchValues = serviceFilters.state.values.reduce((acc, val) => val.checked ? acc.concat(val.search) : acc, []);
        let providers = searchValues.length > 0 ? institutionServiceProviders.filter(sp => searchValues.includes(sp.state)) : institutionServiceProviders;
        if (search.trim().length > 0) {
            const searchString = search.toLowerCase();
            providers = providers.filter(sp => Object.values(sp.names).some(name => name.toLowerCase().indexOf(searchString) > -1) ||
                sp.spEntityId.toLowerCase().indexOf(searchString) > -1);
        }
        const noProviders = providers.length === 0;
        return (
            <div>
                <div className="l-grid settings-header">
                    <h2 className="l-col-8">{I18n.t("my_idp.services_title")}</h2>
                </div>
                {noProviders && <span>{I18n.t("my_idp.services_title_none")}</span>    }
                {providers.map(s => this.renderService(s))}
            </div>
        );
    }

    renderService(service) {
        const url = `apps/${service.id}/${service.exampleSingleTenant ? "single_tenant_template" : "saml20_sp"}/overview`;
        return (
            <div key={service.id}>
                <h2><a href={`/${url}`}  onClick={e => {
                    stopEvent(e);
                    this.context.router.history.replace(url);
                }}>{service.name}</a></h2>
                <table className="services">
                    <tbody>
                    <tr>
                        <td className="percent_40">{I18n.t("my_idp.entity_id")}</td>
                        <td>{service.spEntityId}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.state")}</td>
                        <td>{I18n.t("my_idp." + service.state)}</td>
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
                        <td>{service.descriptions.en}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.description.nl")}</td>
                        <td>{service.descriptions.nl}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.displayName.nl")}</td>
                        <td>{service.displayNames.nl}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.displayName.en")}</td>
                        <td>{service.displayNames.en}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.published_in_edugain")}</td>
                        <td>{service.publishedInEdugain ? I18n.t("boolean.yes") : I18n.t("boolean.no")}</td>
                    </tr>
                    {service.publishedInEdugain &&
                    <tr>
                        <td>{I18n.t("my_idp.date_published_in_edugain")}</td>
                        <td>{service.publishInEdugainDate}</td>
                    </tr>
                    }
                    <tr>
                        <td>{I18n.t("my_idp.guest_enabled")}</td>
                        <td>{service.guestEnabled ? I18n.t("boolean.yes") : I18n.t("boolean.no")}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.no_consent_required")}</td>
                        <td>{service.noConsentRequired ? I18n.t("boolean.yes") : I18n.t("boolean.no")}</td>
                    </tr>
                    </tbody>
                </table>
                {this.renderContactPersons(service.contactPersons, service.names[I18n.locale])}
            </div>
        );
    }

    renderIdpFields() {
        const {currentUser} = this.context;
        const currentIdp = currentUser.getCurrentIdp();
        return (
            <div>
                <table className="institution">
                    <tbody>
                    <tr>
                        <td className="percent_40">{I18n.t("my_idp.entity_id")}</td>
                        <td>{currentIdp.id}</td>
                    </tr>
                    <tr>
                        <td className="percent_40">{I18n.t("my_idp.name.en")}</td>
                        <td>{currentIdp.names.en}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.name.nl")}</td>
                        <td>{currentIdp.names.nl}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.state")}</td>
                        <td>{I18n.t("my_idp." + currentIdp.state)}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.description.en")}</td>
                        <td>{currentIdp.descriptions.en}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.description.nl")}</td>
                        <td>{currentIdp.descriptions.nl}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.displayName.en")}</td>
                        <td>{currentIdp.displayNames.en}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.displayName.nl")}</td>
                        <td>{currentIdp.displayNames.nl}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.keywords.en")}</td>
                        <td>{currentIdp.keywords.en}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.keywords.nl")}</td>
                        <td>{currentIdp.keywords.nl}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.published_in_edugain")}</td>
                        <td>{currentIdp.publishedInEdugain ? I18n.t("boolean.yes") : I18n.t("boolean.no")}</td>
                    </tr>
                    {currentIdp.publishedInEdugain &&
                    <tr>
                        <td>{I18n.t("my_idp.date_published_in_edugain")}</td>
                        <td>{this.renderDate(currentIdp.publishInEdugainDate)}</td>
                    </tr>
                    }
                    <tr>
                        <td>{I18n.t("my_idp.research_and_scholarship_info")}</td>
                        <td>{currentIdp.connectToRSServicesAutomatically ? I18n.t("boolean.yes") : I18n.t("boolean.no")}
                            <span>
                            <i className="fa fa-info-circle" data-for="connectToRSServicesAutomatically" data-tip></i>
                                <ReactTooltip id="connectToRSServicesAutomatically" type="info" class="tool-tip" effect="solid"
                                              multiline={true} delayHide={1000}>
                                    <span dangerouslySetInnerHTML={{__html: I18n.t("my_idp.research_and_scholarship_tooltip")}}/>
                                </ReactTooltip>
                        </span>
                        </td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.logo_url")}</td>
                        <td>
                            {currentIdp.logoUrl ? <img src={currentIdp.logoUrl} alt={currentIdp.logoUrl}/> : null}
                        </td>
                    </tr>
                    </tbody>
                </table>
                {this.renderContactPersons(currentIdp.contactPersons, currentIdp.names[I18n.locale])}
            </div>
        );
    }

    renderDate(dateString) {
        const date = moment(dateString);
        date.locale(I18n.locale);
        return date.format("LLLL");
    }

    renderRoles(roles) {
        return (
            <table>
                <thead>
                <tr>
                    <th className="percent_50">{I18n.t("my_idp.role")}</th>
                    <th className="percent_50">{I18n.t("my_idp.users")}</th>
                </tr>
                </thead>
                <tbody>
                {roles.map(this.renderRole.bind(this))}
                </tbody>
            </table>
        );
    }

    renderRole(role) {
        const names = this.state.roles[role].map(r => {
            return r.firstName + " " + r.surname;
        }).sort().join(", ");
        const roleName = I18n.t("my_idp")[role];
        return (
            <tr key={role}>
                <td>{roleName}</td>
                <td>{names}</td>
            </tr>
        );
    }

    renderContactPersons(contactPersons, name) {
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
                        {contactPersons.map(this.renderContactPerson.bind(this))}
                        </tbody>
                    </table>
                </div>
            );
        }

        return null;
    }

    renderContactPerson(contactPerson, i) {
        return (
            <tr key={i}>
                <td>{contactPerson.name}</td>
                <td>{contactPerson.emailAddress}</td>
                <td>{contactPerson.telephoneNumber}</td>
                <td>{I18n.t("my_idp.contact_types." + contactPerson.contactPersonType)}</td>
            </tr>
        );
    }

    render() {
        const {roles, showInstitution, showRoles, serviceFilters, institutionServiceProviders, search} = this.state;
        const isDashboardAdmin = this.context.currentUser.dashboardAdmin;
        const text = isDashboardAdmin ? I18n.t("my_idp.settings_text") : I18n.t("my_idp.settings_text_viewer");
        const hasServices = institutionServiceProviders.length > 0;
        return (
            <div className="l-main">
                <Flash/>
                <div className="l-left">
                    {hasServices && <ServiceFilter onChange={this.onServiceFilterChange.bind(this)}
                                   filters={serviceFilters}
                                   search={search}
                                   searchChange={e => this.setState({search: e.target.value})}/>}
                </div>
                <div className="l-right">
                    <div className="mod-idp">
                        <h1 className="top">{I18n.t("my_idp.title")}</h1>
                        <p>{text}</p>
                        {isDashboardAdmin && <div className="edit-my-idp">
                            <Link className="t-button" to={"/my-idp/edit"}>{I18n.t("my_idp.edit")}</Link>
                        </div>}


                        <h2 className="top">{I18n.t("my_idp.roles")}
                            <i className={`fa fa-caret-${showRoles ? "up" : "down"}`}
                               onClick={() => this.setState({showRoles: !this.state.showRoles})}/>
                        </h2>

                        {showRoles && <div>
                            <p dangerouslySetInnerHTML={{__html: I18n.t("my_idp.sub_title_html")}}></p>
                            {this.renderRoles(Object.keys(roles))}
                        </div>}


                        <h2>{I18n.t("my_idp.settings")}
                            <i className={`fa fa-caret-${showInstitution ? "up" : "down"}`}
                               onClick={() => this.setState({showInstitution: !this.state.showInstitution})}/>
                        </h2>
                        {showInstitution && this.renderIdpFields()}
                        {this.renderServicesFields(institutionServiceProviders, serviceFilters, search)}
                    </div>
                </div>

            </div>
        );
    }


}

MyIdp.contextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object
};

export default MyIdp;
