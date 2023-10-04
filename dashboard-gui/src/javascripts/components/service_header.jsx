import React, { useContext, useEffect, useState } from 'react'
import {useHistory} from 'react-router-dom'
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
import StepUpModal from "./step_up_modal";
import {login} from "../utils/utils";
import stopEvent from "../utils/stop";

export default function ServiceHeader({ app, policies, onSubmit }) {
  const { currentUser } = useContext(CurrentUserContext)
  const history = useHistory()
  const [showConnectModal, setShowConnectModal] = useState(false)
  const [showDisconnectModal, setShowDisconnectModal] = useState(false)
  const [showDenyModal, setShowDenyModal] = useState(false)
  const [showStepUpModal, setShowStepUpModal] = useState(false)
  const [afterStepUpPath, setAfterStepUpPath] = useState(null)
  const [jiraAction, setJiraAction] = useState(null)
  const hasInvite =
    jiraAction && ((jiraAction.type === 'LINKINVITE' && jiraAction.status === 'Waiting for customer' && !app.connected) ||
          (jiraAction.type === 'UNLINKINVITE' && jiraAction.status === 'Waiting for customer' && app.connected))
  const pendingAction = jiraAction && (jiraAction.status === 'Open' || jiraAction.status === 'In Progress')
  const connectedButJiraNotResolved = pendingAction && (jiraAction.type === 'LINKINVITE' || jiraAction.type === 'LINKREQUEST') && app.connected && !hasInvite
  const notConnectedButJiraNotResolved = pendingAction && jiraAction.type === 'UNLINKREQUEST' && !app.connected && !hasInvite
  const pendingConnectionJira = pendingAction && (jiraAction.type === 'LINKINVITE' || jiraAction.type === 'LINKREQUEST') && !app.connected && !hasInvite
  const pendingUnlinkAction = pendingAction && (jiraAction.type === 'UNLINKREQUEST' || jiraAction.type === 'UNLINKINVITE') && app.connected && !hasInvite
  const currentIdp = currentUser.getCurrentIdp()
  const canConnectOrDisconnect =
    currentUser.dashboardAdmin && currentIdp.institutionId && currentIdp.state !== 'testaccepted' && !pendingAction
  const [loading, setLoading] = useState(true)


  const refresh = () => {
    fetchJira()
    onSubmit()
    history.replace(`/apps/${app.id}/${app.entityType}`)
  }
  const paths = window.location.href.split("/");
  const optionalJiraKey = paths[paths.length - 2]
  const jiraKey = optionalJiraKey && optionalJiraKey.startsWith("CXT") ? optionalJiraKey : null;
  const jiraFilter = {
    maxResults: 1,
    startAt: 0,
    spEntityId: app.spEntityId,
    statuses: jiraKey ? [] : ['Waiting for Acceptance', 'Open', 'In Progress', 'Waiting for customer'],
    types: ['LINKREQUEST', 'UNLINKREQUEST', 'LINKINVITE', 'UNLINKINVITE'],
    key: jiraKey,
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
    } else if ((currentUser.guest || currentUser.currentLoaLevel < 2) && jiraKey) {
      login(null, 2)
      return
    }
    setLoading(false)
  }

  useEffect(() => {
    fetchJira()
    const urlSearchParams = new URLSearchParams(window.location.search);
    const afterStepup = urlSearchParams.get('afterStepup')
    if (afterStepup === 'connect') {
      setShowConnectModal(true)
    } else if (afterStepup === 'disconnect') {
      setShowDisconnectModal(true)
    } else if (afterStepup === 'deny') {
      setShowDenyModal(true)
    }
  }, [])

  const checkLoaLevel = (afterStepUpPathParameter, callback) => {
    if (currentUser.currentLoaLevel < 2 && currentUser.dashboardStepupEnabled) {
      setShowStepUpModal(true)
      setAfterStepUpPath(afterStepUpPathParameter)
    } else {
      callback();
    }
  }
  if (loading) {
    return null
  }
  return (
    <>
      <JiraActionMessage action={jiraAction} app={app} jiraKey={jiraKey}/>
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
                  {(connectedButJiraNotResolved || pendingConnectionJira) &&
                    <button disabled
                            className="c-button">
                      {I18n.t('apps.detail.pending_connection')}
                    </button>
                  }
                  {(notConnectedButJiraNotResolved || pendingUnlinkAction) &&
                    <button disabled
                            className="c-button">
                      {I18n.t('apps.detail.pending_disconnect')}
                    </button>
                  }
                  {(!pendingAction && app.connected && !hasInvite) &&
                    <button
                      disabled={!canConnectOrDisconnect}
                      className="g-button"
                      onClick={() => checkLoaLevel("disconnect",() => setShowDisconnectModal(true))}
                    >
                      <FontAwesomeIcon icon={faCheck} />
                      {I18n.t('apps.detail.connected')}
                    </button>
                  }
                  {(!pendingAction && !app.connected && !hasInvite) &&
                    <button
                      disabled={!canConnectOrDisconnect}
                      className="c-button"
                      onClick={() => checkLoaLevel("connect",() => setShowConnectModal(true))}
                    >
                      {I18n.t(`apps.detail.${app.entityType === 'single_tenant_template' ? 'connect_service_single_tenant':'connect_service'}`)}
                    </button>
                  }
                  {(!pendingAction && !app.connected && hasInvite) &&
                    <div className="approve-deny">
                      <button
                        disabled={!canConnectOrDisconnect}
                        className="g-button"
                        onClick={() => checkLoaLevel("connect",() => setShowConnectModal(true))}
                      >
                        {I18n.t('apps.detail.approve_invite')}
                      </button>
                      <button
                        disabled={!canConnectOrDisconnect}
                        className="red-button deny-invite"
                        onClick={() => checkLoaLevel("deny",() => setShowDenyModal(true))}
                      >
                        {I18n.t('apps.detail.deny_invite')}
                      </button>
                    </div>
                  }
                  {(!pendingAction && app.connected && hasInvite) &&
                      <div className="approve-deny">
                        <button
                            disabled={!canConnectOrDisconnect}
                            className="g-button"
                            onClick={() => checkLoaLevel("disconnect",() => setShowDisconnectModal(true))}
                        >
                          {I18n.t('apps.detail.approve_disconnect_invite')}
                        </button>
                        <button
                            disabled={!canConnectOrDisconnect}
                            className="red-button deny-invite"
                            onClick={() => checkLoaLevel("deny",() => setShowDenyModal(true))}
                        >
                          {I18n.t('apps.detail.deny_disconnect_invite')}
                        </button>
                      </div>
                  }
                  {app.connected &&
                    <div className="connection-details">
                      {app.minimalLoaLevel && (
                        <div className="loa">
                          <div className="green-dot"/>
                          <LoaIcon /> {app.minimalLoaLevel.split('/').pop()}
                        </div>
                      )}
                      {policies.length > 0 && (
                        <div className="loa">
                          <div className="green-dot"/>
                          <PolicyIcon />
                          <a href="/#" onClick={e => {
                            stopEvent(e);
                            history.replace(`/apps/${app.id}/${app.entityType}/settings/authorization_policies`)
                          }}>
                            {I18n.t('apps.detail.policies', { count: policies.length })}
                          </a>
                        </div>
                      )}
                    </div>}
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
          hasInvite={hasInvite}
          existingJiraAction={jiraAction}
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
        <StepUpModal
            app={app}
            isOpen={showStepUpModal}
            queryParam={afterStepUpPath}
            onClose={() => setShowStepUpModal(false)}
        />
      </div>
    </>
  )
}

function JiraActionMessage({ action, app, jiraKey }) {
  if (!action) {
    return null
  }

  function determineMessage() {
    if (jiraKey && action.status !== 'Waiting for customer') {
      if (action.status === 'Open' || action.status === 'In Progress') {
        return I18n.t('apps.detail.inviteBeingProcessed', {
          jiraKey: action.jiraKey
        })

      } else {
        const i18nParam = (action.status === 'Closed' || action.status === 'Resolved') ? 'denied' : 'approved'
        return I18n.t('apps.detail.inviteAlreadyProcessed', {
          jiraKey: action.jiraKey,
          action: I18n.t(`apps.detail.${i18nParam}`),
        })
      }
    } else if (jiraKey && app.connected && action.type !== 'UNLINKINVITE') {
      return I18n.t('how_to_connect_panel.invite_action_collision', {
        app: app.name,
        jiraKey: jiraKey,
      })
    } else if (!jiraKey) {
      return I18n.t('apps.detail.outstandingIssue', {
        jiraKey: action.jiraKey,
        type: I18n.t('history.action_types_name.' + action.type),
        status: I18n.t('history.statuses.' + action.status),
      })
    }
    return null;
  }
  const message = determineMessage()
  return (<>
      {message && <div className="action-message">
      <div className="container" dangerouslySetInnerHTML={{ __html: message }} />
    </div>}
      </>
  )
}
