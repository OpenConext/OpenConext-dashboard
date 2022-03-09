import stopEvent from "./stop";

export function isEmpty(obj) {
    if (obj === undefined || obj === null) {
        return true
    }
    if (Array.isArray(obj)) {
        return obj.length === 0
    }
    if (typeof obj === 'string') {
        return obj.trim().length === 0
    }
    if (typeof obj === 'object') {
        return Object.keys(obj).length === 0
    }
    return false
}

export const consentTypes = ['DEFAULT_CONSENT', 'MINIMAL_CONSENT', 'NO_CONSENT']

export function login(e, loaLevel=null, location=window.location.href) {
    stopEvent(e);
    const loaLevelQueryPart = loaLevel ? `&loa=${loaLevel}` : ""
    window.location.href = `/login?redirect_url=${encodeURIComponent(location)}${loaLevelQueryPart}`
}