import React, { useContext, useState } from 'react'
import I18n from 'i18n-js'
import { CurrentUserContext } from '../App'
import { isEmpty } from '../utils/utils'
import SelectWrapper from '../components/select_wrapper'
import { surfSecureIdChangeRequest } from '../api'
import stopEvent from '../utils/stop'
import { setFlash } from '../utils/flash'
import StepUpModal from "../components/step_up_modal"
import ConnectModalContainer from "../components/connect_modal_container";

export default function SurfSecureID({ app }) {
  const { currentUser } = useContext(CurrentUserContext)
  const stepEntity = (currentUser.currentIdp.stepupEntities || []).find((e) => e.name === app.spEntityId)
  const initialLoaLevel = app.minimalLoaLevel || (stepEntity && stepEntity.level) || ''

  const [loaLevel, setLoaLevel] = useState(initialLoaLevel)
  const [showStepUpModal, setShowStepUpModal] = useState(false)
  const [showJiraDownModal, setShowJiraDownModal] = useState(false)
  const [serverBusy, setServerBusy] = useState(false)
  const isDashboardAdmin = currentUser.dashboardAdmin
  const appHasLoaLevel = !isEmpty(app.minimalLoaLevel)
  const loaLevelEquals = stepEntity && stepEntity.level === loaLevel

  let options = []
  options.push({ value: '', display: I18n.t('consent_panel.defaultLoa') })

  options = options.concat(
    currentUser.loaLevels.map((t) => ({
      value: t,
      display: I18n.t(`consent_panel.${t.substring(t.lastIndexOf('/') + 1).replaceAll('.','_').toLowerCase()}`),
    }))
  )

  const checkLoaLevel = callback => {
    if (currentUser.currentLoaLevel < 3 && currentUser.dashboardStepupEnabled) {
      setShowStepUpModal(true)
    } else {
      callback();
    }
  }

  function saveRequest(e) {
    stopEvent(e)

    if (currentUser.jiraDown) {
        setShowJiraDownModal(true)
        return
    }

    setServerBusy(true)
    surfSecureIdChangeRequest({ entityId: app.spEntityId, loaLevel: loaLevel, entityType: app.entityType })
      .then((res) => {
        res.json().then((action) => {
          if (action.payload['no-changes']) {
            setFlash(I18n.t('my_idp.no_change_request_created'), 'warning')
          } else {
            setFlash(I18n.t('my_idp.change_request_created', { jiraKey: action.payload.jiraKey }))
          }
          setServerBusy(false)
          window.scrollTo(0, 0)
        })
      })
      .catch(() => {
        setFlash(I18n.t('my_idp.change_request_failed'), 'error')
        setServerBusy(false)
        window.scrollTo(0, 0)
      })
  }

    const loaRequired = currentUser.currentLoaLevel < 3 && currentUser.dashboardStepupEnabled

    return (
    <div>
        <div className={"title-container"}>
            <h2>{I18n.t('ssid_panel.title')}</h2>
            {(isDashboardAdmin && loaRequired) &&
                <button className={`c-button save larger`}
                        disabled={serverBusy}
                        onClick={e => checkLoaLevel(() => saveRequest(e))}>
                    {I18n.t('consent_panel.request')}
                </button>}
        </div>
      <p dangerouslySetInnerHTML={{ __html: I18n.t('ssid_panel.subtitle') }} />
      <p className="info" dangerouslySetInnerHTML={{ __html: I18n.t('ssid_panel.subtitle2') }} />
      {isDashboardAdmin && <p className="info" dangerouslySetInnerHTML={{ __html: I18n.t('ssid_panel.subtitle3') }} />}
      <div className="mod-ssid-panel">
        {appHasLoaLevel && <p className="error">{I18n.t('ssid_panel.appHasLoaLevel')}</p>}
        <section className="change-form">
          <label htmlFor="loa-level">{I18n.t('consent_panel.loa_level')}</label>
            {!loaRequired && <SelectWrapper
            defaultValue={loaLevel}
            options={options}
            multiple={false}
            inputId="loa-level"
            isDisabled={!isDashboardAdmin || appHasLoaLevel || loaRequired}
            handleChange={(val) => setLoaLevel(val)}
          />}
            {loaRequired && <input
                type="text"
                id="consent-type"
                value={loaLevel || I18n.t('consent_panel.defaultLoa')}
                disabled={true}
            />}
          {(isDashboardAdmin && !appHasLoaLevel && !loaRequired) && (
            <button
              className={`c-button save ${(loaLevelEquals || serverBusy) ? 'disabled' : ''}`}
              disabled={serverBusy}
              onClick={e => checkLoaLevel(() => saveRequest(e))}>
                {serverBusy && <div id="service-loader-id" className="loader"/>}
                {I18n.t('consent_panel.save')}
            </button>
          )}
        </section>
      </div>
      <StepUpModal
          app={app}
          isOpen={showStepUpModal}
          onClose={() => setShowStepUpModal(false)}
          requiredLoaLevel={3}
      />
        <ConnectModalContainer isOpen={showJiraDownModal} onClose={() => setShowJiraDownModal(false)}>
            <div>
                <div className="connect-modal-header">{I18n.t('how_to_connect_panel.jira_down')}</div>
                <div className="connect-modal-body">
                    <p dangerouslySetInnerHTML={{ __html: I18n.t('how_to_connect_panel.jira_down_description') }}/>
                </div>
                <div className="buttons">
                    <button className="c-button white" onClick={() => setShowJiraDownModal(false)}>
                        {I18n.t('how_to_connect_panel.close')}
                    </button>
                </div>
            </div>
        </ConnectModalContainer>

    </div>
  )
}
