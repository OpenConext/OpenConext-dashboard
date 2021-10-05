import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import I18n from 'i18n-js'
import { isEmpty } from '../utils/utils'
import { privacyProperties } from '../utils/privacy'
import SelectWrapper from './select_wrapper'
import { validEmailRegExp } from '../utils/validations'
import { makeConnection, updateInviteRequest } from '../api'
import ConnectModalContainer from './connect_modal_container'

export default function ConnectModal({ app, currentUser, isOpen, onClose, onSubmit, hasInvite, existingJiraAction }) {
  const title = hasInvite ? 'connect_invite_title' : 'connect_title'
  const subTitle = hasInvite ? 'info_sub_invite_title' : 'info_sub_title'
  const shareInstitutionId = app.institutionId === currentUser.getCurrentIdp().institutionId
  const subTitleAutomaticConnection = automaticallyConnect
    ? I18n.t('how_to_connect_panel.info_connection_without_interaction')
    : shareInstitutionId
    ? I18n.t('how_to_connect_panel.info_connection_share_institution')
    : ''

  const [loaLevel, setLoaLevel] = useState('')
  const [acceptActivationTerms, setAcceptActivationTerms] = useState(false)
  const [emailContactPerson, setEmailContactPerson] = useState('')
  const [refusedShareContactPersonEmail, setRefusedShareContactPersonEmail] = useState(false)
  const [acceptedAansluitOvereenkomstRefused, setAcceptedAansluitOvereenkomstRefused] = useState(false)
  const [comments, setComments] = useState('')
  const [action, setAction] = useState(null)
  const [failed, setFailed] = useState(false)

  const hasPrivacyInfo = privacyProperties.some((prop) => app.privacyInfo[prop])
  const connectAutomaticallyWithEmail = app.dashboardConnectOption === 'CONNECT_WITHOUT_INTERACTION_WITH_EMAIL'
  const inValidContactPersonEmail = !refusedShareContactPersonEmail && !validEmailRegExp.test(emailContactPerson)
  const currentIdp = currentUser.getCurrentIdp()

  const automaticallyConnect =
    connectAutomaticallyWithEmail || app.dashboardConnectOption === 'CONNECT_WITHOUT_INTERACTION_WITHOUT_EMAIL'
  const actionName = hasInvite ? 'approve' : automaticallyConnect ? 'automatic_connect' : 'connect'

  const loaOptions = [{ value: '', display: I18n.t('consent_panel.defaultLoa') }].concat(
    currentUser.loaLevels.map((t) => ({
      value: t,
      display: I18n.t(`consent_panel.${t.substring(t.lastIndexOf('/') + 1).toLowerCase()}`),
    }))
  )

  const validContactPersonEmail =
    !connectAutomaticallyWithEmail || refusedShareContactPersonEmail || validEmailRegExp.test(emailContactPerson)

  const submitAllowed =
    acceptActivationTerms &&
    (!app.aansluitovereenkomstRefused || acceptedAansluitOvereenkomstRefused) &&
    currentUser.dashboardAdmin &&
    validContactPersonEmail

  let stepNumber = 1

  function getPanelRoute(panel) {
    return `/apps/${app.id}/${app.entityType}/${panel}`
  }

  async function submitForm() {
    try {
      if (hasInvite) {
        const action = await updateInviteRequest({
          status: 'ACCEPTED',
          comment: comments,
          jiraKey: existingJiraAction.jiraKey,
          spEntityId: app.spEntityId,
          typeMetaData: app.entityType,
        })

        setAction(action.payload)
      } else {
        const action = await makeConnection(
          app,
          comments,
          loaLevel,
          refusedShareContactPersonEmail ? '' : emailContactPerson
        )

        setAction(action)
      }
    } catch {
      setFailed(true)
    }
    onSubmit()
  }

  if (failed) {
    return (
      <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
        <div>
          <div className="connect-modal-header">{I18n.t('how_to_connect_panel.jira_unreachable')}</div>
          <div className="connect-modal-body">
            <p>{I18n.t('how_to_connect_panel.jira_unreachable_description')} </p>
          </div>
          <div className="buttons">
            <button className="c-button white" onClick={onClose}>
              {I18n.t('how_to_connect_panel.close')}
            </button>
          </div>
        </div>
      </ConnectModalContainer>
    )
  }

  if (action) {
    console.log(action)
    if (existingJiraAction.connectWithoutInteraction) {
      const rejectedOrDone = action.rejected ? 'rejected' : 'done'
      return (
        <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
          <div className="connect-modal-header">
            {I18n.t('how_to_connect_panel.' + rejectedOrDone + '_without_interaction_title')}
          </div>
          <div className="connect-modal-body">
            <p>{I18n.t('how_to_connect_panel.' + rejectedOrDone + '_without_interaction_subtitle')}</p>
          </div>
          <div className="buttons">
            <button className="c-button white" onClick={onClose}>
              {I18n.t('how_to_connect_panel.close')}
            </button>
          </div>
        </ConnectModalContainer>
      )
    }

    const jiraKey = action.jiraKey
    const subtitle = jiraKey
      ? I18n.t('how_to_connect_panel.done_subtitle_with_jira_html', { jiraKey: jiraKey })
      : I18n.t('how_to_connect_panel.done_subtitle_html')

    return (
      <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
        <div className="connect-modal-header">{I18n.t('how_to_connect_panel.done_title')}</div>
        <div className="connect-modal-body">
          <p dangerouslySetInnerHTML={{ __html: subtitle }} />
        </div>
        <div className="buttons">
          <button className="c-button white" onClick={onClose}>
            {I18n.t('how_to_connect_panel.close')}
          </button>
        </div>
      </ConnectModalContainer>
    )
  }

  if (currentIdp.state === 'testaccepted') {
    return (
      <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
        <div>
          <div className="connect-modal-header">
            {I18n.t('how_to_connect_panel.test_connected_no_connection_title', { app: app.name })}
          </div>
          <div className="connect-modal-body">
            <h2>{I18n.t('how_to_connect_panel.test_connected_no_connection_subtitle')}</h2>
            <p
              dangerouslySetInnerHTML={{
                __html: I18n.t('how_to_connect_panel.test_connected_no_connection', { app: app.name }),
              }}
            />
          </div>
          <div className="buttons">
            <button className="c-button white" onClick={onClose}>
              {I18n.t('how_to_connect_panel.close')}
            </button>
          </div>
        </div>
      </ConnectModalContainer>
    )
  }

  if (!currentIdp.publishedInEdugain && app.publishedInEdugain) {
    return (
      <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
        <div className="connect-modal-header">{I18n.t('how_to_connect_panel.not_published_in_edugain_idp')}</div>
        <div className="connect-modal-body">
          <p>{I18n.t('how_to_connect_panel.not_published_in_edugain_idp_info', { name: app.name })} </p>
          <br />
          <Link className="c-button" to={'/my-idp/edit'}>
            {I18n.t('how_to_connect_panel.edit_my_idp_link')}
          </Link>
        </div>
      </ConnectModalContainer>
    )
  }

  return (
    <ConnectModalContainer isOpen={isOpen} onClose={onClose}>
      <div className="connect-modal-header">{I18n.t(`how_to_connect_panel.${title}`, { app: app.name })}</div>
      <div className="connect-modal-subtitle">
        {I18n.t(`how_to_connect_panel.${subTitle}`)} {subTitleAutomaticConnection}
      </div>
      <div className="step">
        <div className="number">{stepNumber++}</div>
        <h2>{I18n.t('how_to_connect_panel.checklist')}</h2>
        <ul>
          <li>
            {I18n.t('how_to_connect_panel.check')}&nbsp;
            <Link to={getPanelRoute('license_data')}>{I18n.t('how_to_connect_panel.license_info')}</Link>
          </li>
          <li>
            {I18n.t('how_to_connect_panel.check')}&nbsp;
            <Link to={getPanelRoute('attribute_policy')}>{I18n.t('how_to_connect_panel.attributes_policy')}</Link>
          </li>
          {hasPrivacyInfo && (
            <li>
              {I18n.t('how_to_connect_panel.check')}&nbsp;
              <Link to={getPanelRoute('privacy')}>{I18n.t('how_to_connect_panel.privacy_policy')}</Link>
            </li>
          )}
          <li>
            <span dangerouslySetInnerHTML={{ __html: I18n.t('how_to_connect_panel.processing_agreements') }} />
          </li>
          <WikiUrl app={app} />
        </ul>
      </div>
      {isEmpty(app.minimalLoaLevel) && (
        <div className="step">
          <div className="number">{stepNumber++}</div>
          <h2>{I18n.t('consent_panel.loa_level')}</h2>
          <p dangerouslySetInnerHTML={{ __html: I18n.t('ssid_panel.subtitle3') }} />
          <div className="grey-container">
            <p>Level of Assurance (LoA)</p>
            <SelectWrapper
              defaultValue={loaLevel}
              options={loaOptions}
              multiple={false}
              handleChange={(val) => setLoaLevel(val)}
            />
          </div>
        </div>
      )}
      <div className="step activation-terms">
        <div className="number">{stepNumber++}</div>
        <h2>{I18n.t('how_to_connect_panel.terms_title')}</h2>
        <ul>
          <li>
            {I18n.t('how_to_connect_panel.provide_attributes.before')}
            <Link to={getPanelRoute('attribute_policy')}>{I18n.t('how_to_connect_panel.attributes')}</Link>
            {I18n.t('how_to_connect_panel.provide_attributes.after')}
          </li>

          <li>
            {I18n.t('how_to_connect_panel.forward_permission.before')}
            <Link to={getPanelRoute('attribute_policy')}>{I18n.t('how_to_connect_panel.attributes')}</Link>
            {I18n.t('how_to_connect_panel.forward_permission.after', { app: app.name })}
          </li>

          <li>
            {I18n.t('how_to_connect_panel.obtain_license.before')}
            <Link to={getPanelRoute('license_data')}>{I18n.t('how_to_connect_panel.license')}</Link>
            {I18n.t('how_to_connect_panel.obtain_license.after', { app: app.name })}
          </li>
        </ul>
        <div className="grey-container">
          <input
            type="checkbox"
            checked={acceptActivationTerms}
            id="activation-terms"
            onChange={(e) => setAcceptActivationTerms(e.target.checked)}
          />
          <label htmlFor="activation-terms">{I18n.t('how_to_connect_panel.accept')}</label>
        </div>
      </div>
      {app.entityType === 'single_tenant_template' && (
        <div className="step single-tenant">
          <div className="number">{stepNumber++}</div>
          <h2>{I18n.t('overview_panel.single_tenant_service')}</h2>
          <p
            className="explanation"
            dangerouslySetInnerHTML={{
              __html: I18n.t('overview_panel.single_tenant_service_html', { name: app.name }),
            }}
          />
          <p>{I18n.t('how_to_connect_panel.single_tenant_service_warning')}</p>
        </div>
      )}
      {connectAutomaticallyWithEmail && (
        <div className="step">
          <div className="number">{stepNumber++}</div>
          <h2>{I18n.t('how_to_connect_panel.activate_with_email.title')}</h2>
          <p>{I18n.t('how_to_connect_panel.activate_with_email.subTitle')}</p>
          <div className="grey-container">
            <div className="automatic-connection-with-email">
              <input
                type="text"
                value={emailContactPerson}
                onChange={(e) => setEmailContactPerson(e.target.value)}
                placeholder={I18n.t('how_to_connect_panel.activate_with_email.emailPlaceholder')}
              />
              {inValidContactPersonEmail && (
                <div className="error">
                  <span>{I18n.t('how_to_connect_panel.activate_with_email.invalidEmail')}</span>
                </div>
              )}
            </div>
            <label>
              <input
                type="checkbox"
                checked={refusedShareContactPersonEmail}
                onChange={(e) => setRefusedShareContactPersonEmail(e.target.checked)}
              />
              {I18n.t('how_to_connect_panel.activate_with_email.disclaimer')}
            </label>
          </div>
        </div>
      )}
      {app.aansluitovereenkomstRefused && (
        <div className="step aansluitovereenkomst">
          <div className="number">{stepNumber++}</div>
          <h2>{I18n.t('overview_panel.aansluitovereenkomst')}</h2>
          <p
            dangerouslySetInnerHTML={{
              __html: I18n.t('overview_panel.aansluitovereenkomstRefused', { organisation: app.organisation }),
            }}
          />
          <div className="grey-container">
            <input
              type="checkbox"
              id="aansluitovereenkomst-refused"
              checked={acceptedAansluitOvereenkomstRefused}
              onChange={(e) => setAcceptedAansluitOvereenkomstRefused(e.target.checked)}
            />
            <label htmlFor="aansluitovereenkomst-refused">
              {I18n.t('how_to_connect_panel.aansluitovereenkomst_accept')}
            </label>
          </div>
        </div>
      )}
      <div className="step">
        <div className="number">{stepNumber++}</div>
        <h2>{I18n.t('how_to_connect_panel.comments_title')}</h2>
        <p>{I18n.t('how_to_connect_panel.comments_description')}</p>
        <div className="grey-container">
          <textarea
            rows="5"
            value={comments}
            onChange={(e) => setComments(e.target.value)}
            placeholder={I18n.t('how_to_connect_panel.comments_placeholder')}
          />
        </div>
      </div>
      <div className="buttons">
        <button className="c-button white" onClick={onClose}>
          {I18n.t('how_to_connect_panel.cancel')}
        </button>
        <button disabled={!submitAllowed} className="c-button" onClick={submitForm}>
          {I18n.t(`how_to_connect_panel.${actionName}`)}
        </button>
      </div>
    </ConnectModalContainer>
  )
}

function WikiUrl({ app }) {
  if (app.wikiUrl) {
    return (
      <li>
        {I18n.t('how_to_connect_panel.read')}&nbsp;
        <a href={app.wikiUrl} target="_blank" rel="noopener noreferrer">
          {I18n.t('how_to_connect_panel.wiki')}
        </a>
      </li>
    )
  }

  return null
}
