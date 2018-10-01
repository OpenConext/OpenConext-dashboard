import React from "react";
import PropTypes from "prop-types";
import moment from "moment";
import I18n from "i18n-js";
import DatePicker from "react-datepicker";
import {defaultScales, getDateTimeFormat, getPeriod} from "../utils/time";
import SelectWrapper from "../components/select_wrapper";
import {loginAggregated, loginTimeFrame, statsServiceProviders, uniqueLoginCount} from "../api";
import "react-datepicker/dist/react-datepicker.css";
import CheckBox from "../components/checkbox";
import Chart from "../components/chart";
import stopEvent from "../utils/stop";

const states = ["all", "prodaccepted", "testaccepted"];

class Stats extends React.Component {

    constructor() {
        super();
        this.allServiceProviderOption = {display: I18n.t("stats.filters.allServiceProviders"), value: "all"};
        this.state = {
            from: moment().subtract(1, "day"),
            to: moment(),
            scale: "minute",
            loaded: false,
            data: [],
            sp: this.allServiceProviderOption.value,
            allSp: [],
            serviceProvidersDict: {},
            displayDetailPerSP: false,
            state: states[1],
            maximumTo: false
        };
    }

    componentWillMount() {
        const {from, to, scale, state} = this.state;
        loginTimeFrame(from.unix(), to.unix(), scale, undefined, state).then(res => {
            this.setState({data: res, loaded: true});
            statsServiceProviders().then(res => this.setState({
                allSp: [this.allServiceProviderOption].concat(res),
                serviceProvidersDict: res.reduce((acc, p) => {
                    acc[p.value] = p.display;
                    return acc;
                }, {})
            }))
        });
    }

    refresh = () => {
        const {from, to, scale, sp, displayDetailPerSP, state} = this.state;
        if (displayDetailPerSP) {
            loginAggregated(getPeriod(from.unix(), scale), state, sp === this.allServiceProviderOption.value ? undefined : sp).then(res => this.setState({data: res}));
        } else if (scale === "all") {
            uniqueLoginCount(from.unix(), to.unix(), sp, state).then(res => this.setState({data: res}));
        } else {
            loginTimeFrame(from.unix(), to.unix(), sp, state).then(res => this.setState({data: res}));
        }
    };

    onChangeFrom = val => {
        const {scale, to} = this.state;
        const additionalState = this.invariantFromToScale(val, to, scale);
        this.setState({data: [], from: val, ...additionalState}, this.refresh)
    };

    onChangeTo = val => {
        const {scale, from} = this.state;
        const additionalState = this.invariantFromToScale(from, val, scale, -1);
        const tomorrowMidnight = moment().add(1, "day").startOf("day");
        const maximumTo = tomorrowMidnight.isBefore(val);
        this.setState({data: [], maximumTo: maximumTo, to: val, ...additionalState}, this.refresh)
    };

    onChangeScale = scale => {
        const {from, to} = this.state;
        const additionalState = this.invariantFromToScale(from, to, scale);
        const state = {data: [], scale: scale, ...additionalState};
        this.setState(state, this.refresh);
    };

    invariantFromToScale = (from, to, scale, multiplier = 1) => {
        let additionalState = {};
        const diff = moment.duration(to.diff(from)).asDays();
        if (scale === "minute" && diff > 1) {
            additionalState[multiplier === 1 ? "from" : "to"] = moment(from).add(multiplier, "day");
        } else if (scale === "hour" && diff > 7) {
            additionalState[multiplier === 1 ? "from" : "to"] = moment(from).add(multiplier * 7, "day");
        }
        return additionalState;
    };

    goLeft = e => {
        stopEvent(e);
        const scale = this.state.scale === "minute" || this.state.scale === "hour" ? "day" : this.state.scale;
        const from = moment(this.state.from).add(-1, scale);
        const to = moment(this.state.to).add(-1, scale);
        this.setState({from: from, to: to, maximumTo: false}, this.refresh)
    };

    goRight = e => {
        stopEvent(e);
        if (this.state.maximumTo) {
            return;
        }
        const scale = this.state.scale === "minute" || this.state.scale === "hour" ? "day" : this.state.scale;
        const from = moment(this.state.from).add(1, scale);
        const to = moment(this.state.to).add(1, scale);
        const tomorrowMidnight = moment().add(1, "day").startOf("day");
        const maximumTo = tomorrowMidnight.isBefore(to);
        this.setState({
            from: from,
            to: maximumTo ? tomorrowMidnight : to,
            maximumTo: maximumTo
        }, this.refresh);
    };


    onChangeServiceProvider = val => {
        let additionalState = {};
        const {scale} = this.state;
        if (!val && scale === "all") {
            const {from, to} = this.state;
            additionalState = this.invariantFromToScale(from, to, "minute")
            additionalState.scale = "minute";
        }
        this.setState({sp: val ? val : this.allServiceProviderOption.value, ...additionalState});
    };

    renderSpSelect = (sp, allSp, clearable, displayDetailPerSP, state) =>
        <fieldset>
            <h2 className="title">{I18n.t("stats.sp")}</h2>
            <SelectWrapper
                defaultValue={sp}
                options={allSp}
                multiple={false}
                isClearable={clearable}
                handleChange={this.onChangeServiceProvider}/>
            <CheckBox name="display" value={displayDetailPerSP}
                      onChange={e => this.setState({displayDetailPerSP: !this.state.displayDetailPerSP})}
                      info={I18n.t("stats.displayDetailPerSP")}
            />
            <h2 className="title secondary">{I18n.t("stats.state")}</h2>
            <SelectWrapper
                defaultValue={state}
                options={states.map(s => ({value: s, display: I18n.t(`my_idp.${s}`)}))}
                multiple={false}
                isClearable={false}
                handleChange={val => this.setState({state: val})}/>
        </fieldset>;

    renderPeriod = (scale, from, to, toEnabled, spSelected) =>
        <fieldset>
            <h2 className="title">{I18n.t("stats.timeScale")}</h2>
            <SelectWrapper
                defaultValue={scale}
                options={(spSelected ? defaultScales : defaultScales.filter(s => s !== "all"))
                    .map(scale => ({display: I18n.t(`stats.scale.${scale}`), value: scale}))}
                multiple={false}
                handleChange={this.onChangeScale}/>
            <h2 className="title secondary">{toEnabled ? I18n.t("stats.from") : I18n.t("stats.date")}</h2>
            <DatePicker
                selected={from}
                preventOpenOnFocus
                onChange={this.onChangeFrom}
                showYearDropdown
                showMonthDropdown
                showWeekNumbers
                todayButton={I18n.t("stats.today")}
                maxDate={moment(to).subtract(1, "day")}
                disabled={false}
                dateFormat={getDateTimeFormat(scale, !toEnabled)}
            />
            {toEnabled && <div><h2 className="title secondary">{I18n.t("stats.to")}</h2>
                <DatePicker
                    selected={to}
                    preventOpenOnFocus
                    onChange={this.onChangeTo}
                    showYearDropdown
                    showMonthDropdown
                    showWeekNumbers
                    todayButton={I18n.t("stats.today")}
                    maxDate={moment()}
                    dateFormat={getDateTimeFormat(scale, !toEnabled)}
                /></div>}
        </fieldset>;

    title = (from, to, displayDetailPerSP, sp, scale) => {
        const format = scale === "minute" || scale === "hour" ? "L" : "L";//'MMMM Do YYYY, h:mm:ss a'
        if (scale === "all") {
            return I18n.t("live.noTimeFrameChart", {
                from: from ? from.format(format) : "",
                to: to ? to.format(format) : "",
                scale: I18n.t(`stats.scale.${scale}`).toLowerCase()
            });
        }
        if (!displayDetailPerSP) {
            return I18n.t("live.chartTitle", {
                from: from ? from.format(format) : "",
                to: to ? to.format(format) : "",
                scale: I18n.t(`stats.scale.${scale}`).toLowerCase()
            });
        }
        if (displayDetailPerSP) {
            return I18n.t("live.aggregatedChartTitlePeriod", {
                period: getPeriod(from, scale),
                group: I18n.t("providers.idp")
            });
        }
    };

    render() {
        const {from, to, scale, allSp, data, displayDetailPerSP, loaded, sp, state, maximumTo, serviceProvidersDict} = this.state;
        const spSelected = sp !== this.allServiceProviderOption.value;
        const toEnabled = !displayDetailPerSP;
        const noResult = data.length === 1 && data[0] === "no_results";
        const results = loaded && data.length > 0 && !noResult;
        const idp = this.context.currentUser.currentIdp;
        const identityProvidersDict = {};
        identityProvidersDict[idp.id] = I18n.locale === "en" ? idp.displayNames["en"] : idp.displayNames["nl"];
        return (
            <div className="l-main stats">
                <div className="l-left-large">
                    <div className="mod-filters">
                        <div className="header">
                            <h1>{I18n.t("stats.filters.name")}</h1>
                        </div>
                        {this.renderSpSelect(sp, allSp, spSelected, displayDetailPerSP, state)}
                        {this.renderPeriod(scale, from, to, toEnabled, spSelected)}
                    </div>
                </div>
                <div className="l-right-small">
                    <div className="mod-chart">
                        {results && <Chart data={data}
                                           scale={scale}
                                           includeUniques={scale !== "minutes" && scale !== "hour"}
                                           title={this.title(from, to, displayDetailPerSP, sp, scale)}
                                           groupedBySp={displayDetailPerSP}
                                           aggregate={displayDetailPerSP}
                                           serviceProvidersDict={serviceProvidersDict}
                                           identityProvidersDict={identityProvidersDict}
                                           goRight={this.goRight}
                                           goLeft={this.goLeft}
                                           rightDisabled={maximumTo}
                                           noTimeFrame={scale === "all"}/>}
                        {!loaded && <div>
                            <section className="loading">
                                <em>{I18n.t("chart.loading")}</em>
                                <i className="fa fa-refresh fa-spin fa-2x fa-fw"></i>
                            </section>
                        </div>}
                        {noResult && <div>
                            <section className="loading">
                                <em>{I18n.t("chart.noResults")}</em>
                            </section>
                        </div>}
                    </div>
                </div>
            </div>
        );
    }
}

Stats.contextTypes = {
    currentUser: PropTypes.object
};

export default Stats;
