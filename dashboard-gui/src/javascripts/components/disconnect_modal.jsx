import React, { useState } from 'react'
import I18n from 'i18n-js'
import ConnectModalContainer from './connect_modal_container'
import CheckBox from './checkbox'
import { removeConnection } from '../api'

export default function DisconnectModal({ isOpen, onClose, app, currentUser, onSubmit }) {
  const [comments, setComments] = useState('')
  const [checked, setChecked] = useState(false)
  const [failed, setFailed] = useState(false)
  const [action, setAction] = useState(null)
  const [done, setDone] = useState(false)

  async function handleDisconnect() {
    try {
      const action = await removeConnection(app, comments)
      setAction(action)
      setDone(true)
    } catch {
      setFailed(true)
    }

    onSubmit()
  }

  if (!currentUser.dashboardAdmin) {
    return null
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

  if (done) {
    const message = action.jiraKey
      ? I18n.t('how_to_connect_panel.done_disconnect_subtitle_html_with_jira_html', {
          jiraKey: action.jiraKey,
        })
      : I18n.t('how_to_connect_panel.done_disconnect_subtitle_html')
    return (
      <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
        <div className="connect-modal-header">{I18n.t('how_to_connect_panel.done_disconnect_title')}</div>
        <div className="connect-modal-body">
          <p dangerouslySetInnerHTML={{ __html: message }} />
        </div>
        <div className="buttons">
          <button className="c-button white" onClick={onClose}>
            {I18n.t('how_to_connect_panel.close')}
          </button>
        </div>
      </ConnectModalContainer>
    )
  }

  return (
    <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
      <div className="disconnect">
        <div className="connect-modal-header">{I18n.t('how_to_connect_panel.disconnect_title', { app: app.name })}</div>
        <div className="connect-modal-body">
          <h2>{I18n.t('how_to_connect_panel.comments_title')}</h2>
          <p>{I18n.t('how_to_connect_panel.comments_description')}</p>
          <div className="grey-container">
            <textarea
              rows="5"
              value={comments}
              onChange={(e) => setComments(e.target.value)}
              placeholder={I18n.t('how_to_connect_panel.comments_placeholder')}
            />
            <CheckBox
              name="disclaimer"
              value={checked}
              info={I18n.t('how_to_connect_panel.accept_disconnect', { app: app.name })}
              onChange={(e) => setChecked(e.target.checked)}
            />
          </div>
        </div>
      </div>
      <div className="buttons">
        <button className="c-button white" onClick={onClose}>
          {I18n.t('how_to_connect_panel.cancel')}
        </button>
        <button disabled={!checked} className="c-button" onClick={handleDisconnect}>
          {I18n.t('how_to_connect_panel.disconnect')}
        </button>
      </div>
    </ConnectModalContainer>
  )
}
