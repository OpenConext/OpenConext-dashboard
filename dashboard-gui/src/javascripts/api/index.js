import qs from 'qs'
import spinner from '../lib/spin'
import { getCurrentUser } from '../models/current_user'
import merge from 'lodash.merge'
import { isEmpty } from '../utils/utils'
import { emitter } from '../utils/flash'

const apiPath = '/dashboard/api'

export function apiUrl(path) {
  return apiPath + path
}

function validateResponse(res) {
  spinner.stop()

  if (!res.ok) {
    const error = new Error(res.statusText)
    error.response = res
    throw error
  }

  return res
}

export function parseJson(res) {
  return res.json()
}

function validFetch(path, options, currentUser = getCurrentUser(), idp = undefined) {
  const headers = {
    Accept: 'application/json',
  }
  if (currentUser || idp) {
    headers['X-IDP-ENTITY-ID'] = idp || currentUser.getCurrentIdpId()
  }

  const fetchOptions = merge({}, { headers }, options, {
    credentials: 'same-origin',
  })

  spinner.start()
  return fetch(apiUrl(path), fetchOptions)
    .catch((err) => {
      spinner.stop()
      throw err
    })
    .then(validateResponse)
}

export function fetchJson(path, options = {}) {
  return validFetch(path, options).then(parseJson)
}

function fetchPost(path, body, options = {}) {
  const data = new FormData()

  for (const key in body) {
    if (Object.prototype.hasOwnProperty.call(body, key)) {
      data.append(key, body[key])
    }
  }

  return validFetch(path, Object.assign({}, { method: 'post', body: data }, options))
}

function postJson(path, body, options = {}) {
  return validFetch(path, Object.assign({}, { method: 'post', body: JSON.stringify(body) }, options))
}

function putJson(path, body, options = {}) {
  return validFetch(path, Object.assign({}, { method: 'put', body: JSON.stringify(body) }, options))
}

function fetchDelete(path) {
  return validFetch(path, { method: 'delete' })
}

function S4() {
  return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
}

export function getUserData(redirect = 'manual') {
  const fetchOptions = {
    headers: {
      Accept: 'application/json',
    },
    credentials: 'same-origin',
    redirect: redirect,
  }
  spinner.start()
  return fetch(apiUrl('/users/me' + window.location.search), fetchOptions).then((response) => {
    spinner.stop()
    if (response.ok) {
      return parseJson(response)
    }
    const guid = (
      S4() +
      S4() +
      '-' +
      S4() +
      '-4' +
      S4().substr(0, 3) +
      '-' +
      S4() +
      '-' +
      S4() +
      S4() +
      S4()
    ).toLowerCase()
    if (document.location.href.indexOf('guid') > -1) {
      return { noAccess: true }
    }
    document.location = document.location + '?guid=' + guid
    return {}
  })
}

export function getApps() {
  return fetchJson('/services')
}

export function getAppsForInvitationRequest(idpEntityId) {
  return validFetch('/services/invitation-request-services', {}, null, idpEntityId).then(parseJson)
}

export function getApp(appId, type) {
  return fetchJson(`/services/detail?spId=${appId}&entityType=${type}`)
}

export function inviteRequest(data) {
  return postJson('/users/inviteRequest', data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
    .then((res) => {
      emitter.emit('invite_request_updates', true)
      return res
    })
    .then(parseJson)
}

export function resendInviteRequest(data) {
  return postJson('/users/resendInviteRequest', data, {
    headers: {
      'Content-Type': 'application/json',
    },
  }).then(parseJson)
}

export function updateInviteRequest(data) {
  return putJson('/users/inviteRequest', data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
    .then((res) => {
      emitter.emit('invite_request_updates', true)
      return res
    })
    .then(parseJson)
}

export function getIdps(spEntityId, type) {
  return fetchJson(`/services/idps?${qs.stringify({ spEntityId })}&type=${type}`)
}

export function getServicesByEntityIds(entityIds) {
  return postJson('/services/by-entity-ids', entityIds, {
    headers: {
      'Content-Type': 'application/json',
    },
  }).then(parseJson)
}

export function getPolicies() {
  return fetchJson('/policies')
}

export function getInstitutionServiceProviders() {
  return fetchJson('/users/me/serviceproviders')
}

export function getConnectedServiceProviders(idpId) {
  return fetchJson('/services/connected', {
    headers: {
      'X-IDP-ENTITY-ID': idpId,
    },
  })
}

export function sabRoles(institutionId) {
  return fetchJson(`/idp/sab/roles?institutionId=${institutionId}`)
}

export function getAllowedAttributes() {
  return fetchJson('/policies/attributes')
}

export function getNewPolicy() {
  return fetchJson('/policies/new')
}

export function logout() {
  return validFetch('/logout')
}

export function exit() {
  return validFetch('/users/me/switch-to-idp')
}

export function switchToIdp(idpId, role) {
  return validFetch('/users/me/switch-to-idp?' + qs.stringify({ idpId, role }))
}

export function searchJira(filter) {
  return postJson('/actions', filter, {
    headers: {
      'Content-Type': 'application/json',
    },
  }).then(parseJson)
}

export function makeConnection(app, comments, loaLevel, emailContactPerson) {
  return fetchPost('/services/connect', {
    comments: comments,
    loaLevel: loaLevel,
    spEntityId: app.spEntityId,
    type: app.entityType,
    emailContactPerson: emailContactPerson,
  })
    .then(parseJson)
    .then((json) => json.payload)
}

export function removeConnection(app, comments) {
  return fetchPost('/services/disconnect', { comments: comments, spEntityId: app.spEntityId, type: app.entityType })
    .then(parseJson)
    .then((json) => json.payload)
}

export function getIdpRolesWithUsers() {
  return fetchJson('/idp/current/roles')
}

export function getIdpsForSuper() {
  return fetchJson('/users/super/idps').then((json) => json.payload)
}

export function createPolicy(policy) {
  return postJson('/policies', policy, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

export function updatePolicy(policy) {
  return putJson('/policies', policy, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

export function deletePolicy(policyId) {
  return fetchDelete(`/policies/${policyId}`)
}

export function getPolicy(policyId) {
  return fetchJson(`/policies/${policyId}`)
}

export function getPolicyRevisions(policyId) {
  return fetchJson(`/policies/${policyId}/revisions`)
}

export function sendChangeRequest(data) {
  return postJson('/users/me/settings', data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

export function loginTimeFrame(from, to, scale, spEntityId) {
  const sp = !isEmpty(spEntityId) ? `&spEntityId=${spEntityId}` : ''
  return fetchJson(`/stats/loginTimeFrame?from=${from}&to=${to}&scale=${scale}&${sp}`)
}

export function loginAggregated(period, spEntityId) {
  const sp = !isEmpty(spEntityId) ? `&spEntityId=${spEntityId}` : ''
  return fetchJson(`/stats/loginAggregated?period=${period}${sp}`)
}

export function uniqueLoginCount(from, to, spEntityId) {
  return fetchJson(`/stats/uniqueLoginCount?from=${from}&to=${to}&spEntityId=${spEntityId}`)
}

export function exportApps(idp, ids) {
  return postJson(
    '/services/download',
    { idp: idp, ids: ids },
    {
      headers: {
        'Content-Type': 'application/json',
      },
    }
  ).then(parseJson)
}

export function consentChangeRequest(data) {
  return postJson('/users/me/consent', data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

export function surfSecureIdChangeRequest(data) {
  return postJson('/users/me/surfsecureid', data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

export function mfaChangeRequest(data) {
  return postJson('/users/me/mfa', data, {
    headers: {
      'Content-Type': 'application/json',
    },
  })
}


export function disableConsent() {
  return fetchJson('/users/disableConsent')
}
