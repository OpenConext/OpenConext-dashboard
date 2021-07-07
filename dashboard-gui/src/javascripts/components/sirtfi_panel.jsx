import React from 'react'
import I18n from 'i18n-js'

import { AppShape } from '../shapes'

class SirtfiPanel extends React.Component {
  render() {
    return (
      <div className="l-middle-app-detail">
        <div className="mod-title">
          <h1>{I18n.t('sirtfi_panel.title', { name: this.props.app.name })}</h1>
          <em className="info" dangerouslySetInnerHTML={{ __html: I18n.t('sirtfi_panel.subtitle') }}></em>
          <p className="spacer">{I18n.t('sirtfi_panel.contactPersons')}</p>
        </div>
        {this.renderSirtfiContactPersons(this.props.app)}
      </div>
    )
  }

  renderSirtfiContactPersons(app) {
    return (
      <div className="mod-sirtfi">
        <table>
          <thead>
            <tr>
              <th>{I18n.t('sirtfi_panel.cp_name')}</th>
              <th>{I18n.t('sirtfi_panel.cp_email')}</th>
              <th>{I18n.t('sirtfi_panel.cp_telephoneNumber')}</th>
            </tr>
          </thead>
          <tbody>
            {app.contactPersons.filter((cp) => cp.sirtfiSecurityContact).map(this.renderContactPerson.bind(this))}
          </tbody>
        </table>
      </div>
    )
  }

  renderContactPerson(contactPerson, index) {
    return (
      <tr key={index}>
        <td>{contactPerson.name}</td>
        <td>{contactPerson.emailAddress}</td>
        <td>{contactPerson.telephoneNumber}</td>
      </tr>
    )
  }
}

SirtfiPanel.propTypes = {
  app: AppShape.isRequired,
}

export default SirtfiPanel
