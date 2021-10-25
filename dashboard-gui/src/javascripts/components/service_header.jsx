import React, { useContext, useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { CurrentUserContext } from '../App'
import { searchJira } from '../api'
import I18n from 'i18n-js'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faArrowLeft, faCheck } from '@fortawesome/free-solid-svg-icons'
import { Link } from 'react-router-dom'
import ConnectModal from '../components/connect_modal'
import DenyInviteModal from '../components/deny_invite_modal'
import DisconnectModal from '../components/disconnect_modal'
import LicenseInfoText from '../components/license_info_text'
import { ReactComponent as LoaIcon } from '../../images/business-deal-handshake.svg'
import { ReactComponent as PolicyIcon } from '../../images/door-lock.svg'
import { getBackPath } from '../utils/back_path'

export default function ServiceHeader({ app, policies, onSubmit }) {
  const { currentUser } = useContext(CurrentUserContext)
  const params = useParams()
  const [showConnectModal, setShowConnectModal] = useState(false)
  const [showDisconnectModal, setShowDisconnectModal] = useState(false)
  const [showDenyModal, setShowDenyModal] = useState(false)
  const [jiraAction, setJiraAction] = useState(null)
  const hasInvite =
    jiraAction && jiraAction.type === 'LINKINVITE' && jiraAction.status === 'Awaiting Input' && !app.connected
  const pendingAction = jiraAction && (jiraAction.status === 'To Do' || jiraAction.status === 'In Progress')
  const canConnectOrDisconnect =
    currentUser.dashboardAdmin && currentUser.getCurrentIdp().institutionId && !pendingAction

  const refresh = () => {
    fetchJira()
    onSubmit()
  }

  const jiraFilter = {
    maxResults: 1,
    startAt: 0,
    spEntityId: app.spEntityId,
    statuses: params.jiraKey ? [] : ['To Do', 'In Progress', 'Awaiting Input'],
    types: ['LINKREQUEST', 'UNLINKREQUEST', 'LINKINVITE'],
    key: params.jiraKey || null,
  }

  async function fetchJira() {
    if (app && !currentUser.guest && !currentUser.dashboardMember) {
      const res = await searchJira(jiraFilter)

      if (res.payload.total > 0) {
        const nonRejected = res.payload.issues.filter((action) => !action.rejected && action.spEid === app.id)
        if (nonRejected.length > 0) {
          setJiraAction(nonRejected[nonRejected.length - 1])
        } else {
          setJiraAction(null)
        }
      } else {
        setJiraAction(null)
      }
    }
  }

  useEffect(() => {
    fetchJira()
  }, [])

  return (
    <>
      <JiraActionMessage action={jiraAction} app={app} />
      <div className="mod-service-header">
        <div className="container">
          <div className="header-wrapper">
            <div className="arrow-container">
              <Link to={getBackPath} aria-label="back">
                <FontAwesomeIcon icon={faArrowLeft} />
              </Link>
            </div>
            <div className="service-info-container">
              <div className="left">
                {app.detailLogoUrl && (
                  <div className="logo">
                    <img src={app.detailLogoUrl} alt={app.name} />
                  </div>
                )}
                <div className="service-info">
                  <h1>{app.name}</h1>
                  {app.organisation && <div className="organisation">{app.organisation}</div>}
                  {app.spEntityId && (
                    <div className="entity-id">
                      {app.entityType === 'oidc10_rp'
                        ? I18n.t('overview_panel.rpClientID')
                        : I18n.t('overview_panel.entityID')}
                      : {app.spEntityId}
                    </div>
                  )}
                  <LicenseInfoText app={app} />
                </div>
              </div>
              {!currentUser.guest && (
                <div className="right">
                  {pendingAction && !app.connected && (
                    <button disabled className="c-button">
                      {I18n.t('apps.detail.pending_connection')}
                    </button>
                  )}
                  {pendingAction && app.connected && (
                    <button disabled className="c-button">
                      {I18n.t('apps.detail.pending_disconnect')}
                    </button>
                  )}
                  {!pendingAction && app.connected && (
                    <button
                      disabled={!canConnectOrDisconnect}
                      className="g-button"
                      onClick={() => setShowDisconnectModal(true)}
                    >
                      <FontAwesomeIcon icon={faCheck} />
                      {I18n.t('apps.detail.connected')}
                    </button>
                  )}
                  {!pendingAction && !app.connected && !hasInvite && (
                    <button
                      disabled={!canConnectOrDisconnect}
                      className="c-button"
                      onClick={() => setShowConnectModal(true)}
                    >
                      {I18n.t('apps.detail.connect_service')}
                    </button>
                  )}
                  {!pendingAction && !app.connected && hasInvite && (
                    <div className="approve-deny">
                      <button
                        disabled={!canConnectOrDisconnect}
                        className="g-button"
                        onClick={() => setShowConnectModal(true)}
                      >
                        {I18n.t('apps.detail.approve_invite')}
                      </button>
                      <button
                        disabled={!canConnectOrDisconnect}
                        className="red-button deny-invite"
                        onClick={() => setShowDenyModal(true)}
                      >
                        {I18n.t('apps.detail.deny_invite')}
                      </button>
                    </div>
                  )}
                  {app.connected && (
                    <div className="connection-details">
                      {app.minimalLoaLevel && (
                        <div className="loa">
                          <div className="green-dot"></div>
                          <LoaIcon /> {app.minimalLoaLevel.split('/').pop()}
                        </div>
                      )}
                      {policies.length > 0 && (
                        <div className="loa">
                          <div className="green-dot"></div>
                          <PolicyIcon /> {I18n.t('apps.detail.policies', { count: policies.length })}
                        </div>
                      )}
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
        <ConnectModal
          app={app}
          currentUser={currentUser}
          isOpen={showConnectModal}
          onSubmit={refresh}
          hasInvite={hasInvite}
          existingJiraAction={jiraAction}
          onClose={() => setShowConnectModal(false)}
        />
        <DisconnectModal
          app={app}
          currentUser={currentUser}
          isOpen={showDisconnectModal}
          onSubmit={refresh}
          onClose={() => setShowDisconnectModal(false)}
        />
        <DenyInviteModal
          app={app}
          currentUser={currentUser}
          jiraAction={jiraAction}
          isOpen={showDenyModal}
          onSubmit={refresh}
          onClose={() => setShowDenyModal(false)}
        />
      </div>
    </>
  )
}

function JiraActionMessage({ action, app }) {
  const params = useParams()

  if (!action) {
    return null
  }

  function determineMessage() {
    if (params.jiraKey && action.status !== 'Awaiting Input') {
      const i18nParam = action.status === 'Closed' ? 'denied' : 'approved'
      return I18n.t('apps.detail.inviteAlreadyProcessed', {
        jiraKey: action.jiraKey,
        action: I18n.t(`apps.detail.${i18nParam}`),
      })
    } else if (params.jiraKey && app.connected) {
      return I18n.t('how_to_connect_panel.invite_action_collision', {
        app: app.name,
        jiraKey: params.jiraKey,
      })
    }

    return I18n.t('apps.detail.outstandingIssue', {
      jiraKey: action.jiraKey,
      type: I18n.t('history.action_types_name.' + action.type),
      status: I18n.t('history.statuses.' + action.status),
    })
  }
  const message = determineMessage()
  return (
    <div className="action-message">
      <div className="container" dangerouslySetInnerHTML={{ __html: message }} />
    </div>
  )
}
