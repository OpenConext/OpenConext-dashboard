import React, { useContext, useEffect, useState } from 'react'
import { CurrentUserContext } from '../App'
import I18n from 'i18n-js'
import isEmpty from 'lodash.isempty'
import { useHistory, useParams } from 'react-router-dom'
import { createPolicy, getAllowedAttributes, getNewPolicy, getPolicy, updatePolicy } from '../api'
import PolicyAttributeSelect from '../components/policy_attribute_select'
import SelectWrapper from '../components/select_wrapper'
import { ReactComponent as PermitIcon } from '../../images/human-resources-offer-employee-1.svg'
import { ReactComponent as DenyIcon } from '../../images/allowances-no-talking.svg'
import { ReactComponent as AndIcon } from '../../images/rule-type-and.svg'
import { ReactComponent as OrIcon } from '../../images/rule-type-or.svg'
import AutoFormat from '../utils/autoformat_policy'
import { setFlash } from '../utils/flash'

export default function AuthorizationPolicyDetail({ app, type }) {
  const params = useParams()
  const history = useHistory()
  const { currentUser } = useContext(CurrentUserContext)
  const [policy, setPolicy] = useState(null)
  const [allowedAttributes, setAllowedAttributes] = useState([])
  const [autoFormat, setAutoFormat] = useState(false)

  async function fetchPolicy() {
    if (params.id === 'new') {
      const res = await getNewPolicy()
      const policy = res.payload
      policy.serviceProviderId = app.spEntityId
      policy.serviceProviderName = app.name
      setPolicy(policy)
    } else {
      const res = await getPolicy(params.id)
      setPolicy(res.payload)
    }
  }

  async function fetchAllowedAttributes() {
    const res = await getAllowedAttributes()
    setAllowedAttributes(res.payload)
  }

  useEffect(() => {
    fetchPolicy()
    fetchAllowedAttributes()
  }, [params.id])

  if (!policy) {
    return null
  }

  const description = autoFormat ? AutoFormat.description(policy) : policy.description || ''

  async function saveChanges(active) {
    policy.active = active
    policy.description = description
    const apiCall = policy.id ? updatePolicy : createPolicy
    const action = policy.id ? I18n.t('policies.flash_updated') : I18n.t('policies.flash_created')
    try {
      await apiCall(policy)
      if (app.policyEnforcementDecisionRequired) {
        setFlash(I18n.t('policies.flash', { policyName: policy.name, action }))
      } else {
        setFlash(I18n.t('policies.flash_first'))
      }
      history.replace(`/apps/${app.id}/${type}/settings/authorization_policies`)
    } catch (e) {
      if (e.response && e.response.json) {
        e.response.json().then((json) => {
          let message = 'error'
          if (json.exception && json.exception.indexOf('PolicyNameNotUniqueException') > 0) {
            message = I18n.t('policies.policy_name_not_unique_exception')
          }
          setFlash(message, 'error')
        })
      } else {
        setFlash(e, 'error')
      }
    }
  }

  const title = policy.id ? I18n.t('policy_detail.update_policy') : I18n.t('policy_detail.create_policy')
  const providers = currentUser.institutionIdps.map((idp) => ({ value: idp.id, display: idp.name }))

  const emptyAttributes = policy.attributes.filter((attr) => {
    return isEmpty(attr.value)
  })
  const invalidPolicy =
    isEmpty(policy.name) ||
    isEmpty(description) ||
    !policy.serviceProviderId ||
    isEmpty(policy.attributes) ||
    emptyAttributes.length > 0 ||
    isEmpty(policy.denyAdvice) ||
    isEmpty(policy.denyAdviceNl)

  return (
    <div className="authorization-policy-detail">
      <div className="authorization-policy-header">
        <h2>{title}</h2>
        <div className={`status-badge ${policy.active ? 'active' : 'inactive'}`}>
          {I18n.t(`policy_detail.${policy.active ? 'active' : 'inactive'}`)}
        </div>
      </div>
      <p className="intro" dangerouslySetInnerHTML={{ __html: I18n.t('policy_detail.intro') }} />
      <div className="section-header">{I18n.t('policy_detail.about')}</div>
      <div className="form-element">
        <fieldset className={isEmpty(policy.name) ? 'failure' : 'success'}>
          <label htmlFor="name">{I18n.t('policy_detail.name')}</label>
          <input
            type="text"
            name="name"
            id="name"
            className="form-input"
            value={policy.name || ''}
            onChange={(e) => setPolicy({ ...policy, name: e.target.value })}
          />
        </fieldset>
      </div>
      <div className="form-element">
        <fieldset className="success">
          <label>{I18n.t('policy_detail.institutions')}</label>
          <SelectWrapper
            defaultValue={policy.identityProviderIds}
            placeholder={I18n.t('policy_detail.idps_placeholder')}
            options={providers}
            multiple={true}
            handleChange={(e) => setPolicy({ ...policy, identityProviderIds: e })}
          />
        </fieldset>
      </div>
      <div className="form-element">
        <fieldset className="success">
          <label>{I18n.t('policy_detail.access')}</label>
          <div className="grey-block">
            <div
              role="button"
              className={`select-block permit ${!policy.denyRule && 'selected'}`}
              onClick={() => setPolicy({ ...policy, denyRule: false })}
              tabIndex={-1}
            >
              <div className="select-block-header">
                <PermitIcon />
                <span>{I18n.t('policy_detail.permit')}</span>
              </div>
              <p>{I18n.t('policy_detail.permit_info')}</p>
            </div>
            <div
              role="button"
              className={`select-block deny ${policy.denyRule && 'selected'}`}
              onClick={() => setPolicy({ ...policy, denyRule: true, allAttributesMustMatch: true })}
              tabIndex={-1}
            >
              <div className="select-block-header">
                <DenyIcon />
                <span>{I18n.t('policy_detail.deny')}</span>
              </div>
              <p>{I18n.t('policy_detail.deny_info')}</p>
            </div>
          </div>
        </fieldset>
      </div>
      <div className="section-header">{I18n.t('policy_detail.rules')}</div>
      <div className="form-element">
        <fieldset className="success">
          <label>{I18n.t('policy_detail.rule')}</label>
          <div className="grey-block">
            <div
              role="button"
              className={`select-block and ${policy.allAttributesMustMatch && 'selected'} ${
                policy.denyRule && 'disabled'
              }`}
              onClick={() => !policy.denyRule && setPolicy({ ...policy, allAttributesMustMatch: true })}
              tabIndex={-1}
            >
              <div className="select-block-header">
                <AndIcon />
                <span>{I18n.t('policy_detail.rule_and')}</span>
              </div>
              <p>{I18n.t('policy_detail.rule_and_info')}</p>
            </div>
            <div
              role="button"
              className={`select-block or ${!policy.allAttributesMustMatch && 'selected'} ${
                policy.denyRule && 'disabled'
              }`}
              onClick={() => !policy.denyRule && setPolicy({ ...policy, allAttributesMustMatch: false })}
              tabIndex={-1}
            >
              <div className="select-block-header">
                <OrIcon />
                <span>{I18n.t('policy_detail.rule_or')}</span>
              </div>
              <p>{I18n.t('policy_detail.rule_or_info')}</p>
            </div>
          </div>
        </fieldset>
      </div>
      <div className="form-element">
        <fieldset className="success">
          <label>{I18n.t('policy_detail.attribute')}</label>
          <PolicyAttributeSelect
            policy={policy}
            allowedAttributes={allowedAttributes}
            onChange={(attributes) => setPolicy({ ...policy, attributes: attributes })}
          />
        </fieldset>
      </div>
      <div className="form-element">
        <fieldset className="success">
          <div className="description-header">
            <label>{I18n.t('policy_detail.description')}</label>
            <div className="auto-format">
              <input
                type="checkbox"
                id="autoFormatDescription"
                name="autoFormatDescription"
                checked={autoFormat}
                onChange={() => setAutoFormat(!autoFormat)}
              />
              <label className="note" htmlFor="autoFormatDescription">
                {I18n.t('policy_detail.autoFormat')}
              </label>
            </div>
          </div>
          <textarea
            rows="4"
            name="description"
            value={description}
            className="form-input"
            onChange={(e) => setPolicy({ ...policy, description: e.target.value })}
          />
        </fieldset>
      </div>
      <div className="section-header">{I18n.t('policy_detail.access_denied_message')}</div>
      <div className="form-element">
        <fieldset className="success">
          <label>{I18n.t('policy_detail.deny_message')}</label>
          <input
            type="text"
            name="denyMessage"
            className="form-input"
            value={policy.denyAdvice || ''}
            onChange={(e) => setPolicy({ ...policy, denyAdvice: e.target.value })}
          />
          <label>{I18n.t('policy_detail.deny_message_nl')}</label>
          <input
            type="text"
            name="denyMessageNl"
            className="form-input"
            value={policy.denyAdviceNl || ''}
            onChange={(e) =>
              setPolicy({
                ...policy,
                denyAdviceNl: e.target.value,
              })
            }
          />
          {currentUser.supportedLanguages.indexOf('pt') > -1 && (
            <div>
              <label>{I18n.t('policy_detail.deny_message_pt')}</label>
              <input
                type="text"
                name="denyMessagePt"
                className="form-input"
                value={policy.denyAdvicePt || ''}
                onChange={(e) =>
                  setPolicy({
                    ...policy,
                    denyAdvicePt: e.target.value,
                  })
                }
              />
            </div>
          )}
        </fieldset>
      </div>
      <div className="form-element buttons">
        <fieldset>
          <button className={'c-button white'} onClick={() => saveChanges(false)} disabled={invalidPolicy}>
            {policy.active ? I18n.t('policy_detail.deactivate_policy') : I18n.t('policy_detail.save_changes')}
          </button>
          <button className="c-button" onClick={() => saveChanges(true)} disabled={invalidPolicy}>
            {policy.active ? I18n.t('policy_detail.save_changes') : I18n.t('policy_detail.activate_policy')}
          </button>
        </fieldset>
      </div>
    </div>
  )
}
