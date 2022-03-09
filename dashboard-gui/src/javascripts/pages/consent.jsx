import React, { useContext, useEffect, useState } from 'react'
import I18n from 'i18n-js'
import { CurrentUserContext } from '../App'
import { consentChangeRequest, disableConsent } from '../api'
import SelectWrapper from '../components/select_wrapper'
import ReactTooltip from 'react-tooltip'
import { consentTypes } from '../utils/utils'
import stopEvent from '../utils/stop'
import { setFlash } from '../utils/flash'
import StepUpModal from "../components/step_up_modal";

export default function Consent({ app }) {
  const [consent, setConsent] = useState(null)
  const [showStepUpModal, setShowStepUpModal] = useState(false)
  const { currentUser } = useContext(CurrentUserContext)
  const isDashboardAdmin = currentUser.dashboardAdmin
  const subTitle2 = isDashboardAdmin ? 'subtitle2' : 'subtitle2Viewer'

  async function fetchDisableConsent() {
    const response = await disableConsent()
    const consent = response.find(dc => dc.spEntityId === app.spEntityId) || {
      spEntityId: app.spEntityId,
      type: 'MINIMAL_CONSENT',
      explanationNl: '',
      explanationEn: '',
      typeMetaData: app.entityType,
    }

    setConsent(consent)
  }

  const checkLoaLevel = callback => {
    if (currentUser.currentLoaLevel === 1) {
      setShowStepUpModal(true)
    } else {
      callback();
    }
  }

  function onSave(e) {
    stopEvent(e)
    consentChangeRequest(consent)
      .then((res) => {
        res.json().then((action) => {
          if (action.payload['no-changes']) {
            setFlash(I18n.t('my_idp.no_change_request_created'), 'warning')
          } else {
            setFlash(I18n.t('my_idp.change_request_created', { jiraKey: action.payload.jiraKey }))
          }
          window.scrollTo(0, 0)
        })
      })
      .catch(() => {
        setFlash(I18n.t('my_idp.change_request_failed'), 'error')
        window.scrollTo(0, 0)
      })
  }

  useEffect(() => {
    fetchDisableConsent()
  }, [])

  if (!consent) {
    return null
  }
  const msgAllowed = consent.type.toLowerCase() !== 'no_consent'

  return (
    <div>
      <h2>{I18n.t('consent_panel.title')}</h2>
      <p>{I18n.t('consent_panel.subtitle', { name: app.name })}</p>
      <p
        className="info"
        dangerouslySetInnerHTML={{ __html: I18n.t(`consent_panel.${subTitle2}`, { name: app.name }) }}
      ></p>
      <div className="mod-consent">
        <section className="change-form">
          <label htmlFor="consent-value">
            {I18n.t('consent_panel.consent_value')}
            <i className="fa fa-info-circle" data-for="consent_value_tooltip" data-tip></i>
            <ReactTooltip id="consent_value_tooltip" type="info" class="tool-tip" effect="solid" multiline={true}>
              <span dangerouslySetInnerHTML={{ __html: I18n.t('consent_panel.consent_value_tooltip') }} />
            </ReactTooltip>
          </label>
          <SelectWrapper
            defaultValue={consent.type}
            options={consentTypes.map((t) => ({ value: t, display: I18n.t(`consent_panel.${t.toLowerCase()}`) }))}
            multiple={false}
            inputId="consent-value"
            isDisabled={!isDashboardAdmin}
            handleChange={(val) => setConsent({ ...consent, type: val })}
          />

          {msgAllowed && (
            <label htmlFor="explanation-nl">
              {I18n.t('consent_panel.explanationNl')}
              <i className="fa fa-info-circle" data-for="explanationNl_tooltip" data-tip></i>
              <ReactTooltip id="explanationNl_tooltip" type="info" class="tool-tip" effect="solid" multiline={true}>
                <span dangerouslySetInnerHTML={{ __html: I18n.t('consent_panel.explanationNl_tooltip') }} />
              </ReactTooltip>
            </label>
          )}
          {msgAllowed && (
            <input
              type="text"
              id="explanation-nl"
              value={consent.explanationNl}
              disabled={!isDashboardAdmin}
              onChange={(e) => setConsent({ ...consent, explanationNl: e.target.value })}
            />
          )}

          {msgAllowed && (
            <label htmlFor="explanation-en">
              {I18n.t('consent_panel.explanationEn')}
              <i className="fa fa-info-circle" data-for="explanationEn_tooltip" data-tip></i>
              <ReactTooltip id="explanationEn_tooltip" type="info" class="tool-tip" effect="solid" multiline={true}>
                <span dangerouslySetInnerHTML={{ __html: I18n.t('consent_panel.explanationEn_tooltip') }} />
              </ReactTooltip>
            </label>
          )}
          {msgAllowed && (
            <input
              type="text"
              id="explanation-en"
              value={consent.explanationEn}
              disabled={!isDashboardAdmin}
              onChange={(e) =>
                setConsent({
                  ...consent,
                  explanationEn: e.target.value,
                })
              }
            />
          )}

          {isDashboardAdmin && (
            <button className="c-button save"
                    onClick={e => checkLoaLevel(() => onSave(e))}>
              {I18n.t('consent_panel.save')}
            </button>
          )}
        </section>
      </div>
      <StepUpModal
          app={app}
          isOpen={showStepUpModal}
          onClose={() => setShowStepUpModal(false)}
      />
    </div>
  )
}
