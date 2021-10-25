import React from 'react'
import I18n from 'i18n-js'
import { withRouter } from 'react-router'
import PropTypes from 'prop-types'
import { resendInviteRequest, searchJira } from '../api'
import SelectWrapper from '../components/select_wrapper'
import stopEvent from '../utils/stop'
import { setFlash } from '../utils/flash'
import { isEmpty } from '../utils/utils'

class ResendInvite extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      action: null,
      message: '',
    }
  }

  componentDidMount() {
    const key = this.props.match.params.jiraKey
    searchJira({ key }).then((data) => {
      const issue = data.payload.issues[0]
      const message = issue.personalMessage || ''
      this.setState({ action: issue, message })
    })
  }

  sendRequest = (e) => {
    stopEvent(e)
    const { action, message } = this.state
    resendInviteRequest({ idpId: action.idpId, jiraKey: action.jiraKey, comments: message }).then(() => {
      setFlash(I18n.t('history.resendInvitationFlash', { jiraKey: action.jiraKey }))
      this.props.history.replace('/tickets')
    })
  }

  render() {
    const { action, message } = this.state
    if (isEmpty(action)) {
      return null
    }
    return (
      <div>
        <div className="l-mini mod-resend-invite">
          <p className="info" dangerouslySetInnerHTML={{ __html: I18n.t('invite_request.info') }} />
          <section className="mod-resend-invite-inner">
            <p>
              {I18n.t('invite_request.resend', {
                date: I18n.strftime(new Date(action.requestDate), '%-d %B %Y'),
                emailTo: action.emailTo,
                status: I18n.t(`history.statuses.${action.status}`),
              })}
            </p>
            <label>{I18n.t('invite_request.idp')}</label>
            <SelectWrapper
              isDisabled={true}
              defaultValue={action.idpId}
              isClearable={false}
              multiple={false}
              options={[{ display: action.idpName, value: action.idpId }]}
            />
            <label>{I18n.t('invite_request.sp')}</label>
            <SelectWrapper
              defaultValue={action.spId}
              multiple={false}
              isDisabled={true}
              options={[{ display: action.spName, value: action.spId }]}
            />
            <label>{I18n.t('invite_request.message')}</label>
            <textarea
              name="message"
              cols="30"
              value={message}
              rows="10"
              onChange={(e) => this.setState({ message: e.target.value })}
            />
            <div className="buttons">
              <button type="button" className={'t-button save'} onClick={this.sendRequest}>
                {I18n.t('invite_request.sendRequest')}
              </button>
            </div>
          </section>
        </div>
      </div>
    )
  }
}

ResendInvite.contextTypes = {
  router: PropTypes.object,
}

export default withRouter(ResendInvite)
