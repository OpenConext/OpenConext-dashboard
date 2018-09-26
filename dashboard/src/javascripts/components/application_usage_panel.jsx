import React from "react";
import PropTypes from "prop-types";

import I18n from "i18n-js";
import moment from "moment";

import {AppShape} from "../shapes";

class ApplicationUsagePanel extends React.Component {
    constructor() {
        super();

        this.state = {
            chart: {
                type: "idpsp",
                periodFrom: moment().subtract(1, "months"),
                periodTo: moment(),
                periodType: "m",
                periodDate: moment(),
                error: false
            }
        };
    }

    render() {
        return (
            <div className="l-middle">
                <div className="mod-title">
                    <h1>{I18n.t("application_usage_panel.title")}</h1>
                </div>
                <div className="mod-usage">
                    <div className="header">
                        <div className="options">
                        </div>
                        <div style={{clear: "both"}}></div>
                    </div>

                    {/*<Chart chart={this.state.chart} />*/}
                </div>
            </div>
        );
    }
}

ApplicationUsagePanel.contextTypes = {
    currentUser: PropTypes.object
};

ApplicationUsagePanel.propTypes = {
    app: AppShape.isRequired
};

export default ApplicationUsagePanel;
