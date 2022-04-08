import React, { useContext } from 'react'
import I18n from 'i18n-js'
import { CurrentUserContext } from '../App'
import ReactTooltip from 'react-tooltip'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faQuestionCircle } from '@fortawesome/free-regular-svg-icons'
import { privacyProperties } from '../utils/privacy'
import { marked } from 'marked'

export default function AttributesAndPrivacy({ app }) {
  const hasPrivacyInfo = privacyProperties.some((prop) => app.privacyInfo[prop])

  return (
    <div className="app-detail-content attributes-privacy">
      <h2>{I18n.t('attributes_policy_panel.title')}</h2>
      <p>{I18n.t('attributes_policy_panel.subtitle', { name: app.name })}</p>
      <AttributeReleasePolicy app={app} />
      {app.manipulationNotes && <ManipulationNotes app={app} />}

      {hasPrivacyInfo && <PrivacyInfo app={app} />}
    </div>
  )
}

function AttributeReleasePolicy({ app }) {
  const { currentUser } = useContext(CurrentUserContext)

  if (app.arp.noArp) {
    return <p>{I18n.t('attributes_policy_panel.arp.noarp', { name: app.name })}</p>
  } else if (app.arp.noAttrArp) {
    return <p>{I18n.t('attributes_policy_panel.arp.noattr', { name: app.name })}</p>
  }

  const hasFilters = app.filteredUserAttributes.some(
    (attribute) => attribute.filters.filter((filter) => filter !== '*').length > 0
  )

  const nameIdValue = app.nameIds.filter((val) => val.includes('unspecified') || val.includes('persistent')).length
    ? 'Persistent'
    : 'Transient'

  return (
    <div>
      <table className="attributes">
        <thead>
          <tr>
            <th scope="col" className="attribute">
              {I18n.t('attributes_policy_panel.attribute')}
            </th>
            {!currentUser.guest && <th className="value">{I18n.t('attributes_policy_panel.your_value')}</th>}
            <th scope="col" className="motivation">
              {I18n.t('attributes_policy_panel.motivation')}
            </th>
          </tr>
        </thead>
        <tbody>
          {app.filteredUserAttributes.map((attribute) => (
            <Attribute app={app} attribute={attribute} currentUser={currentUser} key={attribute.name} />
          ))}
        </tbody>
      </table>
      <p>{I18n.t('attributes_policy_panel.warning')}</p>
      <ul className="attributes-policy-warnings">
        {!currentUser.guest && <li>{I18n.t('attributes_policy_panel.hint')}</li>}
        <li>{I18n.t('attributes_policy_panel.motivationInfo')}</li>
        {hasFilters && <li>{I18n.t('attributes_policy_panel.filterInfo')}</li>}
        <li dangerouslySetInnerHTML={{ __html: I18n.t('attributes_policy_panel.nameIdInfo', { type: nameIdValue }) }} />
      </ul>
    </div>
  )
}

function Attribute({ attribute, app, currentUser }) {
  const renderFilters = attribute.filters.filter((flt) => flt !== '*')

  let name = attribute.name
  let tooltip = undefined

  if (name === 'urn:oid:1.3.6.1.4.1.1076.20.40.40.1') {
    name = 'collabPersonId'
    tooltip = 'Collab unique user identifier'
  }

  return (
    <tr key={name}>
      <td>
        {name}
        {tooltip && (
          <span>
            <FontAwesomeIcon icon={faQuestionCircle} data-tip data-for={name} />
            <ReactTooltip id={name} type="info" class="tool-tip" effect="solid">
              {tooltip}
            </ReactTooltip>
          </span>
        )}
        {renderFilters.length > 0 && !currentUser.guest && (
          <div className="filter-info">{I18n.t('attributes_policy_panel.filter')}</div>
        )}
      </td>
      {!currentUser.guest && (
        <td>
          <ul>
            {attribute.userValues.length > 0 ? (
              attribute.userValues.map((val) => <li key={val}>{val}</li>)
            ) : (
              <em className="no-attribute-value">
                {name.indexOf('eduPersonTargetedID') > 0
                  ? I18n.t('attributes_policy_panel.attribute_value_generated')
                  : I18n.t('attributes_policy_panel.no_attribute_value')}
              </em>
            )}
          </ul>
          {renderFilters.length > 0 && (
            <ul className="filters">
              {renderFilters.map((filter, i) => (
                <li key={i} className="filter">
                  - {filter}
                </li>
              ))}
            </ul>
          )}
        </td>
      )}
      <td>{app.motivations[name]}</td>
    </tr>
  )
}

function PrivacyInfo({ app }) {
  return (
    <div className="privacy">
      <h2>{I18n.t('privacy_panel.title')}</h2>
      <p>{I18n.t('privacy_panel.subtitle', { name: app.name })}</p>
      <p>{I18n.t('privacy_panel.subtitle2', { name: app.name })}</p>
      <PrivacyTable app={app} />
    </div>
  )
}

function PrivacyTable({ app }) {
  return (
    <table>
      <thead>
        <tr>
          <th className="question">{I18n.t('privacy_panel.question')}</th>
          <th answer="question">{I18n.t('privacy_panel.answer')}</th>
        </tr>
      </thead>
      <tbody>
        {privacyProperties.map((prop) => (
          <PrivacyProp name={prop} prop={app.privacyInfo[prop]} key={prop} />
        ))}
      </tbody>
    </table>
  )
}

function PrivacyProp({ name, prop }) {
  const isDate = name === 'certificationValidTo' || name === 'certificationValidFrom'
  const noValue = prop === undefined || prop === null
  let value
  if (isDate && !noValue) {
    value = prop.substring(0, 10)
  } else if (prop === true) {
    value = I18n.t('boolean.yes')
  } else if (prop === false) {
    value = I18n.t('boolean.no')
  } else {
    value = prop
  }
  return (
    <tr key={name} className="privacy-row">
      <td>{I18n.t(`privacy_panel.${name}`)}</td>
      <td className="value">{noValue ? <em>{I18n.t('privacy_panel.noInformation')}</em> : value}</td>
    </tr>
  )
}

function ManipulationNotes({ app }) {
  const notes = marked(app.manipulationNotes).replace(/<a href/g, '<a target="_blank" href')
  return (
    <div className="manipulation-notes">
      <p className="title">{I18n.t('attributes_policy_panel.arp.manipulation')}</p>
      <section className="notes" dangerouslySetInnerHTML={{ __html: notes }} />
    </div>
  )
}
