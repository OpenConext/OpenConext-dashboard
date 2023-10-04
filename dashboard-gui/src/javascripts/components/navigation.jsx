import React from 'react'
import { withRouter } from 'react-router'
import { Link } from 'react-router-dom'
import I18n from 'i18n-js'
import PropTypes from 'prop-types'
import { slide as Menu } from 'react-burger-menu'

import { isEmpty } from '../utils/utils'
import { searchJira } from '../api'
import { emitter } from '../utils/flash'

class Navigation extends React.Component {
  constructor(props) {
    super(props)

    this.state = {
      awaitingInputTickets: 0,
    }
    this.callback = () => this.getAwaitingInputJiraTickets()
  }

  componentDidMount() {
    const { currentUser } = this.context
    if (!currentUser.guest && !currentUser.dashboardMember) {
      this.getAwaitingInputJiraTickets()
    }
    emitter.addListener('invite_request_updates', this.callback)
  }

  getAwaitingInputJiraTickets = () => {
    const jiraFilter = {
      maxResults: 0,
      startAt: 0,
      statuses: ['Waiting for customer'],
      types: ['LINKINVITE', 'UNLINKINVITE'],
    }
    searchJira(jiraFilter).then((data) => {
      const { total } = data.payload
      this.setState({ awaitingInputTickets: total })
    })
  }

  componentWillUnmount() {
    emitter.removeListener('invite_request_updates', this.callback)
  }

  renderItem(href, value, active, marker = 0) {
    return (
      <li>
        <Link
          to={href}
          className={active ? 'active' : ''}
          onClick={() => {
            if (href === '/tickets') {
              this.getAwaitingInputJiraTickets()
            }
            this.props.onMobileMenuChange(false)
          }}
        >
          {I18n.t('navigation.' + value)}
        </Link>
        {marker > 0 && <span className="marker">{marker}</span>}
      </li>
    )
  }

  renderNavigationItems() {
    const { currentUser } = this.context
    const { awaitingInputTickets } = this.state
    const showInviteRequest = !isEmpty(currentUser) && currentUser.superUser
    const activeTab = this.props.location.pathname
    const hideTabs = currentUser.getHideTabs()

    return (
      <ul>
        {hideTabs.indexOf('apps') === -1 && this.renderItem('/apps/connected', 'apps', activeTab.startsWith('/apps'))}
        {hideTabs.indexOf('my_idp') === -1 &&
          !currentUser.guest &&
          this.renderItem('/my-idp', 'my_idp', activeTab.startsWith('/my-idp'))}
        {currentUser.showStats() && this.renderItem('/statistics', 'stats', activeTab === '/statistics')}
        {hideTabs.indexOf('user_invite') === -1 &&
          !currentUser.guest &&
          !currentUser.dashboardMember &&
          showInviteRequest &&
          this.renderItem('/users/invite', 'invite_request', activeTab === '/users/invite')}
        {hideTabs.indexOf('tickets') === -1 &&
          !currentUser.guest &&
          !currentUser.jiraDown &&
          !currentUser.dashboardMember &&
          this.renderItem('/tickets', 'history', activeTab.startsWith('/tickets'), awaitingInputTickets)}
      </ul>
    )
  }

  render() {
    return (
      <nav>
        <div className="desktop-menu">{this.renderNavigationItems()}</div>
        <Menu
          isOpen={this.props.mobileMenuOpen}
          right
          customBurgerIcon={false}
          onStateChange={(state) => this.props.onMobileMenuChange(state.isOpen)}
        >
          {this.renderNavigationItems()}
        </Menu>
      </nav>
    )
  }
}

Navigation.contextTypes = {
  currentUser: PropTypes.object,
}

export default withRouter(Navigation)
