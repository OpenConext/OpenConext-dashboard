import React from "react";
import PropTypes from "prop-types";
import moment from "moment";
import I18n from "i18n-js";
import DatePicker from "react-datepicker";
import {
    defaultScales,
    defaultScalesForPeriod,
    defaultScalesSpSelected,
    getDateTimeFormat,
    getPeriod
} from "../utils/time";
import SelectWrapper from "../components/select_wrapper";
import {loginAggregated, loginTimeFrame, statsServiceProviders, uniqueLoginCount} from "../api";
import "react-datepicker/dist/react-datepicker.css";
import CheckBox from "../components/checkbox";
import Chart from "../components/chart";
import stopEvent from "../utils/stop";
import {isEmpty} from "../utils/utils";

const states = ["all", "prodaccepted", "testaccepted"];
const minDiffByScale = {day: 1, week: 7, month: 31, quarter: 90, year: 365}


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
        Promise.all([loginTimeFrame(from.unix(), to.unix(), scale, undefined, state), statsServiceProviders()])
            .then(res => {
                let data = res[0].filter(p => p.count_user_id > 0);
                data = data.slice(1, data.length - 1);
                this.setState({
                    data: data,
                    loaded: true,
                    allSp: [this.allServiceProviderOption].concat(res[1]),
                    serviceProvidersDict: res[1].reduce((acc, p) => {
                        acc[p.value] = p.display;
                        return acc;
                    }, {})
                })
            });
    }

    refresh = () => {
        const {from, to, scale, sp, displayDetailPerSP, state} = this.state;
        const spEntityId = sp === this.allServiceProviderOption.value ? undefined : sp;
        if (displayDetailPerSP) {
            loginAggregated(getPeriod(from, scale), state, spEntityId).then(res => {
                if (isEmpty(res)) {
                    this.setState({data: res});
                } else if (res.length === 1 && res[0] === "no_results") {
                    this.setState({data: res});
                } else {
                    const sorted = res.filter(p => p.count_user_id).sort((a, b) => b.count_user_id - a.count_user_id);
                    const uniqueOnes = res.filter(p => p.distinct_count_user_id).reduce((acc, p) => {
                        const key = p.sp_entity_id;
                        acc[key] = p.distinct_count_user_id;
                        return acc;
                    }, {});
                    const data = sorted.map(p => {
                        const key = p.sp_entity_id;
                        p.distinct_count_user_id = uniqueOnes[key] || 0;
                        return p;
                    });
                    const linkedSpEntityIds = Object.keys(this.state.serviceProvidersDict);
                    const emptyOnes = linkedSpEntityIds
                        .filter(entityId => isEmpty(uniqueOnes[entityId]))
                        .map(entityId => ({
                            count_user_id: 0,
                            distinct_count_user_id: 0,
                            sp_entity_id: entityId,
                            time: from.format()
                        }));
                    const chartData = data.concat(emptyOnes);
                    this.setState({data: chartData});
                }
            });
        } else if (scale === "all") {
            uniqueLoginCount(from.unix(), to.unix(), spEntityId, state).then(res => this.setState({data: res}));
        } else {
            loginTimeFrame(from.unix(), to.unix(), scale, spEntityId, state).then(res => {
                if (scale === "minute" || scale === "hour") {
                    res = res.filter(p => p.count_user_id > 0);
                    res = res.slice(1, res.length - 1);
                }
                this.setState({data: res})
            });
        }
    };

    onChangeFrom = val => {
        const {scale, to} = this.state;
        const additionalState = this.invariantFromToScale(val, to, scale, "to");
        this.setState({data: [], from: val, ...additionalState}, this.refresh)
    };

    onChangeTo = val => {
        const {scale, from} = this.state;
        const additionalState = this.invariantFromToScale(from, val, scale, "from");
        const tomorrowMidnight = moment().add(1, "day").startOf("day");
        const maximumTo = tomorrowMidnight.isBefore(val);
        this.setState({data: [], maximumTo: maximumTo, to: val, ...additionalState}, this.refresh)
    };

    onChangeScale = scale => {
        const {from, to} = this.state;
        const additionalState = this.invariantFromToScale(from, to, scale);
        const diff = moment.duration(to.diff(from)).asDays();
        const minDiff = minDiffByScale[scale];
        if (minDiff > diff) {
            additionalState.from = moment(to).subtract(minDiff, "day");
        }
        this.setState({data: [], scale: scale, ...additionalState}, this.refresh);
    };

    invariantFromToScale = (from, to, scale, dateToChange = "from") => {
        let additionalState = {};
        const diff = moment.duration(to.diff(from)).asDays();
        if ((scale === "minute" && diff > 1) || (scale === "hour" && diff > 7)) {
            const duration = scale === "minute" ? 1 : 7;
            if (dateToChange === "to") {
                additionalState["to"] = moment(from).add(duration, "day");
            } else {
                additionalState["from"] = moment(to).subtract(duration, "day");
            }
        }
        return additionalState;
    };

    goLeft = e => {
        stopEvent(e);
        const scale = this.state.scale === "minute" || this.state.scale === "hour" ? "day" : this.state.scale;
        const from = moment(this.state.from).add(-1, scale);
        const to = moment(this.state.to).add(-1, scale);
        this.setState({data: [], from: from, to: to, maximumTo: false}, this.refresh)
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
            data: [],
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
            additionalState = this.invariantFromToScale(from, to, "minute");
            additionalState.scale = "minute";
        }
        this.setState({
            data: [],
            sp: val ? val : this.allServiceProviderOption.value, ...additionalState
        }, this.refresh);
    };

    onChangeDisplayDetailPerSP = e => {
        const displayDetailPerSP = e.target.checked;
        const {scale} = this.state;
        let additionalState = {};
        if (["minute", "hour", "all"].includes(scale)) {
            additionalState.scale = "year";
        }
        this.setState({data: [], displayDetailPerSP: displayDetailPerSP, ...additionalState}, this.refresh);
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
                      onChange={this.onChangeDisplayDetailPerSP}
                      info={I18n.t("stats.displayDetailPerSP")}
            />
            <h2 className="title secondary">{I18n.t("stats.state")}</h2>
            <SelectWrapper
                defaultValue={state}
                options={states.map(s => ({value: s, display: I18n.t(`my_idp.${s}`)}))}
                multiple={false}
                isClearable={false}
                handleChange={val => this.setState({data: [], state: val}, this.refresh)}/>
        </fieldset>;

    renderPeriod = (scale, from, to, toEnabled, spSelected) =>
        <fieldset>
            <h2 className="title">{I18n.t("stats.timeScale")}</h2>
            <SelectWrapper
                defaultValue={scale}
                options={(toEnabled ? defaultScalesForPeriod : spSelected ? defaultScalesSpSelected : defaultScales)
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
                scale: I18n.t(`stats.scale.${scale}`, {scale: scale}).toLowerCase()
            });
        }
        if (displayDetailPerSP) {
            return I18n.t("live.aggregatedChartTitlePeriod", {
                period: getPeriod(from, scale),
                group: I18n.t("chart.sp")
            });
        }
    };

    render() {
        const {from, to, scale, allSp, data, displayDetailPerSP, loaded, sp, state, maximumTo, serviceProvidersDict} = this.state;
        const spSelected = sp !== this.allServiceProviderOption.value;
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
                        {this.renderPeriod(scale, from, to, !displayDetailPerSP, spSelected)}
                    </div>
                </div>
                <div className="l-right-small">
                    <div className="mod-chart">
                        {results && <Chart data={data}
                                           scale={scale}
                                           includeUniques={scale !== "minutes" && scale !== "hour"}
                                           title={this.title(from, to, displayDetailPerSP, sp, scale)}
                                           aggregate={displayDetailPerSP}
                                           groupedBySp={displayDetailPerSP}
                                           groupedByIdp={false}
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
