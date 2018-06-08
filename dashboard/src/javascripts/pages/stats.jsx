import React from "react";
import moment from "moment";
import I18n from "i18n-js";

import {apiUrl, retrieveIdp, retrieveSps} from "../api/stats";
import {getPeriod} from "../utils/period";

import DownloadButton from "../components/download_button";
import SelectWrapper from "../components/select_wrapper";
import Period from "../components/period";
import Chart from "../components/chart";

class Stats extends React.Component {

    constructor() {
        super();
        this.state = {
            chart: {
                type: "idpspbar",
                periodFrom: moment().subtract(1, "months"),
                periodTo: moment().subtract(1, "days"),
                periodType: "m",
                periodDate: moment().subtract(1, "days"),
                error: false
            }
        };
    }

    componentWillMount() {
        const {currentUser} = this.context;
        retrieveIdp(currentUser.getCurrentIdp().id, currentUser.getCurrentIdp().institutionId, currentUser.statsToken).then(idp => {
            this.setState({chart: {...this.state.chart, idp: idp.id}});

            retrieveSps(idp.id, currentUser.statsToken).then(sps => {
                const newSps = sps.map(sp => {
                    return {display: sp.name, value: sp.id};
                });

                this.setState({sps: newSps});
            });
        })
            .catch(() => this.setState({chart: {...this.state.chart, error: true}}));
    }

    downloadIdpSpBarCsvFile() {
        const {chart} = this.state;
        const {currentUser} = this.context;
        const url = apiUrl(`/splogins/${chart.idp}.csv`);
        const period = getPeriod(chart.periodDate, chart.periodType);

        window.open(`${url}?access_token=${currentUser.statsToken}&period=${period}`);
    }

    renderDownload() {
        return (
            <DownloadButton
                genFile={() => this.downloadIdpSpBarCsvFile()}
                title={I18n.t("application_usage_panel.download")}
                fileName="splogins.csv"
                mimeType="text/csv"
                className="download-button c-button"
            />
        );
    }

    renderChartTypeSelect() {
        const options = [
            {display: I18n.t("stats.chart.type.idpspbar"), value: "idpspbar"},
            {display: I18n.t("stats.chart.type.idpsp"), value: "idpsp"}
        ];

        const handleChange = value => {
            const newState = {chart: {...this.state.chart, type: value}};
            if (value === "idpsp" && !this.state.chart.sp && this.state.sps && this.state.sps.length > 0) {
                newState.chart.sp = this.state.sps[0].value;
            }
            this.setState(newState);
        };

        return (
            <div>
                <fieldset>
                    <h2>{I18n.t("stats.chart.type.name")}</h2>
                    <SelectWrapper
                        defaultValue={this.state.chart.type}
                        options={options}
                        multiple={false}
                        handleChange={handleChange.bind(this)}/>
                </fieldset>
                {this.renderSpSelect()}
            </div>
        );
    }

    renderSpSelect() {
        if (this.state.chart.type !== "idpsp") {
            return null;
        }

        const handleChange = sp => {
            this.setState({chart: {...this.state.chart, sp: sp}});
        };

        return (
            <fieldset>
                <h2>{I18n.t("stats.chart.sp.name")}</h2>
                <SelectWrapper
                    defaultValue={this.state.chart.sp}
                    options={this.state.sps}
                    multiple={false}
                    handleChange={handleChange.bind(this)}/>
            </fieldset>
        );
    }

    renderPeriodSelect() {
        if (this.state.chart.type === "idpsp") {
            const handleChangePeriodFrom = moment => {
                this.setState({chart: {...this.state.chart, periodFrom: moment}});
            };

            const handleChangePeriodTo = moment => {
                this.setState({chart: {...this.state.chart, periodTo: moment}});
            };

            return (
                <div>
                    <Period
                        initialDate={this.state.chart.periodFrom}
                        title={I18n.t("stats.chart.periodFrom.name")}
                        handleChange={handleChangePeriodFrom.bind(this)}
                        max={this.state.chart.periodTo}/>
                    <Period
                        initialDate={this.state.chart.periodTo}
                        title={I18n.t("stats.chart.periodTo.name")}
                        handleChange={handleChangePeriodTo.bind(this)}
                        min={this.state.chart.periodFrom}/>
                </div>
            );
        }

        const handleChange = moment => {
            this.setState({chart: {...this.state.chart, periodDate: moment}});
        };

        return (
            <div>
                {this.renderPeriodTypeSelect()}
                <Period
                    period={this.state.chart.periodType}
                    initialDate={this.state.chart.periodDate}
                    title={I18n.t("stats.chart.periodDate.name")}
                    handleChange={handleChange.bind(this)}/>
            </div>
        );
    }

    renderPeriodTypeSelect() {
        const options = [
            {display: I18n.t("stats.chart.period.day"), value: "d"},
            {display: I18n.t("stats.chart.period.week"), value: "w"},
            {display: I18n.t("stats.chart.period.month"), value: "m"},
            {display: I18n.t("stats.chart.period.quarter"), value: "q"},
            {display: I18n.t("stats.chart.period.year"), value: "y"}
        ];

        const handleChange = function (value) {
            this.setState({chart: {...this.state.chart, periodType: value}});
        };

        return (
            <fieldset>
                <h2>{I18n.t("stats.chart.period.name")}</h2>
                <SelectWrapper
                    defaultValue={this.state.chart.periodType}
                    options={options}
                    handleChange={handleChange.bind(this)}/>
            </fieldset>
        );
    }

    render() {
        return (
            <div className="l-main stats">
                <div className="l-left">
                    <div className="mod-filters">
                        <div className="header">
                            <h1>{I18n.t("stats.filters.name")}</h1>
                            {this.renderDownload()}
                        </div>
                        <form>
                            {this.renderChartTypeSelect()}
                            {this.renderPeriodSelect()}
                        </form>
                    </div>
                </div>
                <div className="l-right">
                    <div className="mod-chart">
                        <Chart chart={this.state.chart}/>
                    </div>
                </div>
            </div>
        );
    }
}

Stats.contextTypes = {
    currentUser: React.PropTypes.object
};

export default Stats;
