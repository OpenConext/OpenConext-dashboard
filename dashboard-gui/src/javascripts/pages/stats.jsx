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
import {getConnectedServiceProviders, loginAggregated, loginTimeFrame, uniqueLoginCount} from "../api";
import "react-datepicker/dist/react-datepicker.css";
import CheckBox from "../components/checkbox";
import Chart from "../components/chart";
import stopEvent from "../utils/stop";
import {isEmpty} from "../utils/utils";

const minDiffByScale = {day: 30, week: 365, month: 365, quarter: 365, year: 365 * 5};


class Stats extends React.Component {

    constructor(props, context) {
        super(props, context);
        this.allServiceProviderOption = {display: I18n.t("stats.filters.allServiceProviders"), value: "all"};
        this.state = this.getInitialStateValues();
    }

    getInitialStateValues() {
        return {
            from: moment().startOf("year"),
            to: moment().endOf("day"),
            scale: "day",
            loaded: false,
            data: [],
            sp: this.props.sp || this.allServiceProviderOption.value,
            allSp: [],
            serviceProvidersDict: {},
            displayDetailPerSP: false,
            maximumTo: true,
            connectedServiceProviders: []
        };
    }

    reset = e => {
        stopEvent(e);
        const state = this.getInitialStateValues();
        ["allSp", "serviceProvidersDict", "connectedServiceProviders"].forEach(d => delete state[d]);
        this.setState(state, this.refresh);
    };

    componentWillMount() {
        const {from, to, scale, sp} = this.state;
        Promise.all([loginTimeFrame(from.unix(), to.unix(), scale,
            sp === this.allServiceProviderOption.value ? undefined : sp), getConnectedServiceProviders()])
            .then(res => {
                const now = moment().unix() * 1000;
                let data = res[0].filter(p => p.time <= now);
                data = data.slice(1, data.length - 1);
                const options = res[1].payload.map(sp => ({
                    value: sp.spEntityId,
                    display: sp.names["en"]
                }));
                this.setState({
                    data: data,
                    loaded: true,
                    allSp: [this.allServiceProviderOption].concat(options),
                    serviceProvidersDict: options.reduce((acc, p) => {
                        acc[p.value] = p.display;
                        return acc;
                    }, {}),
                    connectedServiceProviders: options
                })
            });
    }

    refresh = () => {
        this.setState({loaded: false});
        const {from, to, scale, sp, displayDetailPerSP} = this.state;
        const spEntityId = sp === this.allServiceProviderOption.value ? undefined : sp;
        if (displayDetailPerSP) {
            loginAggregated(getPeriod(from, scale), spEntityId).then(res => {
                if (isEmpty(res)) {
                    this.setState({data: res, loaded: true});
                } else if (res.length === 1 && res[0] === "no_results") {
                    this.setState({data: res, loaded: true});
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
                    if (!spEntityId) {
                        const fromFormatted = from.format();
                        const emptyOnes = this.state.connectedServiceProviders
                            .filter(sp => isEmpty(uniqueOnes[sp.value]))
                            .map(sp => ({
                                count_user_id: 0,
                                distinct_count_user_id: 0,
                                sp_entity_id: sp.value,
                                time: fromFormatted
                            }));
                        const chartData = data.concat(emptyOnes);
                        this.setState({data: chartData, loaded: true});
                    } else {
                        this.setState({data: data, loaded: true});
                    }
                }
            });
        } else if (scale === "all") {
            uniqueLoginCount(from.unix(), to.unix(), spEntityId).then(res => this.setState({
                data: res,
                loaded: true
            }));
        } else {
            loginTimeFrame(from.unix(), to.unix(), scale, spEntityId).then(res => {
                if (scale === "minute" || scale === "hour") {
                    const now = moment().unix() * 1000;
                    res = res.filter(p => p.time <= now);
                    res = res.slice(1, res.length - 1);
                }
                this.setState({data: res, loaded: true})
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
        const {from, to, displayDetailPerSP} = this.state;
        if (displayDetailPerSP) {
            this.setState({data: [], scale: scale, from: moment().subtract(1, "day").startOf("day")}, this.refresh);
        } else {
            const additionalState = this.invariantFromToScale(from, to, scale);
            const diff = moment.duration(to.diff(from)).asDays();
            const minDiff = minDiffByScale[scale];
            if (minDiff > diff) {
                additionalState.from = moment(to).subtract(minDiff, "day").startOf(scale);
            }
            this.setState({data: [], scale: scale, ...additionalState}, this.refresh);
        }
    };

    invariantFromToScale = (from, to, scale, dateToChange = "from") => {
        let additionalState = {};
        if (this.state.displayDetailPerSP) {
            return additionalState;
        }
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
        const scales = this.getScalesForPeriod(!displayDetailPerSP, this.state.sp !== 'all')

        this.setState({
          data: [],
          from: moment().subtract(1, "day"),
          to: moment(),
          scale: scales.includes(this.state.scale) ? this.state.scale : 'year',
          displayDetailPerSP
        }, this.refresh);
    };

    renderSpSelect = (sp, allSp, clearable, displayDetailPerSP) =>
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
        </fieldset>;

    renderYearPicker = (date, maxYear, onChange) => {
        const currentYear = date.format("YYYY");
        return <SelectWrapper
            defaultValue={currentYear}
            options={Array.from(new Array((1 + maxYear) - 2011), (x, i) => (i + 2011).toString(10))
                .map(m => ({display: m, value: m}))}
            multiple={false}
            handleChange={opt => onChange(moment(date).year(parseInt(opt, 10)))}/>
    };

    renderDatePicker = (scale, date, onChange, maxDate, dateFormat, name, showToday = true) => {
        const dayPicker = ["all", "minute", "hour", "day", "week"].includes(scale);
        const monthPicker = scale === "month";
        const quarterPicker = scale === "quarter";
        if (dayPicker) {
            return <DatePicker
                ref={name}
                selected={date}
                preventOpenOnFocus
                onChange={onChange}
                showYearDropdown
                showMonthDropdown
                showWeekNumbers
                onWeekSelect={m => {
                    onChange(moment(date).week(m.week()));
                    const datepicker = this.refs[name];
                    datepicker.setOpen(false);
                }}
                weekLabel="Week"
                todayButton={showToday ? I18n.t("stats.today") : undefined}
                maxDate={maxDate}
                disabled={false}
                dateFormat={dateFormat}
            />
        }
        if (monthPicker) {
            return <div className="group-dates">
                <SelectWrapper
                    defaultValue={date.format("MMMM")}
                    options={moment.months().map(m => ({display: m, value: m}))}
                    multiple={false}
                    handleChange={opt => onChange(moment(date).month(opt))}/>
                {this.renderYearPicker(date, maxDate.year(), onChange)}
            </div>
        }
        if (quarterPicker) {
            return <div className="group-dates">
                <SelectWrapper
                    defaultValue={date.format("[Q]Q")}
                    options={Array.from(new Array(4), (x, i) => "Q" + (i + 1).toString(10))
                        .map(m => ({display: m, value: m}))}
                    multiple={false}
                    handleChange={opt => onChange(moment(date).quarter(parseInt(opt.substring(1), 10)))}/>
                {this.renderYearPicker(date, maxDate.year(), onChange)}
            </div>
        }
        return this.renderYearPicker(date, maxDate.year(), onChange);
    };

    getScalesForPeriod(toEnabled, spSelected) {
      return !toEnabled ? defaultScalesForPeriod : spSelected ? defaultScalesSpSelected : defaultScales;
    }

    renderPeriod = (scale, from, to, toEnabled, spSelected, className = "") => {
        return <fieldset className={className}>
            <h2 className="title">{I18n.t("stats.timeScale")}</h2>
            <SelectWrapper
                defaultValue={scale}
                options={this.getScalesForPeriod(toEnabled, spSelected).map(scale => ({display: I18n.t(`stats.scale.${scale}`), value: scale}))}
                multiple={false}
                handleChange={this.onChangeScale}/>
            <h2 className="title secondary">{toEnabled ? I18n.t("stats.from") : I18n.t("stats.date")}</h2>
            {this.renderDatePicker(scale, from, this.onChangeFrom, moment(to).subtract(1, "day"), getDateTimeFormat(scale, !toEnabled), "from-date", false)}
            {toEnabled && <div><h2 className="title secondary">{I18n.t("stats.to")}</h2>
                {this.renderDatePicker(scale, to, this.onChangeTo, moment(), getDateTimeFormat(scale, !toEnabled), "to-date")}
            </div>}
        </fieldset>
    };

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
        const {from, to, scale, allSp, data, displayDetailPerSP, loaded, sp, maximumTo, serviceProvidersDict} = this.state;
        const fullView = this.props.view === "full";
        const spSelected = sp !== this.allServiceProviderOption.value;
        const noResult = (data.length === 1 && data[0] === "no_results") || (loaded && data.length === 0);
        const results = loaded && data.length > 0 && !noResult;
        const idp = this.context.currentUser.currentIdp;
        const identityProvidersDict = {};
        identityProvidersDict[idp.id] = I18n.locale === "en" ? idp.displayNames["en"] : idp.displayNames["nl"];
        const classNameView = fullView ? "l-right-small" : "minimal";
        return (
            <div className="l-main stats">
                {fullView && <div className="l-left-large">
                    <div className="mod-filters">
                        <div className="header">
                            <h1>{I18n.t("stats.filters.name")}</h1>
                            <a href={I18n.t("stats.helpLink")} target="_blank" rel="noopener noreferrer" className="help"><i
                                className="fa fa-info-circle"></i></a>
                            <a href="reset" className="reset c-button" onClick={this.reset}>{I18n.t("facets.reset")}</a>
                        </div>
                        {this.renderSpSelect(sp, allSp, spSelected, displayDetailPerSP)}
                        {this.renderPeriod(scale, from, to, !displayDetailPerSP, spSelected)}
                    </div>
                </div>}
                <div className={classNameView}>
                    <div className="mod-chart">
                        {!fullView && this.renderPeriod(scale, from, to, !displayDetailPerSP, spSelected, "horizontal")}
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
PropTypes.Stats = {
    view: PropTypes.string,
    sp: PropTypes.string,
};

export default Stats;
