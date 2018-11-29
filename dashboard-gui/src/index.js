import React from "react";
import ReactDOM from "react-dom";
import App from "./javascripts/App";
import I18n from "i18n-js";
import "babel-polyfill";
import {browserSupported} from "./javascripts/lib/browser_supported";
import {polyfills} from "./javascripts/lib/polyfills";
import moment from "moment";
import * as HighChart from "highcharts";
import * as HighStock from "highcharts/highstock"

import {createCurrentUser} from "./javascripts/models/current_user";

import {getUserData} from "./javascripts/api";
import BrowserNotSupported from "./javascripts/pages/browser_not_supported";
import ServerError from "./javascripts/pages/server_error";

polyfills();

if (browserSupported()) {
    getUserData()
        .then(json => {
            if (json.noAccess === true) {
                ReactDOM.render(<ServerError/>, document.getElementById("app"));
                return;
            }
            I18n.locale = json.language;
            moment.locale(json.language);

            HighChart.setOptions({
                lang: {
                    months: moment.months(),
                    weekdays: moment.weekdays(),
                    shortMonths: moment.monthsShort(),
                    downloadCSV: I18n.t("export.downloadCSV"),
                    downloadPNG: I18n.t("export.downloadPNG"),
                    downloadPDF: I18n.t("export.downloadPDF"),
                }
            });
            HighStock.setOptions({
                lang: {
                    months: moment.months(),
                    weekdays: moment.weekdays(),
                    shortMonths: moment.monthsShort(),
                    downloadCSV: I18n.t("export.downloadCSV"),
                    downloadPNG: I18n.t("export.downloadPNG"),
                    downloadPDF: I18n.t("export.downloadPDF"),
                }
            });
            const currentUser = createCurrentUser(json.payload);
            const spinner = document.getElementById("service-loader-id");
            spinner.parentNode.removeChild(spinner);
            const info = document.getElementById("service-loader-info-id");
            info.parentNode.removeChild(info);
            ReactDOM.render(<App currentUser={currentUser}/>, document.getElementById("app"));
        });
} else {
    ReactDOM.render(<BrowserNotSupported/>, document.getElementById("app"));
}
