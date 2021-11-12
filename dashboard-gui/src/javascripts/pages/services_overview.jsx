import React, { useEffect, useState, useContext } from 'react'
import Helmet from 'react-helmet'

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
  const [loadingApps, setLoadingApps] = useState(true)
  const [facets, setFacets] = useState([])
  const { currentUser } = useContext(CurrentUserContext)

  async function fetchApps() {
    const apps = await getApps()
    const idpState = currentUser.getCurrentIdp().state

    const filteredApps = apps.payload.apps
      .filter((app) => app.state === idpState)
      .filter((app) => !(currentUser.guest && app.idpVisibleOnly))
      .filter((app) => !connected || app.connected)

    setApps(filteredApps)
    setFacets(apps.payload.facets)
    setLoadingApps(false)
  }

  useEffect(() => {
    fetchApps()
  }, [])

  return (
    <>
      <Helmet title={connected ? I18n.t('apps.overview.connected_services') : I18n.t('apps.overview.all_services')} />
      {!currentUser.guest && (
        <>
          <Breadcrumbs items={breadcrumbs} />
          <TabBar>
            <Tab active={connected} to="/apps/connected">
              <ConnectedServiceIcon focusable="true" title={I18n.t('apps.overview.connected_services')} />
              {I18n.t('apps.overview.connected_services')}
            </Tab>
            <Tab active={!connected} to="/apps/all">
              <AllServiceIcon focusable="true" title={I18n.t('apps.overview.all_services')} />
              {I18n.t('apps.overview.all_services')}
            </Tab>
          </TabBar>
        </>
      )}
      <div className="container">
        {loadingApps ? (
          <div className="loader-container">
            <div id="service-loader-id" className="loader"></div>
            {I18n.t('loader.loading')}
          </div>
        ) : (
          <AppList apps={apps} facets={facets} currentUser={currentUser} connected={connected} />
        )}
      </div>
    </>
  )
}
