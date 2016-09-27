import qs from "qs";

const apiPath = "/dashboard/api";

export function apiUrl(path) {
  return apiPath + path;
}

function validateResponse(res) {
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

  const fetchOptions = _.merge({}, { headers }, options, {
    credentials: "same-origin"
  });

  return fetch(apiUrl(path), fetchOptions)
  .then(validateResponse);
}

export function fetchJson(path, options = {}) {
  return validFetch(path, options)
  .then(parseJson);
}

function fetchDelete(path) {
  return validFetch(path, { method: "delete" });
}

function fetchPost(path, body, options = {}) {
  var data = new FormData();

  for (var key in body) {
    if (body.hasOwnProperty(key)) {
      data.append(key, body[key]);
    }
  }

  return validFetch(path, Object.assign({}, { method: "post", body: data }, options));
}

export function getUserData() {
  return fetchJson("/users/me" + window.location.search);
}

export function getFacets() {
  return fetchJson("/facets");
}

export function getApps(idpId) {
  return fetchJson("/services", {
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  });
}

export function downloadOverview(idpId, ids) {
  return fetchJson("/services/download", {
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  });
}

export function getApp(appId, idpId) {
  return fetchJson(`/services/id/${appId}`,{
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  });
}

export function getIdps(spEntityId, idpId) {
  return fetchJson(`/services/idps?${qs.stringify({ spEntityId })}`, {
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  });
}

export function getPolicies() {
  return fetchJson("/policies");
}

export function getInstitutionServiceProviders() {
  return fetchJson('/users/me/serviceproviders');
}

export function getConnectedServiceProviders(idpId) {
  return fetchJson('/services/connected', {
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  });
}

export function getAllowedAttributes() {
  return fetchJson('/policies/attributes');
}

export function getNewPolicy() {
  return fetchJson('/policies/new');
}

export function logout() {
  return validFetch("/logout");
}

export function switchToIdp(idpId, role) {
  return validFetch('/users/me/switch-to-idp?' + qs.stringify({ idpId, role }));
}

export function getNotifications(idpId) {
  return fetchJson('/notifications', {
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  });
}

export function getActions(idpId) {
  return fetchJson('/actions', {
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  });
}

export function makeConnection(idpId, app, comments) {
  return fetchPost(`/services/id/${app.id}/connect`, {comments: comments, spEntityId: app.spEntityId}, {
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  })
  .then(parseJson)
  .then(json => json.payload);
}

export function removeConnection(idpId, app, comments) {
  return fetchPost(`/services/id/${app.id}/disconnect`, {comments: comments, spEntityId: app.spEntityId}, {
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  })
  .then(parseJson)
  .then(json => json.payload);
}

export function getIdpRolesWithUsers(idpId) {
  return fetchJson('/idp/current/roles', {
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  });
}

export function getLicenseContactPerson(idpId) {
  return fetchJson('/idp/licensecontactpersons', {
    "headers": {
      "X-IDP-ENTITY-ID": idpId
    }
  });
}
