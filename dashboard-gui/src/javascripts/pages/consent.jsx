import React, { useContext, useEffect, useState } from 'react'
import I18n from 'i18n-js'
import { CurrentUserContext } from '../App'
import { consentChangeRequest, disableConsent } from '../api'
import SelectWrapper from '../components/select_wrapper'
import ReactTooltip from 'react-tooltip'
import { consentTypes } from '../utils/utils'
import stopEvent from '../utils/stop'
import { setFlash } from '../utils/flash'
import StepUpModal from "../components/step_up_modal"
import ConnectModalContainer from "../components/connect_modal_container";

export default function Consent({ app }) {
  const [consent, setConsent] = useState(null)
  const [showStepUpModal, setShowStepUpModal] = useState(false)
  const [showJiraDownModal, setShowJiraDownModal] = useState(false)
  const [serverBusy, setServerBusy] = useState(false)
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
    if (currentUser.isMFARequired(2)) {
      setShowStepUpModal(true)
    } else {
      callback();
    }
  }

  function onSave(e) {
    stopEvent(e)

    if (currentUser.jiraDown) {
      setShowJiraDownModal(true)
      return
    }
    setServerBusy(true)
    consentChangeRequest(consent)
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

  useEffect(() => {
    fetchDisableConsent()
  }, [])

  if (!consent) {
    return null
  }
  const msgAllowed = consent.type.toLowerCase() !== 'no_consent'
  const loaRequired = currentUser.isMFARequired(2)
  return (
    <div>
      <div className={"title-container"}>
        <h2>{I18n.t('consent_panel.title')}</h2>
        {(isDashboardAdmin && loaRequired) &&
            <button className={`c-button save larger`}
                    disabled={serverBusy}
                    onClick={e => checkLoaLevel(() => onSave(e))}>
              {I18n.t('consent_panel.request')}
            </button>}
      </div>

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
          {(isDashboardAdmin && !loaRequired) && <SelectWrapper
            defaultValue={consent.type}
            options={consentTypes.map((t) => ({ value: t, display: I18n.t(`consent_panel.${t.toLowerCase()}`) }))}
            multiple={false}
            inputId="consent-value"
            isDisabled={!isDashboardAdmin || loaRequired}
            handleChange={(val) => setConsent({ ...consent, type: val })}
          />}
          {(!isDashboardAdmin || loaRequired) &&
            <input
                type="text"
                id="consent-type"
                value={I18n.t(`consent_panel.${consent.type.toLowerCase()}`)}
                disabled={true}
            />}

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
              disabled={!isDashboardAdmin || loaRequired}
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
              disabled={!isDashboardAdmin || loaRequired}
              onChange={(e) =>
                setConsent({
                  ...consent,
                  explanationEn: e.target.value,
                })
              }
            />
          )}

          {(isDashboardAdmin && !loaRequired) &&
            <button className={`c-button save ${serverBusy ? 'disabled' : ''}`}
                    disabled={serverBusy}
                    onClick={e => checkLoaLevel(() => onSave(e))}>
              {serverBusy && <div id="service-loader-id" className="loader"/>}
              {I18n.t('consent_panel.save')}
            </button>}
        </section>
      </div>
      <StepUpModal
          app={app}
          isOpen={showStepUpModal}
          onClose={() => setShowStepUpModal(false)}
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
