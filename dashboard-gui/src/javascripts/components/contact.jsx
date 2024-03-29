import React from 'react'
import PropTypes from 'prop-types'
import I18n from 'i18n-js'

class Contact extends React.Component {
  render() {
    if (this.props.email) {
      return (
        <div className="contact">
          <h2>{I18n.t('contact.email')}</h2>
          <address>
            <a href={'mailto:' + this.props.email}>{this.props.email}</a>
          </address>
        </div>
      )
    }

    return null
  }
}

Contact.propTypes = {
  email: PropTypes.string,
}

export default Contact
