export function providerName(provider, fallback) {
    return provider || fallback;
}

export function mergeList(arr, keyProperty) {
    const keyed = arr.reduce((acc, obj) => {
        const key = obj[keyProperty];
        if (!acc[key]) {
            acc[key] = {};
        }
        Object.keys(obj).forEach(k => acc[key][k] = obj[k] || acc[key][k]);
        return acc;
    }, {});
    return Object.values(keyed);

}

export function isEmpty(obj) {
    if (obj === undefined || obj === null) {
        return true;
    }
    if (Array.isArray(obj)) {
        return obj.length === 0;
    }
    if (typeof obj === "string") {
        return obj.trim().length === 0;
    }
    if (typeof obj === "object") {
        return Object.keys(obj).length === 0;
    }
    return false;
}
