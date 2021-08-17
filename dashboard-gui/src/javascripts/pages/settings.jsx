import React from 'react'
import I18n from 'i18n-js'
import { Redirect, Link, Route, Switch, useLocation, useRouteMatch } from 'react-router-dom'
import Consent from './consent'
import SurfSecureID from './surf_secure_id'
import AuthorizationPolicyDetail from './authorization_policy_detail'
import AuthorizationPolicyOverview from './authorization_policy_overview'
import AuthorizationPolicyRevisions from './authorization_policy_revisions'

export default function Settings({ app, type, isAllowedToMaintainPolicies, showConsent, showSsid, onPolicyChange }) {
  const { path } = useRouteMatch()
  const location = useLocation()
  const pathElements = location.pathname.split('/')
  const currentPath = pathElements[pathElements.length - 1]

  function getDefaultPath() {
    if (showConsent) {
      return `${location.pathname}/consent`
    } else if (isAllowedToMaintainPolicies) {
      return `${location.pathname}/authorization_policies`
    } else if (showSsid) {
      return `${location.pathname}/surf_secure_id`
    }

    return `/apps/${app.id}/${type}/about`
  }

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
          className={pathElements.includes('authorization_policies') ? 'active' : ''}
          to={`/apps/${app.id}/${type}/settings/authorization_policies`}
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
          {isAllowedToMaintainPolicies && (
            <Route path={`${path}/authorization_policies/:id/revisions`}>
              <AuthorizationPolicyRevisions app={app} type={type} />
            </Route>
          )}
          {isAllowedToMaintainPolicies && (
            <Route path={`${path}/authorization_policies/:id`}>
              <AuthorizationPolicyDetail app={app} type={type} onPolicyChange={onPolicyChange} />
            </Route>
          )}
          {isAllowedToMaintainPolicies && (
            <Route path={`${path}/authorization_policies`}>
              <AuthorizationPolicyOverview app={app} type={type} onPolicyChange={onPolicyChange} />
            </Route>
          )}
          {showSsid && (
            <Route path={`${path}/surf_secure_id`}>
              <SurfSecureID app={app} />
            </Route>
          )}
          <Route path={path} render={() => <Redirect to={getDefaultPath()} />} />
        </Switch>
      </div>
    </div>
  )
}
