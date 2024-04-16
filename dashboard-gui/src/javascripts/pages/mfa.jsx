import React, {useContext, useState} from 'react'
import I18n from 'i18n-js'
import {CurrentUserContext} from '../App'
import {isEmpty} from '../utils/utils'
import SelectWrapper from '../components/select_wrapper'
import {mfaChangeRequest} from '../api'
import stopEvent from '../utils/stop'
import {setFlash} from '../utils/flash'
import StepUpModal from "../components/step_up_modal"
import ConnectModalContainer from "../components/connect_modal_container";

export default function MFA({app}) {
    const {currentUser} = useContext(CurrentUserContext)
    const mfaEntity = (currentUser.getCurrentIdp().mfaEntities || []).find((e) => e.name === app.spEntityId)
    const initialAuthnContextLevel = mfaEntity && mfaEntity.level || ''
    const [authnContextLevel, setAuthnContextLevel] = useState(initialAuthnContextLevel)
    const [showStepUpModal, setShowStepUpModal] = useState(false)
    const [showJiraDownModal, setShowJiraDownModal] = useState(false)
    const [serverBusy, setServerBusy] = useState(false)
    const isDashboardAdmin = currentUser.dashboardAdmin
    const authnContextLevelEquals = (!mfaEntity && authnContextLevel === initialAuthnContextLevel) || (mfaEntity && mfaEntity.level === authnContextLevel)

    let options = []
    if (isEmpty(mfaEntity)) {
        options.push({value: '', display: I18n.t('mfa_panel.defaultAuthnContextLevel')})
    }

    const getAuthContextValue = authContextLevel => {
        const splitted = authContextLevel.split(/[\/|:]/)
        const value = splitted[splitted.length - 1].toLowerCase();
        return I18n.t(`mfa_panel.${value}`)
    }

    options = options.concat(
        currentUser.authnContextLevels.map(level => ({
            value: level,
            display: getAuthContextValue(level)
        }))
    )

    const notAllowedAuthnContextLevel = !isEmpty(initialAuthnContextLevel)
        && currentUser.authnContextLevels.indexOf(initialAuthnContextLevel) === -1

    const checkLoaLevel = callback => {
        if (currentUser.isMFARequired(2)) {
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
        mfaChangeRequest({entityId: app.spEntityId, authnContextLevel: authnContextLevel, entityType: app.entityType})
            .then((res) => {
                setServerBusy(false)
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
                setServerBusy(false)
                window.scrollTo(0, 0)
            })
    }
    const loaRequired = currentUser.isMFARequired(2)
    return (
        <div className="mod-ssid-panel">
            <div className={"title-container"}>
                <h2>{I18n.t('mfa_panel.title')}</h2>
                {(isDashboardAdmin && loaRequired) &&
                    <button className={`c-button larger`}
                            disabled={serverBusy}
                            onClick={e => checkLoaLevel(() => saveRequest(e))}>
                        {I18n.t('consent_panel.request')}
                    </button>}
            </div>
            <p dangerouslySetInnerHTML={{__html: I18n.t('mfa_panel.subtitle')}}/>
            <p className="info" dangerouslySetInnerHTML={{__html: I18n.t('mfa_panel.subtitle2')}}/>
            {isDashboardAdmin &&
            <p className="info" dangerouslySetInnerHTML={{__html: I18n.t('mfa_panel.subtitle3')}}/>}
            <div>
                <section className="change-form">
                    <label htmlFor="authn_context_level">{I18n.t('mfa_panel.authn_context_level')}</label>
                    {(!notAllowedAuthnContextLevel && !loaRequired && isDashboardAdmin) &&
                    <SelectWrapper
                        defaultValue={authnContextLevel}
                        options={options}
                        multiple={false}
                        inputId="authn_context_level"
                        isDisabled={!isDashboardAdmin || loaRequired}
                        handleChange={val => setAuthnContextLevel(val)}
                    />}
                    {(!notAllowedAuthnContextLevel && loaRequired) &&
                        <input
                            type="text"
                            id="consent-type"
                            value={authnContextLevel || I18n.t('mfa_panel.defaultAuthnContextLevel')}
                            disabled={true}
                        />
                    }
                    {notAllowedAuthnContextLevel && <div className="not-allowed-mfa-change">
                        <input
                            type="text"
                            id="consent-type"
                            value={getAuthContextValue(initialAuthnContextLevel)}
                            disabled={true}
                        />
                        <p dangerouslySetInnerHTML={{__html: I18n.t('mfa_panel.not_allowed')}}/>
                    </div>}
                    {(isDashboardAdmin && !notAllowedAuthnContextLevel && !loaRequired) &&
                    <button
                        className={`c-button save ${serverBusy ? 'disabled' : ''}`}
                        disabled={serverBusy}
                        onClick={e => checkLoaLevel(() => saveRequest(e))}>
                        {serverBusy && <div id="service-loader-id" className="loader"/>}
                        {I18n.t('consent_panel.save')}
                    </button>
                    }
                </section>
            </div>
            <StepUpModal
                app={app}
                isOpen={showStepUpModal}
                onClose={() => setShowStepUpModal(false)}
                requiredLoaLevel={2}
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
