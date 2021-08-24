import React from 'react'
import I18n from 'i18n-js'
import { ReactComponent as EmailIcon } from '../../images/email-action-unread.svg'
import { ReactComponent as TelephoneIcon } from '../../images/phone-actions-wait-1.svg'

export default function ContactPerson({ contactPerson }) {
  return (
    <div className="contact-person-container">
      <div className="contact-person-name">
        <h3>{contactPerson.name}</h3>
      </div>
      <div className="contact-person-contact-info">
        <div className="contact-person-email">
          <EmailIcon />
          {contactPerson.emailAddress ? (
            <a href={`mailto:${contactPerson.emailAddress}`}>{contactPerson.emailAddress}</a>
          ) : (
            '-'
          )}
        </div>
        <div className="contact-person-telephone">
          <TelephoneIcon />
          {contactPerson.telephoneNumber || '-'}
        </div>
      </div>
      <div className="contact-person-type">
        <div className="type-badge">
          {I18n.t('my_idp.contact_types.' + contactPerson.contactPersonType + '.display')}
        </div>
      </div>
    </div>
  )
}
