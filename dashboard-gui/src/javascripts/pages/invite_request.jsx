import React from "react";
import I18n from "i18n-js";

import {getAppsForIdentiyProvider, getIdpsForSuper} from "../api";
import SelectWrapper from "../components/select_wrapper";
import {isEmpty} from "../utils/utils";
import CheckBox from "../components/checkbox";
import stopEvent from "../utils/stop";

const defaultContactPersons = ["administrative"];
const validEmailRegExp = /^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
const validNameRegExp = /^[\w \-']{1,255}$/;

class InviteRequest extends React.Component {
    constructor() {
        super();

        this.state = {
            idps: [],
            idpId: undefined,
            idp: undefined,
            sps: [],
            sp: undefined,
            emails: {},
            additionalContactPersons: [],
            newContactAdded: false
        };
    }

    componentWillMount() {
        getIdpsForSuper().then(res => this.setState({idps: res.idps}));
    }

    componentDidUpdate = () => {
        const newContactPersonName = this.newContactPersonName;
        if (newContactPersonName && this.state.newContactAdded) {
            this.newContactPersonName.focus();
            this.newContactPersonName = null;
            this.setState({newContactAdded: false});
        }
    };

    onIdpChange = value => {
        const idp = this.state.idps.find(p => p.id === value);
        this.setState({
                idpId: value,
                idp: idp,
                emails: idp.contactPersons.reduce((acc, contactPerson) => {
                    acc[contactPerson.emailAddress] = defaultContactPersons.includes(contactPerson.contactPersonType);
                    return acc;
                }, {})
            },
            () => getAppsForIdentiyProvider(value)
                .then(res => this.setState({
                    sps: res.payload.apps
                        .filter(app => !app.connected && !app.exampleSingleTenant)
                        .sort((a, b) => a.name.localeCompare(b.name))
                })));
    };

    sendRequest = e => {
        stopEvent(e);
        if (this.isValidForSubmit()) {
            //TODO
        }
    };

    onSpChange = value => {
        this.setState({sp: value});
    };

    selectContactPerson = emailAddress => e => {
        const newEmails = {...this.state.emails};
        newEmails[emailAddress] = e.target.checked;
        this.setState({emails: newEmails})
    };

    addEmail = e => {
        stopEvent(e);
        const newAdditionalEmails = [...this.state.additionalContactPersons];
        newAdditionalEmails.push({name: "", emailAddress: ""});
        this.setState({additionalContactPersons: newAdditionalEmails, newContactAdded: true});
    };

    deleteAdditionalContactPerson = index => e => {
        stopEvent(e);
        const newAdditionalContactPersons = [...this.state.additionalContactPersons];
        newAdditionalContactPersons.splice(index, 1);
        this.setState({additionalContactPersons: newAdditionalContactPersons});
    };

    validContactPersons = (additionalContactPersons, emailsSelected) => {
        if (additionalContactPersons.length === 0) {
            return emailsSelected.length > 0;
        }
        return additionalContactPersons.every(p => validNameRegExp.test(p.name) && validEmailRegExp.test(p.emailAddress));
    };

    changeAdditionalContactPersonName = index => e => {
        const newAdditionalContactPersons = [...this.state.additionalContactPersons];
        newAdditionalContactPersons[index].name = e.target.value;
        this.setState({additionalContactPersons: newAdditionalContactPersons});
    };

    changeAdditionalContactPersonEmail = index => e => {
        const newAdditionalContactPersons = [...this.state.additionalContactPersons];
        newAdditionalContactPersons[index].emailAddress = e.target.value;
        this.setState({additionalContactPersons: newAdditionalContactPersons});
    };

    renderAdditionalContactPerson = (contactPerson, i, isLast) =>
        <tr key={i + 1000}>
            <td className="delete-email"><a href="delete-email" onClick={this.deleteAdditionalContactPerson(i)}>
                <i className="fa fa-minus"/></a></td>
            <td><input type="text" value={contactPerson.name} onChange={this.changeAdditionalContactPersonName(i)}
                       ref={ref => {
                           if (isLast && this.state.newContactAdded) {
                               this.newContactPersonName = ref;
                           }
                       }
                       }/>
            </td>
            <td colSpan={2}><input type="text" value={contactPerson.emailAddress}
                                   onChange={this.changeAdditionalContactPersonEmail(i)}/>
            </td>
        </tr>;

    renderContactPerson = (contactPerson, i) =>
        <tr key={i}>
            <td><CheckBox name={`${i}`}
                          value={this.state.emails[contactPerson.emailAddress]}
                          onChange={this.selectContactPerson(contactPerson.emailAddress)}/></td>
            <td>{contactPerson.name}</td>
            <td>{contactPerson.emailAddress}</td>
            <td>{I18n.t("my_idp.contact_types." + contactPerson.contactPersonType)}</td>
        </tr>;

    renderContactPersons = (contactPersons, name) => contactPersons && contactPersons.length > 0 ?
        <div className="contact-persons">
            <p className="select">{I18n.t("invite_request.contactPersons", {name: name})}</p>
            <table>
                <thead>
                <tr>
                    <th className="percent_25">{I18n.t("invite_request.selectContact")}</th>
                    <th className="percent_25">{I18n.t("my_idp.contact_name")}</th>
                    <th className="percent_25">{I18n.t("my_idp.contact_email")}</th>
                    <th className="percent_25">{I18n.t("my_idp.contact_type")}</th>
                </tr>
                </thead>
                <tbody>
                {contactPersons.map(this.renderContactPerson)}
                {this.state.additionalContactPersons.map((person, index) =>
                    this.renderAdditionalContactPerson(person, index, this.state.additionalContactPersons.length === (index + 1)))}
                <tr>
                    <td colSpan={3}></td>
                    <td className="add-email"><a href="add-email" onClick={this.addEmail}><i
                        className="fa fa-plus"/></a></td>
                </tr>
                </tbody>
            </table>
        </div>
        : null;

    isValidForSubmit = () => {
        const {idp, sp, emails, additionalContactPersons} = this.state;
        const emailsSelectedLength = Object.keys(emails).filter(k => emails[k]);
        return idp && sp && this.validContactPersons(additionalContactPersons, emailsSelectedLength);
    };

    render() {
        const {idps, idpId, idp, sps, sp} = this.state;
        const spSelectDisabled = isEmpty(idp) || sps.length === 0;
        const submitClassName = this.isValidForSubmit() ? "" : "disabled";
        return (
            <div className="l-mini mod-invite-request">
                <p className="info">{I18n.t("invite_request.info")}</p>
                <section className="mod-invite-request-inner">
                    <label>{I18n.t("invite_request.idp")}</label>
                    <SelectWrapper handleChange={this.onIdpChange}
                                   placeholder={I18n.t("invite_request.selectIdp")}
                                   defaultValue={idpId}
                                   isClearable={false}
                                   multiple={false}
                                   options={idps.map(provider => ({display: provider.name, value: provider.id}))}/>
                    <label>{I18n.t("invite_request.sp")}</label>
                    <SelectWrapper handleChange={this.onSpChange}
                                   placeholder={spSelectDisabled ? I18n.t("invite_request.selectSpDisabled") : I18n.t("invite_request.selectSp")}
                                   defaultValue={sp}
                                   isClearable={true}
                                   multiple={false}
                                   isDisabled={spSelectDisabled}
                                   options={sps.map(provider => ({
                                       display: provider.name,
                                       value: provider.spEntityId
                                   }))}/>
                    {(idp && sp) && this.renderContactPersons(idp.contactPersons, idp.name)}
                    <div className="buttons">
                        <a href="/send" className={`t-button save ${submitClassName}`}
                           onClick={this.sendRequest}>{I18n.t("invite_request.sendRequest")}</a>
                    </div>
                </section>
            </div>
        );
    }

}

export default InviteRequest;
