import React from "react";
import PropTypes from "prop-types";
import DatePicker from "react-datepicker";
import I18n from "i18n-js";

import {getActions} from "../api";
import sort from "../utils/sort";
import moment from "moment";

import SortableHeader from "../components/sortable_header";
import pagination from "../utils/pagination";
import SelectWrapper from "../components/select_wrapper";
import stopEvent from "../utils/stop";

const allStatuses = ["to_do", "in_progress", "awaiting_input", "resolved", "closed"];

const pageCount = 10;

class History extends React.Component {

    constructor() {
        super();
        this.state = this.getInitialState();
    }

    componentDidMount() {
        this.fetchIssues();
    }

    reset = e => {
        stopEvent(e);
        this.setState(this.getInitialState(), this.componentDidMount)
    };

    getInitialState = () => ({
        actions: [],
        sortAttribute: "requestDate",
        sortAscending: true,
        startAt: 0,
        maxResults: 1000,
        total: 0,
        from: moment().subtract(3, "month"),
        to: moment(),
        page: 1,
        statuses: allStatuses.slice(0, 3),
        spEntityId: "",
        serviceProviders: []
    });

    pagination(currentPage, length) {
        const current = currentPage,
            last = length,
            delta = 2,
            left = current - delta,
            right = current + delta + 1,
            range = [],
            rangeWithDots = [];
        let l;
        for (let i = 1; i <= last; i++) {
            // eslint-disable-next-line
            if (i === 1 || i === last || i >= left && i < right) {
                range.push(i);
            }
        }
        for (const i of range) {
            if (l) {
                if (i - l === 2) {
                    rangeWithDots.push(l + 1);
                } else if (i - l !== 1) {
                    rangeWithDots.push("...");
                }
            }
            rangeWithDots.push(i);
            l = i;
        }
        return rangeWithDots;
    }

    fetchIssues(startAt = this.state.startAt, maxResults = this.state.maxResults) {
        getActions(startAt, maxResults).then(data => {
            const {issues, total, startAt, maxResults} = data.payload;
            const serviceProviders = new Set(issues.map(issue => {
                issue.spName = issue.spName === "Information unavailable" ? issue.spId : issue.spName;
                return issue.spName;
            }));
            this.setState({
                actions: issues, startAt: startAt, maxResults: maxResults, total: total,
                serviceProviders: [""].concat([...serviceProviders])
            });
        });
    }

    handleSort = sortObject => this.setState({
        sortAttribute: sortObject.sortAttribute,
        sortAscending: sortObject.sortAscending
    });

    renderSortableHeader(className, attribute) {
        return (
            <SortableHeader
                sortAttribute={this.state.sortAttribute}
                attribute={attribute}
                sortAscending={this.state.sortAscending}
                localeKey="history"
                className={className}
                onSort={sortObject => this.handleSort(sortObject)}
            />
        );
    }

    changePage = nbr => () => {
        this.setState({page: nbr}, () => window.scrollTo({
            "behavior": "smooth",
            "left": 0,
            "top": 0
        }));
    };

    renderPagination(resultLength, page) {
        if (resultLength <= pageCount) {
            return null;
        }
        const nbrPages = Math.ceil(resultLength / pageCount);
        const rangeWithDots = pagination(page, nbrPages);
        return (
            <section className="pagination">
                <section className="container">
                    {(nbrPages > 1 && page !== 1) &&
                    <i className="fa fa-arrow-left" onClick={this.changePage(page - 1)}></i>}
                    {rangeWithDots.map((nbr, index) =>
                        typeof(nbr) === "string" || nbr instanceof String ?
                            <span key={index} className="dots">{nbr}</span> :
                            nbr === page ?
                                <span className="current" key={index}>{nbr}</span> :
                                <span key={index} onClick={this.changePage(nbr)}>{nbr}</span>
                    )}
                    {(nbrPages > 1 && page !== nbrPages) &&
                    <i className="fa fa-arrow-right" onClick={this.changePage(page + 1)}></i>}
                </section>

            </section>);
    }

    onChangeFrom = e => this.setState({from: e});

    onChangeTo = e => this.setState({to: e});

    onChangeStatus = e => this.setState({statuses: e});

    onChangeSp = e => this.setState({spEntityId: e});

    renderFilter = (from, to, statuses, spEntityId, serviceProviders) => {
        return <section className="filters">
            <div className="header">
                <h1>{I18n.t("stats.filters.name")}</h1>
                <a href="reset" className="reset c-button" onClick={this.reset}>{I18n.t("facets.reset")}</a>
            </div>
            <fieldset>
                <label>{I18n.t("history.from")}</label>
                <DatePicker
                    selected={from}
                    preventOpenOnFocus
                    onChange={this.onChangeFrom}
                    showYearDropdown
                    showMonthDropdown
                    showWeekNumbers
                    weekLabel="Week"
                    maxDate={to}
                    disabled={false}
                    dateFormat={"L"}
                />
                <label>{I18n.t("history.to")}</label>
                <DatePicker
                    selected={to}
                    preventOpenOnFocus
                    onChange={this.onChangeTo}
                    showYearDropdown
                    showMonthDropdown
                    showWeekNumbers
                    weekLabel="Week"
                    maxDate={moment()}
                    disabled={false}
                    dateFormat={"L"}
                />
                <label>{I18n.t("history.status")}</label>
                <SelectWrapper
                    defaultValue={statuses}
                    options={allStatuses.map(t => ({value: t, display: I18n.t(`history.statuses.${t.toLowerCase()}`)}))}
                    multiple={true}
                    isClearable={false}
                    handleChange={this.onChangeStatus}/>
                <label>{I18n.t("history.spEntityId")}</label>
                <SelectWrapper
                    defaultValue={spEntityId}
                    options={serviceProviders.map(t => ({value: t, display: t === "" ? I18n.t("history.servicePlaceHolder") : t}))}
                    multiple={false}
                    isClearable={true}
                    handleChange={this.onChangeSp}/>
            </fieldset>
        </section>
    };


    renderAction = action =>
        <tr key={action.jiraKey}>
            <td >{moment(action.requestDate).format("DD-MM-YYYY")}</td>
            <td >{action.spName === "Information unavailable" ? action.spId : action.spName}</td>
            <td >{action.userName}</td>
            <td >{I18n.t("history.action_types." + action.type, {serviceName: action.spName})}</td>
            <td >{action.jiraKey}</td>
            <td >{action.status}</td>
        </tr>


    render() {
        const {actions, sortAttribute, sortAscending, total, from, to, statuses, spEntityId, page,
            serviceProviders} = this.state;
        let sortedActions = sort(actions, sortAttribute, sortAscending);
        if (sortedActions.length > pageCount) {
            sortedActions = sortedActions.slice((page - 1) * pageCount, page * pageCount);
        }
        return (
            <div className="mod-history">
                {this.renderFilter(from, to, statuses, spEntityId, serviceProviders)}
                <div className="table_wrapper">
                    <table>
                        <thead>
                        <tr>
                            {this.renderSortableHeader("percent_10", "requestDate")}
                            {this.renderSortableHeader("percent_15", "spName")}
                            {this.renderSortableHeader("percent_15", "userName")}
                            {this.renderSortableHeader("percent_25", "type")}
                            {this.renderSortableHeader("percent_15", "jiraKey")}
                            {this.renderSortableHeader("percent_20", "status")}
                        </tr>
                        </thead>
                        <tbody>
                        {sortedActions.map(action => this.renderAction(action))}
                        </tbody>
                    </table>
                    {this.renderPagination(total, page)}
                </div>
            </div>
        );
    }
}

History.contextTypes = {
    currentUser: PropTypes.object
};

export default History;
