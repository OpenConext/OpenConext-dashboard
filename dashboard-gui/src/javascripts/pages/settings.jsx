import React, { useContext } from 'react'
import I18n from 'i18n-js'
import { Link, Route, Switch, useLocation, useRouteMatch } from 'react-router-dom'
import Consent from './consent'
import { CurrentUserContext } from '../App'
import SurfSecureID from './surf_secure_id'

export default function Settings({ app, type }) {
  const { path } = useRouteMatch()
  const location = useLocation()
  const pathElements = location.pathname.split('/')
  const currentPath = pathElements[pathElements.length - 1]
  const { currentUser } = useContext(CurrentUserContext)

  const showConsent =
    app.connected && currentUser.manageConsentEnabled && !currentUser.guest && !currentUser.dashboardMember

  const showSsid = app.connected && !currentUser.guest && !currentUser.dashboardMember

  return (
    <div className="app-detail-content mod-settings">
      <div className="settings-menu">
        <h3>{I18n.t('apps.settings.title')}</h3>
        {showConsent && (
          <Link className={currentPath === 'consent' ? 'active' : ''} to={`/apps/${app.id}/${type}/settings/consent`}>
            {I18n.t('apps.settings.menu.consent')}
          </Link>
        )}
        <Link
          className={currentPath === 'authorization_policy' ? 'active' : ''}
          to={`/apps/${app.id}/${type}/settings/authorization_policy`}
        >
          {I18n.t('apps.settings.menu.authorization_policy')}
        </Link>
        {showSsid && (
          <Link
            className={currentPath === 'surf_secure_id' ? 'active' : ''}
            to={`/apps/${app.id}/${type}/settings/surf_secure_id`}
          >
            {I18n.t('apps.settings.menu.surf_secure_id')}
          </Link>
        )}
      </div>
      <div className="settings-content-container">
        <Switch>
          {showConsent && (
            <Route path={`${path}/consent`}>
              <Consent app={app} />
            </Route>
          )}
          <Route path={`${path}/authorization_policy`}>policy</Route>
          {showSsid && (
            <Route path={`${path}/surf_secure_id`}>
              <SurfSecureID app={app} />
            </Route>
          )}
        </Switch>
      </div>
    </div>
  )
}
