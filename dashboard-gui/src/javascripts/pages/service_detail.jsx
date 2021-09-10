import React, { useContext, useState, useEffect } from 'react'
import I18n from 'i18n-js'
import { useParams, useLocation } from 'react-router-dom'
import { getApp, getPolicies } from '../api'
import Breadcrumbs from '../components/breadcrumbs'
import ServiceHeader from '../components/service_header'
import Tab from '../components/tab'
import TabBar from '../components/tab_bar'
import AboutService from './about_service'
import AttributesAndPrivacy from './attributes_and_privacy'
import ResourceServers from './resource_servers'
import Statistics from './statistics'
import Settings from './settings'
import { Route, Switch, useRouteMatch } from 'react-router-dom'
import { CurrentUserContext } from '../App'

export default function ServiceDetail() {
  const { id, type } = useParams()
  const [app, setApp] = useState(null)
  const [policies, setPolicies] = useState([])
  const { path } = useRouteMatch()
  const location = useLocation()
  const pathElements = location.pathname.split('/')
  const currentPath = pathElements[pathElements.length - 1]
  const { currentUser } = useContext(CurrentUserContext)

  async function fetchApp() {
    try {
      const data = await getApp(id, type)
      setApp(data.payload)
      const res = await getPolicies()
      const policiesForApp = res.payload.filter((policy) => policy.serviceProviderId === data.payload.spEntityId)
      setPolicies(policiesForApp)
    } catch (e) {
      setPolicies([])
    }
  }

  useEffect(() => {
    fetchApp()
  }, [id, type])

  if (!app) {
    return null
  }

  const breadcrumbs = [
    { link: '/apps/connected', text: 'Home' },
    { link: `/apps/${id}/${type}/about`, text: app.name },
  ]

  const isAllowedToMaintainPolicies =
    currentUser.dashboardAdmin || currentUser.getCurrentIdp().allowMaintainersToManageAuthzRules
  const showConsent =
    app.connected && currentUser.manageConsentEnabled && !currentUser.guest && !currentUser.dashboardMember
  const isViewerOrAdmin = currentUser.dashboardAdmin || currentUser.dashboardViewer || currentUser.superUser

  const showSsid = app.connected && !currentUser.guest && !currentUser.dashboardMember

  return (
    <div className="app-detail">
      <Breadcrumbs items={breadcrumbs} />
      <ServiceHeader app={app} policies={policies} />
      <TabBar>
        <Tab
          active={
            pathElements.includes('about') ||
            pathElements.includes('overview') ||
            pathElements.includes('how_to_connect')
          }
          to={`/apps/${id}/${type}/about`}
        >
          {I18n.t('apps.tabs.about')}
        </Tab>
        <Tab active={currentPath === 'attributes_and_privacy'} to={`/apps/${id}/${type}/attributes_and_privacy`}>
          {I18n.t('apps.tabs.attributes')}
        </Tab>
        {app.resourceServers && app.resourceServers.length > 0 && (
          <Tab active={currentPath === 'resource_servers'} to={`/apps/${id}/${type}/resource_servers`}>
            {I18n.t('apps.tabs.resource_servers')}
          </Tab>
        )}
        {currentUser.showStats() && (
          <Tab active={currentPath === 'statistics'} to={`/apps/${id}/${type}/statistics`}>
            {I18n.t('apps.tabs.statistics')}
          </Tab>
        )}
        {(isViewerOrAdmin || showConsent || showSsid) && (
          <Tab active={pathElements.includes('settings')} to={`/apps/${id}/${type}/settings`}>
            {I18n.t('apps.tabs.settings')}
          </Tab>
        )}
      </TabBar>
      <div className="container">
        <Switch>
          <Route path={`${path}/settings`}>
            <Settings
              app={app}
              type={type}
              isViewerOrAdmin={isViewerOrAdmin}
              isAllowedToMaintainPolicies={isAllowedToMaintainPolicies}
              showConsent={showConsent}
              showSsid={showSsid}
              onPolicyChange={() => fetchApp()}
            />
          </Route>
          <Route exact path={`${path}/attributes_and_privacy`}>
            <AttributesAndPrivacy app={app} />
          </Route>
          <Route exact path={`${path}/resource_servers`}>
            <ResourceServers app={app} />
          </Route>
          {currentUser.showStats() && (
            <Route exact path={`${path}/statistics`}>
              <Statistics app={app} />
            </Route>
          )}
          <Route>
            <AboutService app={app} type={type} />
          </Route>
        </Switch>
      </div>
    </div>
  )
}
