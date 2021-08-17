import React from 'react'
import { withRouter } from 'react-router'
import I18n from 'i18n-js'
import PropTypes from 'prop-types'

import { isEmpty } from '../utils/utils'
import { searchJira } from '../api'
import stopEvent from '../utils/stop'
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
      statuses: ['Awaiting Input'],
      types: ['LINKINVITE'],
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
        <a
          href={href}
          className={active ? 'active' : ''}
          onClick={(e) => {
            stopEvent(e)
            if (href === '/tickets') {
              this.getAwaitingInputJiraTickets()
            }
            this.props.history.push(href)
          }}
        >
          {I18n.t('navigation.' + value)}
        </a>
        {marker > 0 && <span className="marker">{marker}</span>}
      </li>
    )
  }

  render() {
    const { currentUser } = this.context
    const { awaitingInputTickets } = this.state
    const showInviteRequest = !isEmpty(currentUser) && currentUser.superUser
    const activeTab = this.props.location.pathname
    const hideTabs = currentUser.getHideTabs()
    return (
      <div className="mod-navigation">
        <ul>
          {hideTabs.indexOf('apps') === -1 && this.renderItem('/apps/connected', 'apps', activeTab.startsWith('/apps'))}
          {hideTabs.indexOf('my_idp') === -1 &&
            !currentUser.guest &&
            this.renderItem('/my-idp', 'my_idp', activeTab === '/my-idp')}
          {currentUser.showStats() && this.renderItem('/statistics', 'stats', activeTab === '/statistics')}
          {hideTabs.indexOf('user_invite') === -1 &&
            !currentUser.guest &&
            !currentUser.dashboardMember &&
            showInviteRequest &&
            this.renderItem('/users/invite', 'invite_request', activeTab === '/users/invite')}
          {hideTabs.indexOf('tickets') === -1 &&
            !currentUser.guest &&
            !currentUser.dashboardMember &&
            this.renderItem('/tickets', 'history', activeTab === '/tickets', awaitingInputTickets)}
        </ul>
      </div>
    )
  }
}

Navigation.contextTypes = {
  currentUser: PropTypes.object,
}

export default withRouter(Navigation)
