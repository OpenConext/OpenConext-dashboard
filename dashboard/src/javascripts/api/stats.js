import qs from "qs";

export const STATS_HOST = "https://stats.surfconext.nl";
const apiPath = `${STATS_HOST}/api/v1`;

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

export function retrieveSp(entityId, statsToken) {
  return fetchJson("/entity/sp.json?" + qs.stringify({
    "entityid": entityId,
    "active": 1,
    "state": "PA",
    "access_token": statsToken
  })).then(data => {
    if (data.records[0]) {
      return data.records[0];
    } else {
      throw new Error(`Could not find entityId ${entityId}`);
    }
  });
}

export function retrieveIdp(currentIdpId, currentIdpInstitutionId, statsToken) {
  return fetchJson("/entity/idp.json?" + qs.stringify({
    "entityid": currentIdpId,
    "institution": currentIdpInstitutionId,
    "state": "PA",
    "access_token": statsToken
  })).then(data => {
    if (data.records[0]) {
      return data.records[0];
    } else {
      throw new Error(`Could not find ${currentIdpId}`);
    }
  });
}

export function retrieveSps(idpId, statsToken) {
  return fetchJson(`/active/idp/${idpId}?` + qs.stringify({ access_token: statsToken })).then(data => {
    return data.entities.filter(function (sp) {
      return sp.name;
    }).sort(function (a, b) {
      return a.name.localeCompare(b.name);
    });
  });
}
