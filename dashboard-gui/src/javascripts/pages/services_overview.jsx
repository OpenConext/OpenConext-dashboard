import React, { useEffect, useState, useContext } from 'react'

import I18n from 'i18n-js'
import { getApps } from '../api'
import AppList from '../components/app_list'
import Breadcrumbs from '../components/breadcrumbs'
import Tab from '../components/tab'
import TabBar from '../components/tab_bar'
import { ReactComponent as ConnectedServiceIcon } from '../../images/tags-favorite-star.svg'
import { ReactComponent as AllServiceIcon } from '../../images/app-window-cloud.svg'
import { CurrentUserContext } from '../App'

export default function ServicesOverview({ connected = false }) {
  const breadcrumbs = [{ link: '/apps/connected', text: 'Home' }]

  if (!connected) {
    breadcrumbs.push({ link: '/apps/all', text: I18n.t('apps.overview.all_services') })
  }

  const [apps, setApps] = useState([])
  const [facets, setFacets] = useState([])
  const { currentUser } = useContext(CurrentUserContext)

  async function fetchApps() {
    const apps = await getApps()
    const idpState = currentUser.getCurrentIdp().state

    const filteredApps = apps.payload.apps
      .filter((app) => app.state === idpState)
      .filter((app) => !(currentUser.guest && app.idpVisiblyOnly))
      .filter((app) => !connected || app.connected)

    setApps(filteredApps)
    setFacets(apps.payload.facets)
  }

  useEffect(() => {
    fetchApps()
  }, [])

  return (
    <>
      <Breadcrumbs items={breadcrumbs} />
      <TabBar>
        <Tab active={connected} to="/apps/connected">
          <ConnectedServiceIcon focusable="true" />
          {I18n.t('apps.overview.connected_services')}
        </Tab>
        <Tab active={!connected} to="/apps/all">
          <AllServiceIcon focusable="true" />
          {I18n.t('apps.overview.all_services')}
        </Tab>
      </TabBar>
      <div className="container">
        <AppList apps={apps} facets={facets} currentUser={currentUser} connected={connected} />
      </div>
    </>
  )
}
