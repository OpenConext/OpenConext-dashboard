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
import Flash from '../components/flash'
import surfLogo from '../../images/SURF.svg'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faBars, faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons'

const UserIndicator = ({ user }) => {
  const givenNameCharacter = user.givenName ? user.givenName[0] : ''
  const surNameCharacter = user.surName ? user.surName[0] : ''

  return (
    <span className="user-indicator">
      <span>
        {givenNameCharacter}
        {surNameCharacter}
      </span>
    </span>
  )
}

UserIndicator.propTypes = {
  user: PropTypes.object,
}

class Header extends React.Component {
  constructor() {
    super()

    this.state = { dropDownActive: false, openMobileMenu: false }
  }

  render() {
    const { currentUser } = this.context

    return (
      <header>
        <div className="container">
          <div className="header-content">
            <Link to="/" className="logo-container">
              <img src={surfLogo} alt="SURF" />
              <span className="conext">CONEXT</span>
              <span className="idp-dashboard">IdP Dashboard</span>
            </Link>

            <Navigation
              mobileMenuOpen={this.state.openMobileMenu}
              onMobileMenuChange={(state) => this.setState({ openMobileMenu: state })}
            />

            <div className="meta">
              {currentUser.guest && (
                <a className="login" href={`/login?redirect_url=${encodeURIComponent(window.location.href)}`}>
                  Login
                </a>
              )}
              {!currentUser.guest && this.renderDropDownToggle()}
              <FontAwesomeIcon icon={faBars} onClick={() => this.setState({ openMobileMenu: true })} />
            </div>
          </div>
        </div>
        <Flash />
      </header>
    )
  }

  renderDropDownToggle() {
    const { currentUser } = this.context

    return (
      <div className="dropdown-container">
        <button className="dropdown-toggle" type="button" onClick={this.handleToggle.bind(this)}>
          <UserIndicator user={currentUser} />
          {this.renderDropDownIndicator()}
        </button>
        {this.renderDropDown()}
      </div>
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
        <>
          <div className="overlay" onClick={this.handleClose.bind(this)} />
          <ul>
            <li>
              <Link className="super-user" to="/users/search" onClick={this.handleClose.bind(this)}>
                {I18n.t('header.super_user_switch')}
              </Link>
            </li>
            {this.renderExitLogout()}
          </ul>
        </>
      )
    }

    if (!currentUser.superUser) {
      return (
        <>
          <div className="overlay" onClick={this.handleClose.bind(this)} />
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
        </>
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
          <button type="button" onClick={this.handleExitClick.bind(this)}>
            {I18n.t('header.links.exit')}
          </button>
        </li>
      )
    }

    return (
      <li>
        <button type="button" onClick={this.handleLogoutClick.bind(this)}>
          {I18n.t('header.links.logout')}
        </button>
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
