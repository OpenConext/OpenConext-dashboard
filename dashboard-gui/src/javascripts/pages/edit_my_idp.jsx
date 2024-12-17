import PropTypes from 'prop-types'
import React from 'react'
import Helmet from 'react-helmet'
import I18n from 'i18n-js'
import {clearFlash, setFlash} from '../utils/flash'

import {getInstitutionServiceProviders, sendChangeRequest} from '../api'
import ReactTooltip from 'react-tooltip'
import get from 'lodash.get'
import set from 'lodash.set'
import cloneDeep from 'lodash.clonedeep'
import stopEvent from '../utils/stop'
import {isEmpty} from "../utils/utils";

const contactPersonTypes = ['administrative', 'support', 'technical']

const requiredFields = ['organisationNameEn']

const urlFields = ["organisationUrlEn", "organisationUrlNl"];

const urlRegExp = /^(http|https):\/\/(.+)$/

class EditMyIdp extends React.Component {
    constructor(props, context) {
        super(props)
        const {currentUser} = context
        const currentIdp = currentUser.getCurrentIdp()

        this.state = {
            serviceProviderSettings: [],
            keywordsEn: currentIdp.keywords.en || '',
            keywordsNl: currentIdp.keywords.nl || '',
            stateType: currentIdp.state,
            displayNamesEn: currentIdp.displayNames.en || '',
            displayNamesNl: currentIdp.displayNames.nl || '',
            descriptionsEn: currentIdp.descriptions.en || '',
            descriptionsNl: currentIdp.descriptions.nl || '',
            organisationUrlEn: currentIdp.homeUrls.en || '',
            organisationUrlNl: currentIdp.homeUrls.nl || '',
            organisationNameEn: currentIdp.organisationNames.en || '',
            organisationNameNl: currentIdp.organisationNames.nl || '',
            organisationDisplayNameEn: currentIdp.organisationDisplayNames.en || '',
            organisationDisplayNameNl: currentIdp.organisationDisplayNames.nl || '',
            comments: '',
            contactPersons: currentIdp.contactPersons.map((contactPerson) => ({
                name: contactPerson.name || '',
                emailAddress: contactPerson.emailAddress || '',
                contactPersonType: contactPerson.contactPersonType,
                telephoneNumber: contactPerson.telephoneNumber || '',
            })),
            showInstitution: true,
            search: '',
            processing: false,
            invalidFields: {},
            missingFields: {}
        }
    }

    componentDidMount() {
        getInstitutionServiceProviders().then((data) => {
            this.setState({serviceProviderSettings: data.payload})
        })
    }

    validateInput = fieldName => e => {
        if (requiredFields.includes(fieldName) || urlFields.includes(fieldName)) {
            const value = e.target.value
            const requiredError = requiredFields.includes(fieldName) && isEmpty(value)
            const urlError = urlFields.includes(fieldName) && !isEmpty(value) && !urlRegExp.test(value)
            if (!requiredError && !urlError) {
                clearFlash()
            }
            const newMissingFields = {...this.state.missingFields, [fieldName]: requiredError}
            const newInvalidFields = {...this.state.invalidFields, [fieldName]: urlError}
            this.setState({missingFields: newMissingFields, invalidFields: newInvalidFields})
        }
    }

    renderInput(fieldName, displayName) {
        const {missingFields, invalidFields} = this.state
        const typeInput = urlFields.includes(fieldName) ? "url" : "text"
        const value = this.state[fieldName];
        const requiredError =  missingFields[fieldName]
        const urlError = invalidFields[fieldName]
        return (
            <>
                <input type={typeInput}
                       className={(requiredError || urlError) ? "error" : ""}
                       value={value || ''}
                       id={fieldName}
                       onBlur={this.validateInput(fieldName)}
                       onChange={this.changeField.bind(this)}/>
                {requiredError &&
                    <section className={"errors"}>
                        <span>{I18n.t("forms.required", {name: displayName || fieldName})}</span>
                    </section>}
                {urlError &&
                    <section className={"errors"}>
                        <span>{I18n.t("forms.invalidUrl", {url: value})}</span>
                    </section>}
            </>
        )
    }

    renderCheckbox(fieldName) {
        return (
            <input
                type="checkbox"
                checked={this.state[fieldName] || false}
                id={fieldName}
                onChange={this.changeCheckbox.bind(this)}
            />
        )
    }

    getService(serviceId) {
        return this.state.serviceProviderSettings.find((s) => s.id === serviceId)
    }

    renderServiceInput(serviceId, fieldName) {
        const service = this.getService(serviceId)
        return (
            <input
                type="text"
                value={get(service, fieldName) || ''}
                id={`${serviceId} ${fieldName}`}
                onChange={(e) => this.changeServiceField(serviceId, fieldName, e)}
            />
        )
    }

    renderServiceCheckbox(serviceId, fieldName) {
        const service = this.getService(serviceId)
        return (
            <input
                type="checkbox"
                checked={service[fieldName] || false}
                id={`${serviceId} ${fieldName}`}
                onChange={(e) => this.changeServiceCheckbox(serviceId, fieldName, e)}
            />
        )
    }

    changeField(e) {
        const {
            target: {id, value},
        } = e
        this.setState({[id]: value})
        if (urlFields.includes(id)) {
            const newInvalidFields = {...this.state.invalidFields, [id]: false}
            this.setState({invalidFields: newInvalidFields})
        }
        if (requiredFields.includes(id)) {
            const newMissingFields = {...this.state.missingFields, [id]: false}
            this.setState({missingFields: newMissingFields})
        }
    }

    changeCheckbox(e) {
        const {
            target: {id, checked},
        } = e
        this.setState({[id]: checked})
    }

    changeServiceCheckbox(serviceId, fieldName, e) {
        const newServiceProviderSettings = [...this.state.serviceProviderSettings]
        const service = newServiceProviderSettings.find((s) => s.id === serviceId)
        service[fieldName] = e.target.checked
        this.setState({serviceProviderSettings: newServiceProviderSettings})
    }

    changeServiceField(serviceId, fieldName, e) {
        const newServiceProviderSettings = [...this.state.serviceProviderSettings]
        const service = newServiceProviderSettings.find((s) => s.id === serviceId)
        set(service, fieldName, e.target.value)
        this.setState({serviceProviderSettings: newServiceProviderSettings})
    }

    renderIdpFields() {
        const {contactPersons} = this.state
        const {currentUser} = this.context
        const currentIdp = currentUser.getCurrentIdp()

        return (
            <div>
                <p>{I18n.t('my_idp.edit_message')}</p>
                <table className="institution">
                    <tbody>
                    <tr>
                        <td className="percent_40">{I18n.t('my_idp.keywords.en')}</td>
                        <td>{this.renderInput('keywordsEn')}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t('my_idp.keywords.nl')}</td>
                        <td>{this.renderInput('keywordsNl')}</td>
                    </tr>
                    <tr>
                        <td className="percent_40">{I18n.t('my_idp.description.en')}</td>
                        <td>{this.renderInput('descriptionsEn')}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t('my_idp.description.nl')}</td>
                        <td>{this.renderInput('descriptionsNl')}</td>
                    </tr>
                    <tr>
                        <td className="percent_40">{I18n.t('my_idp.displayName.en')}</td>
                        <td>{this.renderInput('displayNamesEn')}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t('my_idp.displayName.nl')}</td>
                        <td>{this.renderInput('displayNamesNl')}</td>
                    </tr>
                    <tr>
                        <td>
                            {I18n.t('my_idp.organizationURL.en')}
                            <span>
                  <i className="fa fa-info-circle" data-for="organizationURLen" data-tip></i>
                  <ReactTooltip id="organizationURLen" type="info" class="tool-tip" effect="solid" multiline={true}>
                    <span dangerouslySetInnerHTML={{__html: I18n.t('my_idp.organizationURL_en_tooltip')}}/>
                  </ReactTooltip>
                </span>
                        </td>
                        <td>{this.renderInput('organisationUrlEn', I18n.t('my_idp.organizationURL.en'))}</td>
                    </tr>
                    <tr>
                        <td>
                            {I18n.t('my_idp.organizationURL.nl')}
                            <span>
                  <i className="fa fa-info-circle" data-for="organizationURLnl" data-tip></i>
                  <ReactTooltip id="organizationURLnl" type="info" class="tool-tip" effect="solid" multiline={true}>
                    <span dangerouslySetInnerHTML={{__html: I18n.t('my_idp.organizationURL_nl_tooltip')}}/>
                  </ReactTooltip>
                </span>
                        </td>
                        <td>{this.renderInput('organisationUrlNl', I18n.t('my_idp.organizationURL.nl'))}</td>
                    </tr>
                    <tr>
                        <td>
                            {I18n.t('my_idp.organizationName.en')}
                            <span>
                  <i className="fa fa-info-circle" data-for="organizationNameEn" data-tip></i>
                  <ReactTooltip id="organizationNameEn" type="info" class="tool-tip" effect="solid" multiline={true}>
                    <span dangerouslySetInnerHTML={{__html: I18n.t('my_idp.organizationName_en_tooltip')}}/>
                  </ReactTooltip>
                </span>
                        </td>
                        <td>{this.renderInput('organisationNameEn', I18n.t('my_idp.organizationName.en'))}</td>
                    </tr>
                    <tr>
                        <td>
                            {I18n.t('my_idp.organizationName.nl')}
                            <span>
                  <i className="fa fa-info-circle" data-for="organizationNameNl" data-tip></i>
                  <ReactTooltip id="organizationNameNl" type="info" class="tool-tip" effect="solid" multiline={true}>
                    <span dangerouslySetInnerHTML={{__html: I18n.t('my_idp.organizationName_nl_tooltip')}}/>
                  </ReactTooltip>
                </span>
                        </td>
                        <td>{this.renderInput('organisationNameNl')}</td>
                    </tr>
                    <tr>
                        <td>
                            {I18n.t('my_idp.organizationDisplayName.en')}
                            <span>
                  <i className="fa fa-info-circle" data-for="organizationDisplayNameEn" data-tip></i>
                  <ReactTooltip
                      id="organizationDisplayNameEn"
                      type="info"
                      class="tool-tip"
                      effect="solid"
                      multiline={true}
                  >
                    <span dangerouslySetInnerHTML={{__html: I18n.t('my_idp.organizationDisplayName_en_tooltip')}}/>
                  </ReactTooltip>
                </span>
                        </td>
                        <td>{this.renderInput('organisationDisplayNameEn')}</td>
                    </tr>
                    <tr>
                        <td>
                            {I18n.t('my_idp.organizationDisplayName.nl')}
                            <span>
                  <i className="fa fa-info-circle" data-for="organizationDisplayNameNl" data-tip></i>
                  <ReactTooltip
                      id="organizationDisplayNameNl"
                      type="info"
                      class="tool-tip"
                      effect="solid"
                      multiline={true}
                  >
                    <span dangerouslySetInnerHTML={{__html: I18n.t('my_idp.organizationDisplayName_nl_tooltip')}}/>
                  </ReactTooltip>
                </span>
                        </td>
                        <td>{this.renderInput('organisationDisplayNameNl')}</td>
                    </tr>
                    <tr>
                        <td>{I18n.t('my_idp.state')}</td>
                        <td>
                            <select value={this.state.stateType}
                                    onChange={(e) => this.setState({stateType: e.target.value})}>
                                <option value="prodaccepted">{I18n.t('my_idp.prodaccepted')}</option>
                            </select>
                        </td>
                    </tr>
                    </tbody>
                </table>
                {this.renderContactPersons(true, currentIdp.names[I18n.locale], contactPersons)}
            </div>
        )
    }

    renderContactPersons(contactPersonsOfIdp, name, contactPersons, service = null) {
        if (contactPersons && contactPersons.length > 0) {
            return (
                <div className="contact-persons">
                    <h3>{I18n.t('my_idp.contact', {name: name})}</h3>
                    <table>
                        <thead>
                        <tr>
                            <th scope="col" className="percent_25">
                                {I18n.t('my_idp.contact_name.title')}
                            </th>
                            <th scope="col" className="percent_25">
                                {I18n.t('my_idp.contact_email.title')}
                                <i className="fa fa-info-circle" data-for="contact-email-tooltip" data-tip/>
                                <ReactTooltip id="contact-email-tooltip" type="info" class="tool-tip" effect="solid"
                                              multiline={true}>
                                    <span dangerouslySetInnerHTML={{__html: I18n.t('my_idp.contact_email.tooltip')}}/>
                                </ReactTooltip>
                            </th>
                            <th scope="col" className="percent_25">
                                {I18n.t('my_idp.contact_telephone.title')}
                            </th>
                            <th scope="col" className="percent_25">
                                {I18n.t('my_idp.contact_type.title')}
                                <i className="fa fa-info-circle" data-for="contact-person-type-all" data-tip/>
                                <ReactTooltip
                                    id="contact-person-type-all"
                                    type="info"
                                    class="tool-tip"
                                    effect="solid"
                                    multiline={true}
                                >
                                    <ul>{contactPersonTypes.map(this.renderContactTypeTooltip)}</ul>
                                </ReactTooltip>
                            </th>
                        </tr>
                        </thead>
                        <tbody>{contactPersons.map(this.renderContactPerson.bind(this, contactPersonsOfIdp, service))}</tbody>
                    </table>
                </div>
            )
        }
        return null
    }

    renderContactPersonInput(field, i) {
        return (
            <input
                type="text"
                value={this.state.contactPersons[i][field] || ''}
                onChange={this.changeContactPersonField.bind(this, field, i, null)}
            />
        )
    }

    changeContactPersonField(field, i, service, e) {
        const newContactPersons = [...this.state.contactPersons]
        newContactPersons[i][field] = e.target.value
        this.setState({contactPersons: newContactPersons})
    }

    renderServiceContactPersonInput(field, i, service) {
        return (
            <input
                type="text"
                value={service.contactPersons[i][field] || ''}
                onChange={this.changeServiceContactPersonField.bind(this, field, i, service)}
            />
        )
    }

    changeServiceContactPersonField(field, i, service, e) {
        const serviceProviderSettings = [...this.state.serviceProviderSettings]
        const serviceToUpdate = serviceProviderSettings.find((s) => s.id === service.id)
        serviceToUpdate.contactPersons[i][field] = e.target.value
        this.setState({serviceProviderSettings: serviceProviderSettings})
    }

    renderContactPerson(contactPersonsOfIdp, service, contactPerson, i) {
        const renderFunction = contactPersonsOfIdp
            ? this.renderContactPersonInput.bind(this)
            : this.renderServiceContactPersonInput.bind(this)
        const changeFunction = contactPersonsOfIdp ? this.changeContactPersonField : this.changeServiceContactPersonField

        return (
            <tr key={i}>
                <td>{renderFunction('name', i, service)}</td>
                <td>{renderFunction('emailAddress', i, service)}</td>
                <td>{renderFunction('telephoneNumber', i, service)}</td>
                <td>
                    <select
                        className="contact-person-type"
                        value={contactPerson.contactPersonType}
                        onChange={changeFunction.bind(this, 'contactPersonType', i, service)}
                    >
                        {contactPersonTypes.map((type) => (
                            <option key={type} value={type}>
                                {I18n.t('my_idp.contact_types.' + type + '.display')}
                            </option>
                        ))}
                    </select>
                </td>
            </tr>
        )
    }

    renderContactTypeTooltip(contactPersonType) {
        const tooltipPerson = I18n.t('my_idp.contact_types.' + contactPersonType + '.title')
        const tooltipPersonDescription = I18n.t('my_idp.contact_types.' + contactPersonType + '.tooltip')
        return (
            <li key={contactPersonType}
                dangerouslySetInnerHTML={{__html: `${tooltipPerson}${tooltipPersonDescription}`}}/>
        )
    }

    hasErrors = () => {
        const {missingFields, invalidFields} = this.state
        return Object.values(missingFields).some(val => val) || Object.values(invalidFields).some(val => val)
    }

    saveRequest(e) {
        stopEvent(e)
        if (this.hasErrors()) {
            setFlash(I18n.t('forms.errors'), 'error')
            window.scrollTo(0, 0)
            return
        }
        this.setState({processing: true})
        const request = cloneDeep(this.state)
        request.serviceProviderSettings = request.serviceProviderSettings.map((s) => ({
            spEntityId: s.spEntityId,
            descriptionEn: s.descriptions.en,
            descriptionNl: s.descriptions.nl,
            displayNameEn: s.displayNames.en,
            displayNameNl: s.displayNames.nl,
            hasGuestEnabled: s.guestEnabled,
            noConsentRequired: s.noConsentRequired,
            contactPersons: s.contactPersons,
            stateType: s.state,
        }))

        sendChangeRequest(request)
            .then((res) => {
                res.json().then((action) => {
                    if (action.payload['no-changes']) {
                        setFlash(I18n.t('my_idp.no_change_request_created'), 'warning')
                    } else {
                        setFlash(I18n.t('my_idp.change_request_created', {jiraKey: action.payload.jiraKey}))
                    }
                    this.setState({processing: false})
                    window.scrollTo(0, 0)
                    this.props.history.replace('/my-idp')
                })
            })
            .catch(() => {
                this.setState({processing: false})
                setFlash(I18n.t('my_idp.change_request_failed'), 'error')
                window.scrollTo(0, 0)
                this.props.history.replace('/my-idp')
            })
    }

    render() {
        const {showInstitution, processing} = this.state
        const disableSubmit = this.hasErrors()
        return (
            <div className="container">
                <Helmet title={I18n.t('my_idp.settings_edit')}/>
                <div className="mod-idp">
                    <h1>{I18n.t('my_idp.settings_edit')}</h1>
                    <h2>
                        {I18n.t('my_idp.institution')}
                        <i
                            className={`fa fa-caret-${showInstitution ? 'up' : 'down'}`}
                            onClick={() => this.setState({showInstitution: !this.state.showInstitution})}
                        />
                    </h2>
                    {showInstitution && this.renderIdpFields()}
                    <h2>{I18n.t('my_idp.comments')}</h2>
                    <textarea value={this.state.comments} onChange={(e) => this.setState({comments: e.target.value})}/>
                    <button type="button"
                            className="t-button save policy-button"
                            disabled={processing || disableSubmit}
                            onClick={e => (!processing && !disableSubmit) && this.saveRequest(e)}>
                        {I18n.t('my_idp.save')}
                    </button>
                </div>
            </div>
        )
    }
}

EditMyIdp.contextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object,
}

export default EditMyIdp
