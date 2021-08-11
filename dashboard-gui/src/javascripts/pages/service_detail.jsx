import React, { useState, useEffect } from 'react'
import I18n from 'i18n-js'
import { useParams, useLocation } from 'react-router-dom'
import { getApp } from '../api'
import Breadcrumbs from '../components/breadcrumbs'
import ServiceHeader from '../components/service_header'
import Tab from '../components/tab'
import TabBar from '../components/tab_bar'
import AboutService from './about_service'
import AttributesAndPrivacy from './attributes_and_privacy'
import ResourceServers from './resource_servers'
import Statistics from './statistics'
import { Route, Switch, useRouteMatch } from 'react-router-dom'

export default function ServiceDetail() {
  const { id, type } = useParams()
  const [app, setApp] = useState(null)
  const { path } = useRouteMatch()
  const location = useLocation()
  const pathElements = location.pathname.split('/')
  const currentPath = pathElements[pathElements.length - 1]

  async function fetchApp() {
    const data = await getApp(id, type)
    setApp(data.payload)
  }

  useEffect(() => {
    fetchApp()
  }, [])

  if (!app) {
    return null
  }

  const breadcrumbs = [
    { link: '/apps/connected', text: 'Home' },
    { link: `/apps/${id}/${type}/about`, text: app.name },
  ]

  return (
    <div className="app-detail">
      <Breadcrumbs items={breadcrumbs} />
      <ServiceHeader app={app} />
      <TabBar>
        <Tab active={currentPath === 'about'} to={`/apps/${id}/${type}/about`}>
          {I18n.t('apps.tabs.about')}
        </Tab>
        <Tab active={currentPath === 'attributes_and_privacy'} to={`/apps/${id}/${type}/attributes_and_privacy`}>
          {I18n.t('apps.tabs.attributes')}
        </Tab>
        {app.resourceServers && (
          <Tab active={currentPath === 'resource_servers'} to={`/apps/${id}/${type}/resource_servers`}>
            {I18n.t('apps.tabs.resource_servers')}
          </Tab>
        )}
        <Tab active={currentPath === 'statistics'} to={`/apps/${id}/${type}/statistics`}>
          {I18n.t('apps.tabs.statistics')}
        </Tab>
        <Tab to="/apps/all">{I18n.t('apps.tabs.settings')}</Tab>
      </TabBar>
      <div className="container">
        <Switch>
          <Route path={`${path}/about`} component={AboutService}>
            <AboutService app={app} type={type} />
          </Route>
          <Route path={`${path}/overview`} component={AboutService}>
            <AboutService app={app} type={type} />
          </Route>
          <Route exact path={`${path}/attributes_and_privacy`}>
            <AttributesAndPrivacy app={app} />
          </Route>
          <Route exact path={`${path}/resource_servers`}>
            <ResourceServers app={app} />
          </Route>
          <Route exact path={`${path}/statistics`}>
            <Statistics app={app} />
          </Route>
        </Switch>
      </div>
    </div>
  )
}
