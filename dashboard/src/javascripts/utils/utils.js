import I18n from "i18n-js";

export function providerName(provider, fallback) {
    if (!provider) {
        return fallback;
    }
    const alt = I18n.locale === "en" ? "nl" : "en";
    return provider[`name_${I18n.locale}`] || provider[`name_${alt}`] || fallback;
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

