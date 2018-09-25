import React from "react";
import ReactDOM from "react-dom";
import App from "./javascripts/App";
import I18n from "i18n-js";
import {browserSupported} from "./javascripts/lib/browser_supported";
import moment from "moment";

import {createCurrentUser} from "./javascripts/models/current_user";

import {getUserData} from "./javascripts/api";
import BrowserNotSupported from "./javascripts/pages/browser_not_supported";
import ServerError from "./javascripts/pages/server_error";


if (browserSupported()) {
    getUserData()
        .then(json => {
            if (json.noAccess === true) {
                ReactDOM.render(<ServerError/>, document.getElementById("app"));
                return;
            }
            I18n.locale = json.language;
            moment.locale(json.language);
            const currentUser = createCurrentUser(json.payload);
            const locationHash = window.location.hash.substr(1);
            currentUser.statsToken = locationHash.substr(locationHash.indexOf("access_token=")).split("&")[0].split("=")[1];

            if (!currentUser.statsToken && currentUser.statsEnabled) {
                window.location = currentUser.statsUrl + "&state=" + window.location;
            } else {
                const spinner = document.getElementById("service-loader-id");
                spinner.parentNode.removeChild(spinner);
                const info = document.getElementById("service-loader-info-id");
                info.parentNode.removeChild(info);
                ReactDOM.render(<App currentUser={currentUser}/>, document.getElementById("app"));
            }
        });
} else {
    ReactDOM.render(<BrowserNotSupported/>, document.getElementById("app"));
}
