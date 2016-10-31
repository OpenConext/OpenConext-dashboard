import qs from "qs";
import spinner from "../lib/spin";
import { getCurrentUser } from "../models/current_user";

const apiPath = "/dashboard/api";

export function apiUrl(path) {
  return apiPath + path;
}

function validateResponse(res) {
  spinner.stop();

  if (!res.ok) {
    const error = new Error(res.statusText);
    error.response = res;
    throw error;
  }

  return res;
}

export function parseJson(res) {
  return res.json();
}

function validFetch(path, options) {
  const headers = {
    "Accept": "application/json"
  };

  const currentUser = getCurrentUser();

  if (currentUser) {
    headers["X-IDP-ENTITY-ID"] = currentUser.getCurrentIdpId();
  }

  const fetchOptions = _.merge({}, { headers }, options, {
    credentials: "same-origin"
  });

  spinner.start();
  return fetch(apiUrl(path), fetchOptions)
  .catch(err => {
    spinner.stop();
    throw err;
  })
  .then(validateResponse);
}

export function fetchJson(path, options = {}) {
  return validFetch(path, options)
  .then(parseJson);
}

function fetchPost(path, body, options = {}) {
  const data = new FormData();

  for (const key in body) {
    if (body.hasOwnProperty(key)) {
      data.append(key, body[key]);
    }
  }

  return validFetch(path, Object.assign({}, { method: "post", body: data }, options));
}

function postJson(path, body, options = {}) {
  return validFetch(path, Object.assign({}, { method: "post", body: JSON.stringify(body) }, options));
}

function putJson(path, body, options = {}) {
  return validFetch(path, Object.assign({}, { method: "put", body: JSON.stringify(body) }, options));
}

function fetchDelete(path) {
  return validFetch(path, { method: "delete" });
}

export function getUserData() {
  return fetchJson("/users/me" + window.location.search);
}

export function getFacets() {
  return fetchJson("/facets");
}

export function getApps() {
  return fetchJson("/services");
}

export function getApp(appId) {
  return fetchJson(`/services/id/${appId}`);
}

export function getIdps(spEntityId) {
  return fetchJson(`/services/idps?${qs.stringify({ spEntityId })}`);
}

export function getPolicies() {
  return fetchJson("/policies");
}

export function getInstitutionServiceProviders() {
  return fetchJson("/users/me/serviceproviders");
}

export function getGuestEnabledServices() {
  return fetchJson("/users/me/guest-enabled-services");
}

export function getConnectedServiceProviders(idpId) {
  return fetchJson("/services/connected", {
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  });
}

export function getAllowedAttributes() {
  return fetchJson("/policies/attributes");
}

export function getNewPolicy() {
  return fetchJson("/policies/new");
}

export function logout() {
  return validFetch("/logout");
}

export function exit() {
  return validFetch("/users/me/switch-to-idp");
}

export function switchToIdp(idpId, role) {
  return validFetch("/users/me/switch-to-idp?" + qs.stringify({ idpId, role }));
}

export function getNotifications() {
  return fetchJson("/notifications");
}

export function getActions() {
  return fetchJson("/actions");
}

export function makeConnection(app, comments) {
  return fetchPost(`/services/id/${app.id}/connect`, { comments: comments, spEntityId: app.spEntityId })
  .then(parseJson)
  .then(json => json.payload);
}

export function removeConnection(app, comments) {
  return fetchPost(`/services/id/${app.id}/disconnect`, { comments: comments, spEntityId: app.spEntityId })
  .then(parseJson)
  .then(json => json.payload);
}

export function getIdpRolesWithUsers() {
  return fetchJson("/idp/current/roles");
}

export function getLicenseContactPerson() {
  return fetchJson("/idp/licensecontactpersons");
}

export function getIdpsForSuper() {
  return fetchJson("/users/super/idps")
  .then(json => json.payload);
}

export function createPolicy(policy) {
  return postJson("/policies", policy, {
    headers: {
      "Content-Type": "application/json"
    }
  });
}

export function updatePolicy(policy) {
  return putJson("/policies", policy, {
    headers: {
      "Content-Type": "application/json"
    }
  });
}

export function deletePolicy(policyId) {
  return fetchDelete(`/policies/${policyId}`);
}

export function getPolicy(policyId) {
  return fetchJson(`/policies/${policyId}`);
}

export function getPolicyRevisions(policyId) {
  return fetchJson(`/policies/${policyId}/revisions`);
}
