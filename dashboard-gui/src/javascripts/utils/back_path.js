let backPath = null

export function setBackPath(path) {
  backPath = path
}

export function getBackPath() {
  return backPath || '/apps/connected'
}
