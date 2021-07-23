import React, { useContext, useEffect, useState } from 'react'
import { CurrentUserContext } from '../App'
import { searchJira } from '../api'
import I18n from 'i18n-js'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faArrowLeft, faCheck } from '@fortawesome/free-solid-svg-icons'
import { Link } from 'react-router-dom'
import ConnectModal from '../components/connect_modal'
import LicenseInfoText from '../components/license_info_text'
import { ReactComponent as LoaIcon } from '../../images/business-deal-handshake.svg'

export default function ServiceHeader({ app }) {
  const { currentUser } = useContext(CurrentUserContext)
  const [showConnectModal, setShowConnectModal] = useState(false)
  const [jiraAction, setJiraAction] = useState(null)
  const hasInvite =
    jiraAction && jiraAction.type === 'LINKINVITE' && jiraAction.status === 'Awaiting Input' && !app.connected
  const pendingAction = jiraAction && (jiraAction.status === 'To Do' || jiraAction.status === 'In Progress')
  const canConnectOrDisconnect =
    currentUser.dashboardAdmin && currentUser.getCurrentIdp().institutionId && !pendingAction

  console.log(hasInvite)
  console.log(jiraAction)
  console.log(pendingAction)

  const jiraFilter = {
    maxResults: 1,
    startAt: 0,
    spEntityId: app.spEntityId,
    statuses: ['To Do', 'In Progress', 'Awaiting Input'],
    types: ['LINKREQUEST', 'UNLINKREQUEST', 'LINKINVITE'],
    key: null,
  }

  async function fetchJira() {
    if (app && !currentUser.guest && !currentUser.dashboardMember) {
      const res = await searchJira(jiraFilter)

      if (res.payload.total > 0) {
        const nonRejected = res.payload.issues.filter(
          (action) => !action.rejected && action.service && action.service.id === app.id
        )
        if (nonRejected.length > 0) {
          setJiraAction(nonRejected[nonRejected.length - 1])
        }
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
              <Link to="/apps/connected">
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
              <div className="right">
                {pendingAction && !app.connected && (
                  <button disabled className="c-button">
                    Pending connection request...
                  </button>
                )}
                {pendingAction && app.connected && (
                  <button disabled className="c-button">
                    Pending disconnect request...
                  </button>
                )}
                {!pendingAction && app.connected && (
                  <button disabled={!canConnectOrDisconnect} className="g-button">
                    <FontAwesomeIcon icon={faCheck} />
                    Connected
                  </button>
                )}
                {!pendingAction && !app.connected && (
                  <button
                    disabled={!canConnectOrDisconnect}
                    className="c-button"
                    onClick={() => setShowConnectModal(true)}
                  >
                    Connect this service
                  </button>
                )}
                {app.connected && (
                  <div className="connection-details">
                    {app.minimalLoaLevel && (
                      <div className="loa">
                        <div className="green-dot"></div>
                        <LoaIcon /> {app.minimalLoaLevel.split('/').pop()}
                      </div>
                    )}
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
        <ConnectModal
          app={app}
          currentUser={currentUser}
          isOpen={showConnectModal}
          onSubmit={fetchJira}
          onClose={() => setShowConnectModal(false)}
        />
      </div>
    </>
  )
}

function JiraActionMessage({ action, app }) {
  console.log(action, app)
  if (!action) {
    return null
  }
  function determineMessage() {
    let message = I18n.t('apps.detail.outstandingIssue', {
      jiraKey: action.jiraKey,
      type: I18n.t('history.action_types_name.' + action.type),
      status: I18n.t('history.statuses.' + action.status),
    })
    return message
  }
  const message = determineMessage()
  return (
    <div className="action-message">
      <div className="container">{message}</div>
    </div>
  )
}
