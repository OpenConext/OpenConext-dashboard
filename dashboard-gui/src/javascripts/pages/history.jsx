import React from "react";
import PropTypes from "prop-types";
import DatePicker from "react-datepicker";
import I18n from "i18n-js";
import ReactTooltip from "react-tooltip";
import {searchJira} from "../api";
import sort from "../utils/sort";
import moment from "moment";

import SortableHeader from "../components/sortable_header";
import pagination from "../utils/pagination";
import SelectWrapper from "../components/select_wrapper";
import stopEvent from "../utils/stop";
import {isEmpty} from "../utils/utils";

const pageCount = 10;

const allStatuses = ["To Do", "In Progress", "Awaiting Input", "Resolved", "Closed"];
const allTypes = ["LINKREQUEST", "UNLINKREQUEST", "CHANGE", "LINKINVITE"];

class History extends React.Component {

    constructor() {
        super();
        this.state = this.getInitialState();
    }

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
        types: allTypes,
        spEntityId: "",
        serviceProviders: [],
        loaded: false
    });

    componentDidMount() {
        this.setState({actions: [], loaded: false});
        const {startAt, sortAttribute, sortAscending, maxResults, from, to, statuses, types, spEntityId} = this.state;
        const filter = {
            maxResults: maxResults,
            startAt: startAt,
            from: from.unix(),
            to: moment(to).add(1, "day").unix(),
            spEntityId: spEntityId,
            statuses: statuses,
            types: types,
            sortBy: sortAttribute,
            sortAsc: sortAscending
        };
        searchJira(filter).then(data => {
            const {issues, total, startAt, maxResults} = data.payload;
            const serviceProvidersUnique = issues
                .filter(issue => !isEmpty(issue.spId))
                .reduce((acc, issue) => {
                    acc[issue.spId] = issue.spName === "Information unavailable" ? issue.spId : issue.spName;
                    return acc;
                }, {});
            const serviceProviders = Object.keys(serviceProvidersUnique).map(spId => ({
                spId: spId,
                spName: serviceProvidersUnique[spId]
            }));
            this.setState({
                actions: issues,
                startAt: startAt,
                maxResults: maxResults,
                total: total,
                loaded: true,
                serviceProviders: serviceProviders
            }, () => window.scrollTo({
                "behavior": "smooth",
                "left": 0,
                "top": 0
            }));
        });

    }

    reset = e => {
        stopEvent(e);
        this.setState(this.getInitialState(), this.componentDidMount)
    };

    refresh = e => {
        stopEvent(e);
        this.componentDidMount();
    };

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

    handleSort = sortObject => this.setState({
        sortAttribute: sortObject.sortAttribute,
        sortAscending: sortObject.sortAscending,
        startAt: 0,
        page: 1
    });

    renderSortableHeader(className, attribute) {
        return (
            <SortableHeader
                sortAttribute={this.state.sortAttribute}
                attribute={attribute}
                sortAscending={this.state.sortAscending}
                localeKey="history"
                className={`${className} sortable-header`}
                onSort={sortObject => this.handleSort(sortObject)}
            />
        );
    }

    changePage = nbr => () => {
        this.setState({page: nbr});
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

    onChangeFilter = name => e => {
        const newState = {startAt: 0, page: 1};
        newState[name] = e;
        this.setState(newState, this.componentDidMount);
    };

    renderFilter = (from, to, statuses, spEntityId, serviceProviders, types) => {
        return <section className="filters">
            <div className="header">
                <h1>{I18n.t("stats.filters.name")}</h1>
                <a href="refresh" className="refresh c-button" onClick={this.refresh}>{I18n.t("facets.refresh")}</a>
                <a href="reset" className="reset c-button" onClick={this.reset}>{I18n.t("facets.reset")}</a>
            </div>
            <fieldset>
                <label>{I18n.t("history.from")}</label>
                <DatePicker
                    selected={from}
                    preventOpenOnFocus
                    onChange={this.onChangeFilter("from")}
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
                    onChange={this.onChangeFilter("to")}
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
                    options={allStatuses.map(t => ({value: t, display: I18n.t(`history.statuses.${t}`)}))}
                    multiple={true}
                    isClearable={false}
                    handleChange={this.onChangeFilter("statuses")}/>
                <label>{I18n.t("history.typeIssue")}</label>
                <SelectWrapper
                    defaultValue={types}
                    options={allTypes.map(t => ({
                        value: t,
                        display: I18n.t(`history.action_types_name.${t}`)
                    }))}
                    multiple={true}
                    isClearable={false}
                    handleChange={this.onChangeFilter("types")}/>
                <label>{I18n.t("history.spEntityId")}</label>
                <SelectWrapper
                    defaultValue={spEntityId}
                    options={serviceProviders.map(t => ({
                        value: t.spId,
                        display: t.spName
                    }))}
                    multiple={false}
                    isClearable={true}
                    placeholder={I18n.t("history.servicePlaceHolder")}
                    handleChange={this.onChangeFilter("spEntityId")}/>
            </fieldset>
        </section>
    };

    viewInvitation = action => e => {
        stopEvent(e);
        const type = "saml20_sp";
        this.context.router.history.replace(`/apps/${action.spEid}/${type}/how_to_connect/${action.jiraKey}/accept`);
    };

    renderResolution = action => {
      if (action.resolution) {
          const transKey = action.resolution.replace(/'/g,"").replace(/ /g,"_").toLowerCase();
          return <span className="actionResolution">{I18n.t(`history.resolution.${transKey}`)}
              <i className="fa fa-info-circle" data-for={action.jiraKey} data-tip></i>
                                <ReactTooltip id={action.jiraKey} type="info" class="tool-tip" effect="solid">
                                    <span>{I18n.t(`history.resolution.${transKey}Tooltip`)}</span>
                                </ReactTooltip>
          </span>
      }
      return null;
    };

    renderAction = action => {
        const currentUser = this.context.currentUser;
        const renderAction = action.type === "LINKINVITE" && action.status === "Awaiting Input" && action.spEid && currentUser.dashboardAdmin;
        return <tr key={action.jiraKey}>
            <td>{moment(action.requestDate).format("DD-MM-YYYY")}</td>
            <td>{moment(action.updateDate).format("DD-MM-YYYY")}</td>
            <td>{action.spName === "Information unavailable" ? action.spId : action.spName}</td>
            <td>{action.userName}</td>
            <td>{I18n.t("history.action_types_name." + action.type)}</td>
            <td>{action.jiraKey}</td>
            <td>{I18n.t("history.statuses." + action.status)}</td>
            <td>{renderAction ?  <a href="/send" className={`t-button save`}
                                    onClick={this.viewInvitation(action)}>{I18n.t("history.viewInvitation")}</a> : this.renderResolution(action)}</td>
        </tr>
    };

    render() {
        const {
            actions, sortAttribute, sortAscending, total, from, to, statuses, spEntityId, page,
            serviceProviders, types, loaded
        } = this.state;
        let sortedActions = sort(actions, sortAttribute, sortAscending);
        if (sortedActions.length > pageCount) {
            sortedActions = sortedActions.slice((page - 1) * pageCount, page * pageCount);
        }
        return (
            <div className="mod-history">
                {this.renderFilter(from, to, statuses, spEntityId, serviceProviders, types)}
                <div className="table_wrapper">
                    <p className="info">{I18n.t("history.info")}</p>
                    {(loaded && sortedActions.length > 0) &&
                    <table>
                        <thead>
                        <tr>
                            {this.renderSortableHeader("percent_10", "requestDate")}
                            {this.renderSortableHeader("percent_10", "updateDate")}
                            {this.renderSortableHeader("percent_15", "spName")}
                            <th className={"percent_10"}>{I18n.t("history.userName")}</th>
                            {this.renderSortableHeader("percent_15", "type")}
                            {this.renderSortableHeader("percent_10", "jiraKey")}
                            {this.renderSortableHeader("percent_15", "status")}
                            <th className={"percent_15"}></th>
                        </tr>
                        </thead>
                        <tbody>
                        {sortedActions.map(action => this.renderAction(action))}
                        </tbody>
                    </table>}
                    {(loaded && sortedActions.length === 0) && <div>
                        <p>{I18n.t("history.noTicketsFound")}</p>
                    </div>}
                    {(loaded) && this.renderPagination(total, page)}
                </div>
            </div>
        );
    }
}

History.contextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object
};

export default History;
