import React, { useEffect, useState } from 'react'
import { getServicesByEntityIds } from '../api'
import I18n from 'i18n-js'
import { isEmpty } from '../utils/utils'

export default function ResourceServers({ app }) {
  const [resourceServers, setResourceServers] = useState([])

  async function fetchResourceServers() {
    const result = await getServicesByEntityIds(app.resourceServers)
    setResourceServers(result.payload)
  }

  useEffect(() => {
    try {
      if (app.resourceServers) {
        fetchResourceServers()
      }
    } catch (e) {
      console.error(e)
    }
  })

  function description(rs) {
    const descriptions = rs.descriptions
    if (isEmpty(descriptions)) {
      return ''
    }
    const alternative = I18n.locale === 'en' ? 'nl' : 'en'
    return descriptions[I18n.locale] || descriptions[alternative]
  }

  return (
    <div className="app-detail-content resource-servers">
      <h2>{I18n.t('connected_resource_servers_panel.title')}</h2>

      <p>{I18n.t('connected_resource_servers_panel.subtitle', { name: app.name })}</p>

      <table>
        <thead>
          <tr>
            <th className="clientId">{I18n.t('connected_resource_servers_panel.clientId')}</th>
            <th className="name">{I18n.t('connected_resource_servers_panel.name')}</th>
            <th>{I18n.t('connected_resource_servers_panel.description')}</th>
          </tr>
        </thead>
        <tbody>
          {resourceServers.map((rs, index) => (
            <tr key={index}>
              <td>{rs.spEntityId}</td>
              <td>{rs.names[I18n.locale]}</td>
              <td>{description(rs)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
