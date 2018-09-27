import React from "react";
import PropTypes from "prop-types";
import moment from "moment";
import I18n from "i18n-js";
import DatePicker from "react-datepicker";
import {defaultScales, getDateTimeFormat, getPeriod} from "../utils/time";
import SelectWrapper from "../components/select_wrapper";
import {loginAggregated, statsServiceProviders} from "../api";
import "react-datepicker/dist/react-datepicker.css";
import CheckBox from "../components/checkbox";
import Chart from "../components/chart";
import {stop} from "../utils/utils";

const states = ["all", "prodaccepted", "testaccepted"];

class Stats extends React.Component {

    constructor() {
        super();
        this.allServiceProviderOption = {display: I18n.t("stats.filters.allServiceProviders"), value: "all"};
        this.state = {
            from: moment().subtract(1, "year"),
            to: moment(),
            scale: "year",
            loaded: false,
            data: [],
            sp: this.allServiceProviderOption.value,
            allSp: [],
            displayDetailPerSP: false,
            state: states[1],

        };
    }

    componentWillMount() {
        const {scale, state} = this.state;
        loginAggregated(getPeriod(moment(), scale), state).then(res => {
            this.setState({data: res, loaded: true});
            statsServiceProviders().then(res => this.setState({
                allSp: [this.allServiceProviderOption].concat(res)
            }))
        });
    }

    goLeft = e => {
        stop(e);
        const scale = this.state.scale === "minute" || this.state.scale === "hour" ? "day" : this.state.scale;
        const from = moment(this.state.from).add(-1, scale);
        const to = moment(this.state.to).add(-1, scale);
        this.setState({from: from, to: to, maximumTo: false}, this.componentDidMount)
    };

    goRight = e => {
        stop(e);
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
        }, this.componentDidMount);
    };

    renderPeriod = (scale, from, to, toEnabled, spSelected) =>
        <fieldset>
            <h2 className="title">{I18n.t("stats.timeScale")}</h2>
            <SelectWrapper
                defaultValue={scale}
                options={(spSelected ? defaultScales : defaultScales.filter(s => s !== "all"))
                    .map(scale => ({display: I18n.t(`stats.scale.${scale}`), value: scale}))}
                multiple={false}
                handleChange={val => this.setState({scale: val })}/>
            <h2 className="title secondary">{toEnabled ? I18n.t("stats.from"):I18n.t("stats.date")}</h2>
                <DatePicker
                    selected={from}
                    preventOpenOnFocus
                    onChange={m => this.setState({"from": m})}
                    showYearDropdown
                    showMonthDropdown
                    todayButton={I18n.t("stats.today")}
                    maxDate={moment(to).subtract(1, "day")}
                    disabled={false}
                   dateFormat={getDateTimeFormat(scale, !toEnabled)}
                />
            {toEnabled && <div><h2 className="title secondary">{I18n.t("stats.to")}</h2>
                <DatePicker
                    selected={to}
                    preventOpenOnFocus
                    onChange={m => this.setState({"to": m})}
                    showYearDropdown
                    showMonthDropdown
                    todayButton={I18n.t("stats.today")}
                    maxDate={moment()}
                    dateFormat={getDateTimeFormat(scale, !toEnabled)}
                /></div>}
        </fieldset>;

    renderSpSelect = (sp, allSp, clearable, displayDetailPerSP, state) =>
        <fieldset>
            <h2 className="title">{I18n.t("stats.sp")}</h2>
            <SelectWrapper
                defaultValue={sp}
                options={allSp}
                multiple={false}
                isClearable={clearable}
                handleChange={val => this.setState({sp: val ? val : this.allServiceProviderOption.value})}/>
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

    title = (from, to, displayDetailPerSP,sp, scale) => {
        return null;
    };

    render() {
        const {from, to, scale, allSp, data, displayDetailPerSP, loaded, sp, state} = this.state;
        const spSelected = sp !== this.allServiceProviderOption.value;
        const toEnabled = !displayDetailPerSP;
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
                        <Chart data={data}
                               scale={scale}
                               includeUniques={scale !== "minutes" && scale != "hour"}
                               title={this.title(from, to, displayDetailPerSP,sp, scale)}
                               groupedBySp={displayDetailPerSP}
                               aggregate={displayDetailPerSP}
                               serviceProvidersDict={allSp}
                               goRight={this.goRight}
                               goLeft={this.goLeft}
                               rightDisabled={maximumTo}
                               noTimeFrame={scale === "all"}/>
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
