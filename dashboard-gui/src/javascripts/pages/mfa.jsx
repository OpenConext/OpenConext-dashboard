import React, {useContext, useState} from 'react'
import I18n from 'i18n-js'
import {CurrentUserContext} from '../App'
import {isEmpty} from '../utils/utils'
import SelectWrapper from '../components/select_wrapper'
import {mfaChangeRequest} from '../api'
import stopEvent from '../utils/stop'
import {setFlash} from '../utils/flash'
import StepUpModal from "../components/step_up_modal"

export default function MFA({app}) {
    const {currentUser} = useContext(CurrentUserContext)
    const mfaEntity = (currentUser.currentIdp.mfaEntities || []).find((e) => e.name === app.spEntityId)
    const initialAuthnContextLevel = mfaEntity && mfaEntity.level || ''

    const [authnContextLevel, setAuthnContextLevel] = useState(initialAuthnContextLevel)
    const [showStepUpModal, setShowStepUpModal] = useState(false)
    const isDashboardAdmin = currentUser.dashboardAdmin
    const authnContextLevelEquals = (!mfaEntity && authnContextLevel === initialAuthnContextLevel) || (mfaEntity && mfaEntity.level === authnContextLevel)

    let options = []
    if (isEmpty(mfaEntity)) {
        options.push({value: '', display: I18n.t('mfa_panel.defaultAuthnContextLevel')})
    }
    options = options.concat(
        currentUser.authnContextLevels.map(level => ({
            value: level,
            display: I18n.t(`mfa_panel.${level.substring(level.lastIndexOf("/") + 1)}`),
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

        mfaChangeRequest({entityId: app.spEntityId, authnContextLevel: authnContextLevel, entityType: app.entityType})
            .then((res) => {
                res.json().then((action) => {
                    if (action.payload['no-changes']) {
                        setFlash(I18n.t('my_idp.no_change_request_created'), 'warning')
                    } else {
                        setFlash(I18n.t('my_idp.change_request_created', {jiraKey: action.payload.jiraKey}))
                    }
                    window.scrollTo(0, 0)
                })
            })
            .catch(() => {
                setFlash(I18n.t('my_idp.change_request_failed'), 'error')
                window.scrollTo(0, 0)
            })
    }

    return (
        <div className="mod-ssid-panel">
            <h2 dangerouslySetInnerHTML={{__html: I18n.t('mfa_panel.title')}}/>
            <p dangerouslySetInnerHTML={{__html: I18n.t('mfa_panel.subtitle')}}/>
            <p className="info" dangerouslySetInnerHTML={{__html: I18n.t('mfa_panel.subtitle2')}}/>
            {isDashboardAdmin &&
            <p className="info" dangerouslySetInnerHTML={{__html: I18n.t('mfa_panel.subtitle3')}}/>}
            <div>
                <section className="change-form">
                    <label htmlFor="authn_context_level">{I18n.t('mfa_panel.authn_context_level')}</label>
                    <SelectWrapper
                        defaultValue={authnContextLevel}
                        options={options}
                        multiple={false}
                        inputId="authn_context_level"
                        isDisabled={!isDashboardAdmin}
                        handleChange={val => setAuthnContextLevel(val)}
                    />
                    {isDashboardAdmin && (
                        <button
                            className={`c-button save ${authnContextLevelEquals ? 'disabled' : ''}`}
                            disabled={authnContextLevelEquals}
                            onClick={e => checkLoaLevel(() => saveRequest(e))}>
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
        </div>
    )
}
