import I18n from "i18n-js";

export function getPeriod(m, scale) {
    switch (scale) {
        case "day":
            return `${m.year()}D${m.dayOfYear()}`;
        case "week":
            return `${m.year()}W${m.week()}`;
        case "month":
            return `${m.year()}M${m.month() + 1}`;
        case "quarter":
            return `${m.year()}Q${m.quarter()}`;
        case "year":
            return `${m.year()}`;
        default:
            return undefined;
    }
}

export function getDateTimeFormat(scale, periodEnabled = true) {
    if (!periodEnabled) {
        return "L";
    }
    switch (scale) {
        case "day":
            return "L";
        case "week":
            return `YYYY [week] ww`;
        case "month":
            return "YYYY MMMM";
        case "quarter":
            return `YYYY [Q]Q`;
        case "year":
            return `[${I18n.t("stats.period.year")}] YYYY`;
        default:
            return "L";
    }
}

export const defaultScales = ["all", "year", "quarter", "month", "week", "day", "hour", "minute"];
