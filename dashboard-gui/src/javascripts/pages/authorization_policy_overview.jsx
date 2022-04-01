import React, { useContext, useEffect, useState } from 'react'
import {Link, useHistory} from 'react-router-dom'
import I18n from 'i18n-js'
import { deletePolicy, getPolicies } from '../api'
import { CurrentUserContext } from '../App'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faPencilAlt, faTrashAlt } from '@fortawesome/free-solid-svg-icons'
import { setFlash } from '../utils/flash'
import StepUpModal from "../components/step_up_modal";

export default function AuthorizationPolicyOverview({ app, type, onPolicyChange }) {
  const history = useHistory()
  const [policies, setPolicies] = useState([])
  const [unreachable, setUnreachable] = useState(false)
  const [loading, setLoading] = useState(true)
  const { currentUser } = useContext(CurrentUserContext)
  const [showStepUpModal, setShowStepUpModal] = useState(false)
  const [locationAfterStepup, setLocationAfterStepup] = useState(false)


  const isAllowedToMaintainPolicies =
    currentUser.dashboardAdmin || currentUser.getCurrentIdp().allowMaintainersToManageAuthzRules

  const isPolicyActive = policy => app.policyEnforcementDecisionRequired && policy.active

  const checkLoaLevel = (theLocation, callback) => {
    if (currentUser.currentLoaLevel < 2 && currentUser.dashboardStepupEnabled) {
      setShowStepUpModal(true)

      setLocationAfterStepup(theLocation)
    } else {
      callback();
    }
  }

  async function fetchPolicies() {
    try {
      const res = await getPolicies()
      const policiesForApp = res.payload.filter((policy) => policy.serviceProviderId === app.spEntityId)
      setPolicies(policiesForApp)
      setLoading(false)
    } catch (e) {
      setUnreachable(true)
    }
  }

  async function handleDeletePolicy(policy) {
    if (window.confirm(I18n.t('policies.confirmation', { policyName: policy.name }))) {
      await deletePolicy(policy.id)
      await fetchPolicies()
      onPolicyChange()
      setFlash(
        I18n.t('policies.flash', {
          policyName: policy.name,
          action: I18n.t('policies.flash_deleted'),
        })
      )
    }
  }

  useEffect(() => {
    fetchPolicies()
  }, [])

  if (unreachable) {
    return (
      <div>
        <h2>{I18n.t('policies.pdp_unreachable')}</h2>
        <p>{I18n.t('policies.pdp_unreachable_description')} </p>
      </div>
    )
  }

  return (
    <div>
      <div className="policy-overview-header">
        <h2>{I18n.t('policies.overview.header')}</h2>
        {isAllowedToMaintainPolicies &&
        <button className="c-button" onClick={() => checkLoaLevel(`${window.location.origin}/apps/${app.id}/${type}/settings/authorization_policies/new`,
            () => history.replace(`/apps/${app.id}/${type}/settings/authorization_policies/new`))}>
          {I18n.t('policies.new_policy')}
        </button>
        }
      </div>

      <div>
        {!loading && policies.length === 0 && <div>{I18n.t('policies.no_policies')}</div>}
        {policies.map((policy) => (
          <div className="policy-block" key={policy.id}>
            <div className="policy-name-description">
              <h3>{policy.name}</h3>
              {policy.description}
              {policy.identityProviderNames.length > 0 && (
                <div>
                  <strong>{I18n.t('policies.overview.identityProviderNames')}</strong>:{' '}
                  {policy.identityProviderNames.join(', ')}
                </div>
              )}
            </div>
            <div className="policy-actions">
              <div className={`active-state ${isPolicyActive(policy) && 'active'}`}>
                {I18n.t(`policies.overview.${isPolicyActive(policy) ? 'active' : 'inactive'}`)}
              </div>
              {isAllowedToMaintainPolicies && (
                <div>
                  {I18n.t('policies.overview.numberOfRevisions')}:{' '}
                  <Link to={`/apps/${app.id}/${type}/settings/authorization_policies/${policy.id}/revisions`}>
                    {policy.numberOfRevisions}
                  </Link>
                </div>
              )}
              {isAllowedToMaintainPolicies && policy.actionsAllowed && (
                <div className="policy-buttons">
                  <button type="button" className="c-button"
                          onClick={() => checkLoaLevel(`${window.location.origin}/apps/${app.id}/${type}/settings/authorization_policies/${policy.id}`,
                      () => history.replace(`/apps/${app.id}/${type}/settings/authorization_policies/${policy.id}`))}>
                    <FontAwesomeIcon icon={faPencilAlt} />
                  </button>
                  <button type="button" className="c-button"
                          onClick={() => checkLoaLevel(window.location.href,
                              () => handleDeletePolicy(policy))}>
                    <FontAwesomeIcon icon={faTrashAlt} />
                  </button>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
      <StepUpModal
          app={app}
          location={locationAfterStepup}
          isOpen={showStepUpModal}
          onClose={() => setShowStepUpModal(false)}
      />
    </div>
  )
}
