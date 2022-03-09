import React from 'react'
import I18n from 'i18n-js'
import ConnectModalContainer from './connect_modal_container'
import {login} from "../utils/utils";

export default function StepUpModal({app, isOpen, onClose, requiredLoaLevel = 2, location=window.location.href}) {
    return (
        <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
            <div className="connect-modal-header">
                {I18n.t('stepup.title', {app: app.name})}
            </div>
            <div className="connect-modal-body">
                <p>{I18n.t('stepup.info')}</p>
            </div>
            <div className="buttons">
                <button className="c-button white" onClick={onClose}>
                    {I18n.t('stepup.cancel')}
                </button>
                <button className="c-button" onClick={e => login(e, requiredLoaLevel, location)}>
                    {I18n.t('stepup.go')}
                </button>
            </div>
        </ConnectModalContainer>
    )
}
