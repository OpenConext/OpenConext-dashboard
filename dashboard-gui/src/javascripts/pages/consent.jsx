import React, { useContext, useEffect, useState } from 'react'
import I18n from 'i18n-js'
import { CurrentUserContext } from '../App'
import { consentChangeRequest, disableConsent } from '../api'
import SelectWrapper from '../components/select_wrapper'
import ReactTooltip from 'react-tooltip'
import { consentTypes } from '../utils/utils'
import stopEvent from '../utils/stop'
import { setFlash } from '../utils/flash'

export default function Consent({ app }) {
  const [consent, setConsent] = useState(null)
  const { currentUser } = useContext(CurrentUserContext)
  const isDashboardAdmin = currentUser.dashboardAdmin
  const subTitle2 = isDashboardAdmin ? 'subtitle2' : 'subtitle2Viewer'

  async function fetchDisableConsent() {
    const response = await disableConsent()
    const consent = response.find((dc) => dc.spEntityId === app.spEntityId) || {
      spEntityId: app.spEntityId,
      type: 'DEFAULT_CONSENT',
      explanationNl: '',
      explanationEn: '',
      explanationPt: '',
      typeMetaData: app.entityType,
    }

    setConsent(consent)
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

  const msgAllowed = consent.type !== 'no_consent'

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
          <label>
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
            isDisabled={!isDashboardAdmin}
            handleChange={(val) => setConsent({ ...consent, type: val })}
          />

          {msgAllowed && (
            <label>
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
              value={consent.explanationNl}
              disabled={!isDashboardAdmin}
              onChange={(e) => setConsent({ ...consent, explanationNl: e.target.value })}
            />
          )}

          {msgAllowed && (
            <label>
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

          {msgAllowed && (
            <label>
              {I18n.t('consent_panel.explanationPt')}
              <i className="fa fa-info-circle" data-for="explanationPt_tooltip" data-tip></i>
              <ReactTooltip id="explanationPt_tooltip" type="info" class="tool-tip" effect="solid" multiline={true}>
                <span dangerouslySetInnerHTML={{ __html: I18n.t('consent_panel.explanationPt_tooltip') }} />
              </ReactTooltip>
            </label>
          )}
          {msgAllowed && (
            <input
              type="text"
              value={consent.explanationPt}
              disabled={!isDashboardAdmin}
              onChange={(e) =>
                setConsent({
                  ...consent,
                  explanationPt: e.target.value,
                })
              }
            />
          )}

          {isDashboardAdmin && (
            <button className="c-button save" onClick={(e) => onSave(e)}>
              {I18n.t('consent_panel.save')}
            </button>
          )}
        </section>
      </div>
    </div>
  )
}
