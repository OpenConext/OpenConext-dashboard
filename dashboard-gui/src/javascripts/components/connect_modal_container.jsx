import React from 'react'
import Modal from 'react-modal'

const customStyles = {
  content: {
    top: '50%',
    left: '50%',
    right: 'auto',
    bottom: 'auto',
    marginRight: '-50%',
    transform: 'translate(-50%, -50%)',
    maxWidth: '777px',
    padding: 0,
    background: 'rgb(255, 255, 255)',
    border: '1px solid rgb(204, 204, 204)',
    inset: '50% auto auto 50%',
    overflow: 'auto',
    outline: 'none',
    position: 'absolute',
    borderRadius: '8px',
    maxHeight: '95%',
  },
}

Modal.setAppElement('#app')

export default function ConnectModalContainer({ isOpen, onClose, children }) {
  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onClose}
      onAfterOpen={() => {
        document.body.style.overflow = 'hidden'
      }}
      onAfterClose={() => {
        document.body.removeAttribute('style')
      }}
      style={customStyles}
      overlayClassName="confirmation-dialog-overlay"
      className="connect-modal"
    >
      {children}
    </Modal>
  )
}
