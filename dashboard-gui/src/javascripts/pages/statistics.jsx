import React from 'react'
import I18n from 'i18n-js'
import Stats from './stats'

export default function Statistics({ app }) {
  return (
    <div className="app-detail-content statistics">
      <h2>{I18n.t('apps.tabs.statistics')}</h2>
      <Stats view="minimal" sp={app.spEntityId} />
    </div>
  )
}
