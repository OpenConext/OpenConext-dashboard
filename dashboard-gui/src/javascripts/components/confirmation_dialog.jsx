import React from 'react'
import PropTypes from 'prop-types'
import Modal from 'react-modal'
import I18n from 'i18n-js'
import stopEvent from '../utils/stop'

export default function ConfirmationDialog({
  isOpen = false,
  cancel,
  confirm,
  question = '',
  leavePage = false,
  isError = false,
}) {
  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={cancel}
      contentLabel={I18n.t('confirmation_dialog.title')}
      className="confirmation-dialog-content"
      overlayClassName="confirmation-dialog-overlay"
      closeTimeoutMS={250}
      ariaHideApp={false}
    >
      <section className="dialog-header">{I18n.t('confirmation_dialog.title')}</section>
      {leavePage ? (
        <section className={`dialog-content ${isError ? ' error' : ''}`}>
          <h2>{I18n.t('confirmation_dialog.leavePage')}</h2>
          <p>{I18n.t('confirmation_dialog.leavePageSub')}</p>
        </section>
      ) : (
        <section className="dialog-content">
          <h2>{question}</h2>
        </section>
      )}
      <section className="dialog-buttons">
        <button className="n-button" onClick={cancel}>
          {leavePage ? I18n.t('confirmation_dialog.leave') : I18n.t('confirmation_dialog.cancel')}
        </button>
        <button
          className="c-button"
          onClick={(e) => {
            stopEvent(e)
            confirm()
          }}
        >
          {leavePage ? I18n.t('confirmation_dialog.stay') : I18n.t('confirmation_dialog.confirm')}
        </button>
      </section>
    </Modal>
  )
}

ConfirmationDialog.propTypes = {
  isOpen: PropTypes.bool,
  cancel: PropTypes.func.isRequired,
  confirm: PropTypes.func.isRequired,
  question: PropTypes.string,
  leavePage: PropTypes.bool,
  isError: PropTypes.bool,
}
