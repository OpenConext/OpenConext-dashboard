import React from 'react'
import PropTypes from 'prop-types'
import Helmet from 'react-helmet'

import I18n from 'i18n-js'
import { isEmpty } from '../utils/utils'

class Profile extends React.Component {
  render() {
    const { currentUser } = this.context
    const attributeKeys = Object.keys(currentUser.attributeMap)
    const roles = currentUser.grantedAuthorities
    return (
      <div className="l-mini">
        <Helmet title={I18n.t('profile.title')} />
        <div className="mod-profile">
          <h1>{I18n.t('profile.title')}</h1>
          <p>{I18n.t('profile.sub_title')}</p>
          <h3>{I18n.t('profile.my_attributes')}</h3>
          <table>
            <thead>
              <tr>
                <th scope="col" className="percent_50">
                  {I18n.t('profile.attribute')}
                </th>
                <th scope="col" className="percent_50">
                  {I18n.t('profile.value')}
                </th>
              </tr>
            </thead>
            <tbody>{attributeKeys.map(this.renderAttribute.bind(this))}</tbody>
          </table>
          <h3>{I18n.t('profile.my_roles')}</h3>
          <p>{I18n.t('profile.my_roles_description')}</p>
          <table>
            <thead>
              <tr>
                <th className="percent_50">{I18n.t('profile.role')}</th>
                <th className="percent_50">{I18n.t('profile.role_description')}</th>
              </tr>
            </thead>
            <tbody>{roles.map(this.renderRole)}</tbody>
          </table>
        </div>
      </div>
    )
  }

  renderAttribute(attributeKey) {
    const { currentUser } = this.context
    // Use [] to get the value from I18n because attributeKey can contain (.) dot's.
    const attributeTranslation = I18n.t('profile.attribute_map')[attributeKey] || {}
    const attributeName = attributeTranslation['name']
    const attributeDescription = attributeTranslation['description']
    return (
      <tr key={attributeKey}>
        <td title={attributeDescription}>{attributeName}</td>
        <td>
          <ul>
            {currentUser.attributeMap[attributeKey].map((value, i) => {
              return <li key={i}>{value}</li>
            })}
          </ul>
        </td>
      </tr>
    )
  }

  renderRole(role) {
    return (
      <tr key={role.authority}>
        <td>{I18n.t('profile.roles.' + role.authority + '.name')}</td>
        <td>{I18n.t('profile.roles.' + role.authority + '.description')}</td>
      </tr>
    )
  }
}

Profile.contextTypes = {
  currentUser: PropTypes.object,
}

export default Profile
