import React, { useState } from 'react'
import I18n from 'i18n-js'
import ConnectModalContainer from './connect_modal_container'
import { updateInviteRequest } from '../api'

export default function DenyInviteModal({ isOpen, onClose, app, jiraAction, onSubmit }) {
  const [comments, setComments] = useState('')
  const [failed, setFailed] = useState(false)

  async function handleDeny() {
    try {
      await updateInviteRequest({ status: 'REJECTED', jiraKey: jiraAction.jiraKey, comment: comments })
      onSubmit()
      onClose()
    } catch {
      setFailed(true)
    }
  }

  if (failed) {
    return (
      <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
        <div>
          <div className="connect-modal-header">{I18n.t('how_to_connect_panel.jira_unreachable')}</div>
          <div className="connect-modal-body">
            <p>{I18n.t('how_to_connect_panel.jira_unreachable_description')} </p>
          </div>
          <div className="buttons">
            <button className="c-button white" onClick={onClose}>
              {I18n.t('how_to_connect_panel.close')}
            </button>
          </div>
        </div>
      </ConnectModalContainer>
    )
  }

  return (
    <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
      <div className="connect-modal-header">{I18n.t('how_to_connect_panel.deny_invitation', { app: app.name })}</div>
      <div className="connect-modal-body">
        <p>{I18n.t('how_to_connect_panel.deny_invitation_info')}</p>
        <p>{I18n.t('how_to_connect_panel.comments_description')}</p>
        <div className="grey-container">
          <textarea
            rows="5"
            value={comments}
            onChange={(e) => setComments(e.target.value)}
            placeholder={I18n.t('how_to_connect_panel.comments_placeholder')}
          />
        </div>
      </div>
      <div className="buttons">
        <button className="c-button white" onClick={onClose}>
          {I18n.t('how_to_connect_panel.cancel')}
        </button>
        <button className="c-button" onClick={handleDeny}>
          {I18n.t('apps.detail.deny_invite')}
        </button>
      </div>
    </ConnectModalContainer>
  )
}
