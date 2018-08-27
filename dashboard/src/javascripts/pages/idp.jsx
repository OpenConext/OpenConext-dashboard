import React from "react";
import I18n from "i18n-js";
import Link from "react-router/Link";
import Flash from "../components/flash";
import moment from "moment";

import {getIdpRolesWithUsers, getInstitutionServiceProviders} from "../api";

class MyIdp extends React.Component {
    constructor() {
        super();

        this.state = {
            roles: {},
            institutionServiceProviders: []
        };
    }

    componentWillMount() {
        getIdpRolesWithUsers().then(data => {
            this.setState({roles: data.payload});
            getInstitutionServiceProviders().then(data => this.setState({institutionServiceProviders: data.payload}));
        });
    }

    render() {
        const roles = Object.keys(this.state.roles);
        return (
            <div className="l-mini">
                <Flash/>
                <div className="mod-idp">
                    <h1>{I18n.t("my_idp.title")}</h1>

                    <p dangerouslySetInnerHTML={{__html: I18n.t("my_idp.sub_title_html")}}></p>
                    {this.renderRoles(roles)}

                    <h1>{I18n.t("my_idp.settings")}</h1>
                    {this.renderIdpFields()}
                    {this.renderServicesFields()}
                </div>
            </div>
        );
    }

    renderServicesFields() {
        return (
            <div>
                <div className="l-grid settings-header">
                    <h1 className="l-col-8">{I18n.t("my_idp.services")}</h1>
                    <Link className="t-button l-col-4 policy-button" to={"/my-idp/edit"}>{I18n.t("my_idp.edit")}</Link>
                </div>
                {this.state.institutionServiceProviders.map(s => this.renderService(s))}
            </div>
        );
    }

    renderService(service) {
        return (
            <div key={service.id}>
                <h2><a href={`/apps/${service.name}`} onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.context.router.transitionTo(`apps/${service.id}/${service.exampleSingleTenant ? "single_tenant_template" : "saml20_sp"}/overview`);
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
                <p>{I18n.t("my_idp.settings_text")}</p>
                <div className="l-grid settings-header">
                    <h2 className="l-col-8">{I18n.t("my_idp.institution")}</h2>
                    <Link className="t-button l-col-4 policy-button" to={"/my-idp/edit"}>{I18n.t("my_idp.edit")}</Link>
                </div>
                <table className="institution">
                    <tbody>
                    <tr>
                        <td className="percent_40">{I18n.t("my_idp.name.en")}</td>
                        <td>{currentIdp.names.en}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t("my_idp.name.nl")}</td>
                        <td>{currentIdp.names.nl}</td>
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
                        <td>{I18n.t("my_idp.logo_url")}</td>
                        <td>
                            {currentIdp.logoUrl ? <img src={currentIdp.logoUrl} alt={currentIdp.logoUrl}/> : null}
                        </td>
                    </tr>
                    </tbody>
                </table>
                { this.renderContactPersons(currentIdp.contactPersons, currentIdp.names[I18n.locale]) }
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
                <div>
                    <h2>{ I18n.t("my_idp.contact", {name: name}) }</h2>
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
                <td>{I18n.t("my_idp.contact_types." + contactPerson.contactPersonType) }</td>
            </tr>
        );
    }

}

MyIdp.contextTypes = {
    currentUser: React.PropTypes.object,
    router: React.PropTypes.object
};

export default MyIdp;
