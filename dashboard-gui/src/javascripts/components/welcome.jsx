import React from 'react'
import PropTypes from 'prop-types'
import I18n from 'i18n-js'
import stopEvent from '../utils/stop'
import Cookies from 'js-cookie'

class Welcome extends React.Component {

  constructor() {
    super()
    this.state = { show: true }
  }

  componentDidMount() {
    const showWelcomeBanner = Cookies.get('show_welcome_banner');
    this.setState({show: !showWelcomeBanner});
  }

  close = (e) => {
    stopEvent(e)
    this.setState({ show: !this.state.show })
    Cookies.set('show_welcome_banner', 'true', { secure: window.location.protocol.startsWith("https"), expires: 365 })
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
            href="https://support.surfconext.nl/idp-help-en"
            target="_blank"
            rel="noreferrer noopener"
            className="welcome-text"
          >
            {I18n.t('header.welcome_txt')}
          </a>
          <button type="button" className="close" onClick={this.close}>
            X
          </button>
        </div>
      </div>
    )
  }
}

Welcome.contextTypes = {
  currentUser: PropTypes.object,
}

export default Welcome
