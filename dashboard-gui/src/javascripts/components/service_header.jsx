import React from 'react'
import I18n from 'i18n-js'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons'
import { Link } from 'react-router-dom'
import LicenseInfoText from '../components/license_info_text'

export default function ServiceHeader({ app }) {
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
            <div className="right">Connect</div>
          </div>
        </div>
      </div>
    </div>
  )
}
