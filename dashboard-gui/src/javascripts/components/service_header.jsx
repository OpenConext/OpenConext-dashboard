import React, { useContext, useState } from 'react'
import { CurrentUserContext } from '../App'
import I18n from 'i18n-js'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faArrowLeft, faCheck } from '@fortawesome/free-solid-svg-icons'
import { Link } from 'react-router-dom'
import ConnectModal from '../components/connect_modal'
import LicenseInfoText from '../components/license_info_text'
import { ReactComponent as LoaIcon } from '../../images/business-deal-handshake.svg'

export default function ServiceHeader({ app }) {
  const { currentUser } = useContext(CurrentUserContext)
  const canConnectOrDisconnect = currentUser.dashboardAdmin && currentUser.getCurrentIdp().institutionId
  const [showConnectModal, setShowConnectModal] = useState(false)

  return (
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
              {app.connected ? (
                <button disabled={!canConnectOrDisconnect} className="g-button">
                  <FontAwesomeIcon icon={faCheck} />
                  Connected
                </button>
              ) : (
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
        onClose={() => setShowConnectModal(false)}
      />
    </div>
  )
}
