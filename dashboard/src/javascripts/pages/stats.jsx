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
            state: "all",

        };
    }

    componentWillMount() {
        const {scale, state} = this.state;
        loginAggregated(getPeriod(moment(), scale), state).then(res => {
            this.setState({data: res});
            statsServiceProviders().then(res => this.setState({
                allSp: [this.allServiceProviderOption].concat(res)
            }))
        });
    }

    renderFrom = () =>
        <fieldset>
            <h2 className="title">{I18n.t("stats.timeScale")}</h2>
            <SelectWrapper
                defaultValue={this.state.scale}
                options={defaultScales.map(scale => ({display: I18n.t(`stats.scale.${scale}`), value: scale}))}
                multiple={false}
                handleChange={val => this.setState({scale: val })}/>
            <h2 className="title secondary">{I18n.t("stats.from")}</h2>
            {/*<label className="date-picker-container">*/}
                <DatePicker
                    selected={this.state.from}
                    preventOpenOnFocus
                    onChange={m => this.setState({"from": m})}
                    showYearDropdown
                    showMonthDropdown
                    todayButton={I18n.t("stats.today")}
                    maxDate={this.state.to.subtract(1, "day")}
                    disabled={false}
                   dateFormat={getDateTimeFormat(this.state.scale)}
                />
                {/*<i className="fa fa-calendar"></i>*/}
            {/*</label>*/}
            <h2 className="title secondary">{I18n.t("stats.to")}</h2>
            {/*<label className="date-picker-container">*/}
                <DatePicker
                    selected={this.state.to}
                    preventOpenOnFocus
                    onChange={m => this.setState({"to": m})}
                    showYearDropdown
                    showMonthDropdown
                    todayButton={I18n.t("stats.today")}
                    maxDate={moment()}
                    disabled={this.state.scale !== "all"}
                    dateFormat={getDateTimeFormat(this.state.scale)}
                />
                {/*<i className="fa fa-calendar"></i>*/}
            {/*</label>*/}
        </fieldset>;

    renderSpSelect = () =>
        <fieldset>
            <h2 className="title">{I18n.t("stats.sp")}</h2>
            <SelectWrapper
                defaultValue={this.state.sp}
                options={this.state.allSp}
                multiple={false}
                isClearable={this.state.sp !== this.allServiceProviderOption.value}
                handleChange={val => {
                    this.setState({sp: val ? val : this.allServiceProviderOption.value});
                }}/>
            <CheckBox name="display" value={this.state.displayDetailPerSP}
                      onChange={e => this.setState({displayDetailPerSP: !this.state.displayDetailPerSP})}
                        info={I18n.t("stats.displayDetailPerSP")}
            />
        </fieldset>;


    render() {
        return (
            <div className="l-main stats">
                <div className="l-left-large">
                    <div className="mod-filters">
                        <div className="header">
                            <h1>{I18n.t("stats.filters.name")}</h1>
                        </div>
                        {this.renderSpSelect()}
                        {this.renderFrom()}
                    </div>
                </div>
                <div className="l-right-small">
                    <div className="mod-chart">
                        {/*<Chart chart={this.state.chart}/>*/}
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
