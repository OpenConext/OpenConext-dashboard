import React from 'react'

import PropTypes from 'prop-types'
import I18n from 'i18n-js'
import Logout from '../pages/logout'
import { render } from 'react-dom'
import { exit, logout } from '../api'
import { Link } from 'react-router-dom'
import IDPSelector from '../components/idp_selector'
import Navigation from '../components/navigation'
import isUndefined from 'lodash.isundefined'
import stopEvent from '../utils/stop'
import surfLogo from '../../images/SURF.svg'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons'

const UserIndicator = ({ user }) => {
  const givenNameCharacter = user.givenName ? user.givenName[0] : ''
  const surNameCharacter = user.surName ? user.surName[0] : ''

  return (
    <div className="user-indicator">
      <span>
        {givenNameCharacter}
        {surNameCharacter}
      </span>
    </div>
  )
}

UserIndicator.propTypes = {
  user: PropTypes.object,
}

class Header extends React.Component {
  constructor() {
    super()

    this.state = { dropDownActive: false }
  }

  login = (e) => {
    stopEvent(e)
    window.location.href = `/login?redirect_url=${encodeURIComponent(window.location.href)}`
  }

  render() {
    const { currentUser } = this.context

    return (
      <div className="mod-header">
        <div className="container">
          <div className="header-content">
            <Link to="/" className="logo-container">
              <img src={surfLogo} alt="SURF" />
              <span className="idp-dashboard">IdP Dashboard</span>
            </Link>

            <div className="navigation-items">
              <Navigation />
            </div>

            <div className="meta">
              {currentUser.guest && (
                <a className="login" href="/login" onClick={this.login}>
                  Login
                </a>
              )}
              {!currentUser.guest && this.renderDropDownToggle()}
            </div>
          </div>
        </div>
      </div>
    )
  }

  handleDropdownKeyDown(e) {
    if (e.key !== 'Tab') {
      e.preventDefault()
      this.handleToggle()
    }
  }

  renderDropDownToggle() {
    const { currentUser } = this.context

    return (
      <div className="dropdown-container">
        <div
          className="dropdown-toggle"
          role="button"
          tabIndex={0}
          onKeyDown={this.handleDropdownKeyDown.bind(this)}
          onClick={this.handleToggle.bind(this)}
        >
          <UserIndicator user={currentUser} />
          {this.renderDropDownIndicator()}
        </div>
        {this.renderDropDown()}
      </div>
    )
  }

  renderProfileLink() {
    const { currentUser } = this.context
    if (isUndefined(currentUser)) {
      return null
    }
    return currentUser.superUser ? (
      <span>
        <span>
          {I18n.t('header.welcome')}&nbsp;{currentUser.displayName}
        </span>
        <Link className="super-user" to={'/users/search'}>
          {I18n.t('header.super_user_switch')}
        </Link>
      </span>
    ) : (
      <span>
        {I18n.t('header.welcome')}&nbsp;
        <a href="/welcome" onClick={this.handleToggle.bind(this)}>
          {currentUser.displayName}
          {this.renderDropDownIndicator()}
        </a>
      </span>
    )
  }

  renderDropDownIndicator() {
    if (this.state.dropDownActive) {
      return <FontAwesomeIcon icon={faChevronUp} />
    }

    return <FontAwesomeIcon icon={faChevronDown} />
  }

  renderDropDown() {
    const { currentUser } = this.context

    if (!currentUser || !this.state.dropDownActive) {
      return null
    }

    if (currentUser.superUser) {
      return (
        <ul>
          <li>
            <Link className="super-user" to={'/users/search'}>
              {I18n.t('header.super_user_switch')}
            </Link>
          </li>
        </ul>
      )
    }

    if (!currentUser.superUser) {
      return (
        <ul>
          <h2>{currentUser.displayName}</h2>
          <ul>
            <li>
              <Link to="/profile" onClick={this.handleClose.bind(this)}>
                {I18n.t('header.profile')}
              </Link>{' '}
            </li>
            {this.renderExitLogout()}
          </ul>
          <IDPSelector />
        </ul>
      )
    }

    return null
  }

  renderExitLogout() {
    const { currentUser } = this.context
    if (isUndefined(currentUser)) {
      return null
    } else if (currentUser.superUser && currentUser.switchedToIdp) {
      return (
        <li>
          <a href="/exit" onClick={this.handleExitClick.bind(this)}>
            {I18n.t('header.links.exit')}
          </a>
        </li>
      )
    }

    return (
      <li>
        <a href="/logout" onClick={this.handleLogoutClick.bind(this)}>
          {I18n.t('header.links.logout')}
        </a>
      </li>
    )
  }

  handleLogoutClick(e) {
    stopEvent(e)
    logout().then(() => render(<Logout />, document.getElementById('app')))
  }

  handleExitClick(e) {
    stopEvent(e)
    exit().then(() => (window.location = '/'))
  }

  handleClose() {
    this.setState({ dropDownActive: false })
  }

  handleToggle(e) {
    stopEvent(e)
    this.setState({ dropDownActive: !this.state.dropDownActive })
  }
}

Header.contextTypes = {
  currentUser: PropTypes.object,
}

export default Header
