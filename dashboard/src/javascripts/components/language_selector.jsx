import React from "react";
import I18n from "i18n-js";
import stopEvent from "../utils/stop";

class LanguageSelector extends React.Component {
    render() {
        return (
            <ul className="language">
                {[
                    this.renderLocaleChooser("en"),
                    this.renderLocaleChooser("nl")
                ]}
            </ul>
        );
    }

    renderLocaleChooser(locale) {
        return (
            <li key={locale}>
                <a
                    href="/locale"
                    className={I18n.currentLocale() === locale ? "selected" : ""}
                    title={I18n.t("select_locale", {locale: locale})}
                    onClick={this.handleChooseLocale(locale)}>
                    {I18n.t("code", {locale: locale})}
                </a>
            </li>
        );
    }

    handleChooseLocale(locale) {
        return function (e) {
            stopEvent(e);
            if (I18n.currentLocale() !== locale) {
                window.location.search = "lang=" + locale;
            }
        };
    }
}

export default LanguageSelector;
