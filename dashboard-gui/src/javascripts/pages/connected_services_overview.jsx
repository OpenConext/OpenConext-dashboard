import React, { useEffect, useState, useContext } from 'react'

import { getApps } from '../api'
import AppList from '../components/app_list'
import Breadcrumbs from '../components/breadcrumbs'
import Tab from '../components/tab'
import TabBar from '../components/tab_bar'
import { ReactComponent as ConnectedServiceIcon } from '../../images/tags-favorite-star.svg'
import { ReactComponent as AllServiceIcon } from '../../images/app-window-cloud.svg'
import { CurrentUserContext } from '../App'

export default function ConnectedServicesOverview() {
  const breadcrumbs = [{ link: '/apps/connected', text: 'Home' }]
  const [apps, setApps] = useState([])
  const [facets, setFacets] = useState([])
  const { currentUser } = useContext(CurrentUserContext)

  async function fetchApps() {
    const apps = await getApps()
    const idpState = currentUser.getCurrentIdp().state

    const filteredApps = apps.payload.apps
      .filter((app) => app.state === idpState)
      .filter((app) => !(currentUser.guest && app.idpVisiblyOnly))
      .filter((app) => app.connected)

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
        <Tab active to="/apps/connected">
          <ConnectedServiceIcon />
          Connected Services
        </Tab>
        <Tab to="/apps">
          <AllServiceIcon />
          All Services
        </Tab>
      </TabBar>
      <div className="container">
        <AppList apps={apps} facets={facets} currentUser={currentUser} />
      </div>
    </>
  )
}
