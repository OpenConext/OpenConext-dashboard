import React from 'react'
import I18n from 'i18n-js'
import { withRouter } from 'react-router'
import PropTypes from 'prop-types'
import { getAppsForInvitationRequest, getIdpsForSuper, inviteRequest, sabRoles } from '../api'
import SelectWrapper from '../components/select_wrapper'
import { isEmpty } from '../utils/utils'
import CheckBox from '../components/checkbox'
import stopEvent from '../utils/stop'
import Flash from '../components/flash'
import { setFlash } from '../utils/flash'
import { validEmailRegExp, validNameRegExp } from '../utils/validations'

class InviteRequest extends React.Component {
  constructor(props) {
    super(props)
    this.state = this.initialState()
  }

  initialState = () => ({
    idps: [],
    idpId: null,
    idp: null,
    sps: [],
    spId: null,
    sp: null,
    emails: {},
    additionalContactPersons: [],
    sabContactPersons: [],
    newContactAdded: false,
    message: '',
  })

  componentDidMount() {
    getIdpsForSuper().then((res) => this.setState({ idps: res.idps }))
  }

  componentDidUpdate = () => {
    const newContactPersonName = this.newContactPersonName
    if (newContactPersonName && this.state.newContactAdded) {
      this.newContactPersonName.focus()
      this.newContactPersonName = null
      this.setState({ newContactAdded: false })
    }
  }

  onIdpChange = (value) => {
    const idp = this.state.idps.find((p) => p.id === value)
    this.setState(
      {
        idpId: value,
        idp: idp,
        emails: idp
          ? idp.contactPersons.reduce((acc, contactPerson) => {
              acc[contactPerson.emailAddress] = false
              return acc
            }, {})
          : [],
      },
      () =>
        idp &&
        Promise.all([getAppsForInvitationRequest(value), sabRoles(idp.institutionId)]).then((res) => {
          const sabPayload = res[1].payload
          const roles = Object.keys(sabPayload)
          const sabPersonMap = roles.reduce((acc, role) => {
            const sabPersons = sabPayload[role].filter((p) => !isEmpty(p.email))
            sabPersons.forEach((p) => (acc[p.uid] = p))
            return acc
          }, {})
          const sabPersons = Object.values(sabPersonMap).map((p) => ({
            emailAddress: p.email,
            name: p.firstName + ' ' + p.surname,
            uid: p.uid,
            roles: p.roles.map((r) => r.roleName),
          }))
          const emails = { ...this.state.emails }
          sabPersons.forEach((p) => (emails[p.uid] = p.roles.includes('SURFconextverantwoordelijke')))
          this.setState({
            sps: res[0].payload.apps
              .filter((app) => !app.connected && !app.exampleSingleTenant)
              .sort((a, b) => a.name.localeCompare(b.name)),
            sabContactPersons: sabPersons,
            emails: emails,
          })
        })
    )
  }

  sendRequest = (e) => {
    stopEvent(e)
    if (this.isValidForSubmit()) {
      const { idpId, idp, sp, emails, additionalContactPersons, sabContactPersons, message } = this.state
      sabContactPersons.forEach((cp) => (cp.sabContact = true))
      const contactPersons = idp.contactPersons
        .filter((cp) => emails[cp.emailAddress])
        .concat(sabContactPersons.filter((cp) => emails[cp.uid]))
        .concat(additionalContactPersons)
      inviteRequest({
        idpEntityId: idpId,
        idpName: idp.name,
        spEntityId: sp.spEntityId,
        spName: sp.name,
        typeMetaData: sp.entityType,
        spId: sp.id,
        message: message,
        contactPersons: contactPersons,
      }).then((json) => {
        this.props.history.replace('/dummy')
        setTimeout(() => {
          window.scrollTo(0, 0)
          this.props.history.replace('/users/invite')
          setFlash(I18n.t('invite_request.jiraFlash', { jiraKey: json.payload.jiraKey }))
        }, 5)
      })
    }
  }

  onSpChange = (value) => {
    this.setState({ spId: value, sp: this.state.sps.find((sp) => sp.spEntityId === value) })
  }

  selectContactPerson = (identifier) => (e) => {
    const newEmails = { ...this.state.emails }
    newEmails[identifier] = e.target.checked
    this.setState({ emails: newEmails })
  }

  addEmail = (e) => {
    stopEvent(e)
    const newAdditionalEmails = [...this.state.additionalContactPersons]
    newAdditionalEmails.push({ name: '', emailAddress: '' })
    this.setState({ additionalContactPersons: newAdditionalEmails, newContactAdded: true })
  }

  deleteAdditionalContactPerson = (index) => (e) => {
    stopEvent(e)
    const newAdditionalContactPersons = [...this.state.additionalContactPersons]
    newAdditionalContactPersons.splice(index, 1)
    this.setState({ additionalContactPersons: newAdditionalContactPersons })
  }

  validContactPersons = (additionalContactPersons, emailsSelected) => {
    if (additionalContactPersons.length === 0) {
      return emailsSelected.length > 0
    }
    return additionalContactPersons.every((p) => validNameRegExp.test(p.name) && validEmailRegExp.test(p.emailAddress))
  }

  changeAdditionalContactPersonName = (index) => (e) => {
    const newAdditionalContactPersons = [...this.state.additionalContactPersons]
    newAdditionalContactPersons[index].name = e.target.value
    this.setState({ additionalContactPersons: newAdditionalContactPersons })
  }

  changeAdditionalContactPersonEmail = (index) => (e) => {
    const newAdditionalContactPersons = [...this.state.additionalContactPersons]
    newAdditionalContactPersons[index].emailAddress = e.target.value
    this.setState({ additionalContactPersons: newAdditionalContactPersons })
  }

  renderAdditionalContactPerson = (contactPerson, i, isLast) => (
    <tr key={i + 1000}>
      <td className="delete-email">
        <a href="delete-email" onClick={this.deleteAdditionalContactPerson(i)}>
          <i className="fa fa-minus" />
        </a>
      </td>
      <td>
        <input
          type="text"
          value={contactPerson.name}
          onChange={this.changeAdditionalContactPersonName(i)}
          ref={(ref) => {
            if (isLast && this.state.newContactAdded) {
              this.newContactPersonName = ref
            }
          }}
        />
      </td>
      <td colSpan={2}>
        <input type="text" value={contactPerson.emailAddress} onChange={this.changeAdditionalContactPersonEmail(i)} />
      </td>
    </tr>
  )

  renderContactPerson = (contactPerson, i, identifier) => {
    const type = contactPerson.contactPersonType
      ? I18n.t('my_idp.contact_types.' + contactPerson.contactPersonType + '.display')
      : contactPerson.roles.join(', ')
    return (
      <tr key={i}>
        <td>
          <CheckBox
            name={`${i}`}
            value={this.state.emails[contactPerson[identifier]] || false}
            onChange={this.selectContactPerson(contactPerson[identifier])}
          />
        </td>
        <td>{contactPerson.name}</td>
        <td>{contactPerson.emailAddress}</td>
        <td>{type}</td>
      </tr>
    )
  }

  renderContactPersons = (contactPersons, sabContactPersons, additionalContactPersons, name) =>
    contactPersons && contactPersons.length > 0 ? (
      <div className="contact-persons">
        <p className="select">{I18n.t('invite_request.contactPersons', { name: name })}</p>
        <table>
          <thead>
            <tr>
              <th className="percent_25">{I18n.t('invite_request.selectContact')}</th>
              <th className="percent_25">{I18n.t('my_idp.contact_name.title')}</th>
              <th className="percent_25">{I18n.t('my_idp.contact_email.title')}</th>
              <th className="percent_25">{I18n.t('my_idp.contact_type.title')}</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td colSpan={4} className="contact-source">
                {I18n.t('invite_request.sourcePersons', { source: 'SAB' })}
              </td>
            </tr>
            {sabContactPersons
              .filter((cp) => !isEmpty(cp.emailAddress))
              .map((cp, index) => this.renderContactPerson(cp, index + contactPersons.length, 'uid'))}
            <tr>
              <td colSpan={4} className="contact-source">
                {I18n.t('invite_request.sourcePersons', { source: 'Manage' })}
              </td>
            </tr>
            {contactPersons
              .filter((cp) => !isEmpty(cp.emailAddress))
              .map((cp, index) => this.renderContactPerson(cp, index, 'emailAddress'))}
            <tr>
              <td colSpan={4} className="contact-source">
                {I18n.t('invite_request.additionalPersons')}
              </td>
            </tr>
            {additionalContactPersons.map((person, index) =>
              this.renderAdditionalContactPerson(
                person,
                index,
                this.state.additionalContactPersons.length === index + 1
              )
            )}
            <tr>
              <td colSpan={3}></td>
              <td className="add-email">
                <a href="add-email" onClick={this.addEmail}>
                  <i className="fa fa-plus" />
                </a>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    ) : null

  isValidForSubmit = () => {
    const { idp, spId, emails, additionalContactPersons } = this.state
    const emailsSelectedLength = Object.keys(emails).filter((k) => emails[k])
    return idp && spId && this.validContactPersons(additionalContactPersons, emailsSelectedLength)
  }

  render() {
    const { idps, idpId, idp, sps, spId, message, sabContactPersons, additionalContactPersons } = this.state
    const spSelectDisabled = isEmpty(idp) || sps.length === 0
    const submitClassName = this.isValidForSubmit() ? '' : 'disabled'
    return (
      <div>
        <Flash />
        <div className="l-mini mod-invite-request">
          <p className="info" dangerouslySetInnerHTML={{ __html: I18n.t('invite_request.info') }} />
          <section className="mod-invite-request-inner">
            <label>{I18n.t('invite_request.idp')}</label>
            <SelectWrapper
              handleChange={this.onIdpChange}
              placeholder={I18n.t('invite_request.selectIdp')}
              defaultValue={idpId}
              isClearable={false}
              multiple={false}
              options={idps.map((provider) => ({ display: provider.name, value: provider.id }))}
            />
            <label>{I18n.t('invite_request.sp')}</label>
            <SelectWrapper
              handleChange={this.onSpChange}
              placeholder={
                spSelectDisabled ? I18n.t('invite_request.selectSpDisabled') : I18n.t('invite_request.selectSp')
              }
              defaultValue={spId}
              isClearable={true}
              multiple={false}
              isDisabled={spSelectDisabled}
              options={sps.map((provider) => ({
                display: provider.name,
                value: provider.spEntityId,
              }))}
            />
            {idp &&
              spId &&
              this.renderContactPersons(idp.contactPersons, sabContactPersons, additionalContactPersons, idp.name)}
            <label>{I18n.t('invite_request.message')}</label>
            <textarea
              name="message"
              cols="30"
              value={message}
              rows="10"
              onChange={(e) => this.setState({ message: e.target.value })}
            />
            <div className="buttons">
              <a href="/send" className={`t-button save ${submitClassName}`} onClick={this.sendRequest}>
                {I18n.t('invite_request.sendRequest')}
              </a>
            </div>
          </section>
        </div>
      </div>
    )
  }
}

InviteRequest.contextTypes = {
  router: PropTypes.object,
}

export default withRouter(InviteRequest)
