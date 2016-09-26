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
    "Accept": "application/json",
    "Content-Type": "application/json"
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

function fetchPost(path, body) {
  return validFetch(path, { method: "post", body: JSON.stringify(body) });
}

export function getUserData() {
  return fetchJson("/users/me");
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
