import React from 'react'
import PropTypes from 'prop-types'
import I18n from 'i18n-js'
import stopEvent from '../utils/stop'

class Welcome extends React.Component {
  constructor() {
    super()
    this.state = { show: true }
  }

  close = (e) => {
    stopEvent(e)
    this.setState({ show: !this.state.show })
  }

  render() {
    const { currentUser } = this.context
    if (!currentUser.guest || !this.state.show) {
      return null
    }

    return (
      <div className="mod-welcome">
        <div className="container">
          <a
            href="https://wiki.surfnet.nl/display/surfconextdev/SURFconext+IdP+dashboard+-+help+page"
            target="_blank"
            rel="noreferrer noopener"
            className="welcome-text"
          >
            {I18n.t('header.welcome_txt')}
          </a>
          <a href="/close" className="close" onClick={this.close}>
            X
          </a>
        </div>
      </div>
    )
  }
}

Welcome.contextTypes = {
  currentUser: PropTypes.object,
}

export default Welcome
