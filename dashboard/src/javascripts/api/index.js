const apiPath = "/dashboard/api";

function url(path) {
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

  const fetchOptions = Object.assign({}, { headers }, options, {
    credentials: "same-origin"
  });

  return fetch(url(path), fetchOptions)
  .then(validateResponse);
}

function fetchJson(path) {
  return validFetch(path)
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
