import I18n from "i18n-js";

const converters = {
    spName: value => value !== undefined && value !== null ? value.toLowerCase() : "",
    name: value => value !== undefined && value !== null ? value.toLowerCase() : "",
    license: (value, app) => app.licenseStatus,
    jiraKey: value => {
        if (value !== undefined && value != null && value.indexOf("-") > -1) {
            return parseInt(value.substring(value.indexOf("-") + 1), 10);
        }
        return value;
    },
    requestDate: value => new Date(value),
    type: value => I18n.t(`history.action_types_name.${value}`),
    status: value => I18n.t(`history.statuses.${value.toLowerCase()}`)
};

const converterForAttribute = attr => {
    return converters[attr];
};

const compare = function (a, b) {
    if (a < b) {
        return -1;
    } else if (a > b) {
        return 1;
    }
    return 0;
};

export default function sort(list, sortAttribute, sortAscending) {
    return list.sort((a, b) => {
        let aAttr = a[sortAttribute];
        let bAttr = b[sortAttribute];

        const converter = converterForAttribute(sortAttribute);

        if (converter) {
            aAttr = converter(aAttr, a);
            bAttr = converter(bAttr, b);
        }

        const result = compare(aAttr, bAttr);

        if (sortAscending) {
            return result * -1;
        }
        return result;
    });
}
