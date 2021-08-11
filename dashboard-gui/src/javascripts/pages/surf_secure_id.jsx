import React, { useContext, useState } from 'react'
import I18n from 'i18n-js'
import { CurrentUserContext } from '../App'
import { isEmpty } from '../utils/utils'
import SelectWrapper from '../components/select_wrapper'
import { surfSecureIdChangeRequest } from '../api'
import stopEvent from '../utils/stop'

export default function SurfSecureID({ app }) {
  const { currentUser } = useContext(CurrentUserContext)
  const stepEntity = (currentUser.currentIdp.stepupEntities || []).find((e) => e.name === app.spEntityId)
  const initialLoaLevel = app.minimalLoaLevel || (stepEntity && stepEntity.level) || ''

  const [loaLevel, setLoaLevel] = useState(initialLoaLevel)
  const [flash, setFlash] = useState(null)

  const isDashboardAdmin = currentUser.dashboardAdmin
  const appHasLoaLevel = !isEmpty(app.minimalLoaLevel)
  const highestLoaLevel = stepEntity && stepEntity.level.endsWith('loa3')
  const loaLevelEquals = stepEntity && stepEntity.level === loaLevel

  let options = []
  if (isEmpty(loaLevel)) {
    options.push({ value: '', display: I18n.t('consent_panel.defaultLoa') })
  }
  options = options.concat(
    currentUser.loaLevels.map((t) => ({
      value: t,
      display: I18n.t(`consent_panel.${t.substring(t.lastIndexOf('/') + 1).toLowerCase()}`),
    }))
  )
  function saveRequest(e) {
    stopEvent(e)

    surfSecureIdChangeRequest({ entityId: app.spEntityId, loaLevel: loaLevel, entityType: app.entityType })
      .then((res) => {
        res.json().then((action) => {
          if (action.payload['no-changes']) {
            setFlash(I18n.t('my_idp.no_change_request_created'))
          } else {
            setFlash(I18n.t('my_idp.change_request_created', { jiraKey: action.payload.jiraKey }))
          }
          window.scrollTo(0, 0)
        })
      })
      .catch(() => {
        setFlash(I18n.t('my_idp.change_request_failed'))
        window.scrollTo(0, 0)
      })
  }

  return (
    <div>
      {flash && <div className="flash-message">{flash}</div>}
      <h2 dangerouslySetInnerHTML={{ __html: I18n.t('ssid_panel.title') }} />
      <p dangerouslySetInnerHTML={{ __html: I18n.t('ssid_panel.subtitle') }} />
      <p className="info" dangerouslySetInnerHTML={{ __html: I18n.t('ssid_panel.subtitle2') }} />
      {isDashboardAdmin && <p className="info" dangerouslySetInnerHTML={{ __html: I18n.t('ssid_panel.subtitle3') }} />}
      <div className="mod-ssid-panel">
        {appHasLoaLevel && <p className="error">{I18n.t('ssid_panel.appHasLoaLevel')}</p>}
        {highestLoaLevel && !appHasLoaLevel && (
          <p className="error" dangerouslySetInnerHTML={{ __html: I18n.t('ssid_panel.highestLoaReached') }} />
        )}
        <section className="change-form">
          <label>{I18n.t('consent_panel.loa_level')}</label>
          <SelectWrapper
            defaultValue={loaLevel}
            options={options}
            multiple={false}
            isDisabled={highestLoaLevel || !isDashboardAdmin || appHasLoaLevel}
            handleChange={(val) => setLoaLevel(val)}
          />
          {isDashboardAdmin && !highestLoaLevel && !appHasLoaLevel && (
            <button
              className={`c-button save ${loaLevelEquals ? 'disabled' : ''}`}
              disabled={loaLevelEquals}
              onClick={(e) => saveRequest(e)}
            >
              {I18n.t('consent_panel.save')}
            </button>
          )}
        </section>
      </div>
    </div>
  )
}
