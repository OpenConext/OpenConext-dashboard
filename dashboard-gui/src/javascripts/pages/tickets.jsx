import React, { useContext, useEffect, useState } from 'react'
import I18n from 'i18n-js'
import { Link, useParams } from 'react-router-dom'
import moment from 'moment'
import Breadcrumbs from '../components/breadcrumbs'
import { ReactComponent as TicketsIcon } from '../../images/task-list-check-1.svg'
import { ReactComponent as TagsIcon } from '../../images/tags.svg'
import { ReactComponent as UserIcon } from '../../images/social-profile-avatar.svg'
import { ReactComponent as RobotIcon } from '../../images/robot-2.svg'
import { searchJira } from '../api'
import { CurrentUserContext } from '../App'
import { isEmpty } from '../utils/utils'

const statusMap = {
  todo: 'To Do',
  in_progress: 'In Progress',
  awaiting_input: 'Awaiting Input',
  resolved: 'Resolved',
  closed: 'Closed',
}

const allTypes = ['LINKREQUEST', 'UNLINKREQUEST', 'CHANGE', 'LINKINVITE']

export default function Tickets() {
  const [actions, setActions] = useState([])
  const { status } = useParams()
  const currentStatus = statusMap[status] ? [statusMap[status]] : Object.values(statusMap)
  const { currentUser } = useContext(CurrentUserContext)

  async function fetchActions() {
    const from = moment().subtract(1, 'year')
    const to = moment().add(1, 'day')

    const filter = {
      from: from.unix(),
      to: to.unix(),
      maxResults: 1000,
      sortAscending: true,
      sortBy: 'requestDate',
      startAt: 0,
      statuses: currentStatus,
      types: allTypes,
    }
    const data = await searchJira(filter)
    const { issues } = data.payload
    setActions(issues)
  }

  useEffect(() => {
    fetchActions()
  }, [status])

  const breadcrumbs = [
    { link: '/apps/connected', text: 'Home' },
    { link: `/tickets`, text: I18n.t('history.header') },
  ]

  return (
    <div className="tickets">
      <Breadcrumbs items={breadcrumbs} />
      <div className="tickets-header">
        <div className="container">
          <TicketsIcon />
          <h1>{I18n.t('history.header')}</h1>
          <span>{I18n.t('history.info')}</span>
        </div>
      </div>
      <div className="tickets-content">
        <div className="container">
          <div className="filters">
            <h3>{I18n.t('history.filter')}</h3>
            {Object.keys(statusMap).map((s) => (
              <Link key={s} to={`/tickets/${s}`} className={status === s && 'active'}>
                {I18n.t(`history.statuses.${statusMap[s]}`)}
              </Link>
            ))}
            <Link to={`/tickets`} className={!status && 'active'}>
              {I18n.t('history.statuses.all')}
            </Link>
          </div>
          <div className="list">
            <h2>{I18n.t(`history.statuses.${statusMap[status] || 'all'}`)}</h2>
            {actions.map((action) => (
              <Action key={action.jiraKey} action={action} currentUser={currentUser} />
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

function Action({ action, currentUser }) {
  const linkInviteAwaitingInput = action.type === 'LINKINVITE' && action.status === 'Awaiting Input' && action.spId
  const type = action.typeMetaData || 'saml20_sp'
  const renderViewInvitation = linkInviteAwaitingInput && currentUser.dashboardAdmin
  const renderResend = linkInviteAwaitingInput && currentUser.superUser && !isEmpty(action.emailTo)

  return (
    <div className="ticket">
      <div className="ticket-content">
        <h4>{action.spName === 'Information unavailable' ? action.spId : action.spName}</h4>
        {action.personalMessage && (
          <p dangerouslySetInnerHTML={{ __html: action.personalMessage.replace(/\n/g, '<br>') }} />
        )}
        <div className="actions">
          {action.spEid && (
            <Link to={`/apps/${action.spEid}/${type}/about`} className="c-button">
              {I18n.t('history.serviceDetails')}
            </Link>
          )}
          {renderViewInvitation && (
            <Link to={`/apps/${action.spEid}/${type}/about`} className="c-button">
              {I18n.t('history.viewInvitation')}
            </Link>
          )}
          {renderResend && (
            <Link to={`/users/resend_invite/${action.jiraKey}`} className="c-button">
              {I18n.t('history.resendInvitation')}
            </Link>
          )}
        </div>
      </div>
      <div className="ticket-details">
        <div className="ticket-detail-row type">
          <TicketsIcon /> {I18n.t('history.action_types_name.' + action.type)}
        </div>
        <div className="ticket-detail-row timestamps">
          <TagsIcon />{' '}
          <div>
            <div className="created">{moment(action.requestDate).format('DD-MM-YYYY')}</div>
            {action.updateDate && (
              <div className="updated">
                {I18n.t('history.last_updated')} {moment(action.updateDate).format('DD-MM-YYYY')}
              </div>
            )}
          </div>
        </div>
        <div className="ticket-detail-row">
          <UserIcon /> {action.userName}
        </div>
        <div className="ticket-detail-row">
          <RobotIcon /> {action.jiraKey}
        </div>
      </div>
    </div>
  )
}
