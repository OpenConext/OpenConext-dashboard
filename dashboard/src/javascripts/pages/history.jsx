import React from "react";

import I18n from "i18n-js";

import {getActions} from "../api";
import sort from "../utils/sort";
import moment from "moment";

import SortableHeader from "../components/sortable_header";

class History extends React.Component {

    constructor() {
        super();
        this.state = {
            actions: [],
            sortAttribute: "requestDate",
            sortAscending: true,
            startAt: 0,
            maxResults: 100,
            total: 0
        };
    }

    componentWillMount() {
        this.fetchActions();
    }

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
            if (i === 1 || i === last || i >= left && i < right) {
                range.push(i);
            }
        }
        for (let i of range) {
            if (l) {
                if (i - l === 2) {
                    rangeWithDots.push(l + 1);
                } else if (i - l !== 1) {
                    rangeWithDots.push('...');
                }
            }
            rangeWithDots.push(i);
            l = i;
        }
        return rangeWithDots;
    }

    fetchActions(startAt = this.state.startAt, maxResults = this.state.maxResults) {
        getActions(startAt, maxResults).then(data => {
            const {issues, total, startAt, maxResults} = data.payload;
            this.setState({actions: issues, startAt: startAt, maxResults: maxResults, total: total});
        });
    }

    handleSort(sortObject) {
        this.setState({
            sortAttribute: sortObject.sortAttribute,
            sortAscending: sortObject.sortAscending
        });
    }

    renderSortableHeader(className, attribute) {
        return (
            <SortableHeader
                sortAttribute={this.state.sortAttribute}
                attribute={attribute}
                sortAscending={this.state.sortAscending}
                localeKey="history"
                className={className}
                onSort={this.handleSort.bind(this)}
            />
        );
    }

    renderPageLink() {
        return null;
    }

    renderPagination(currentPage, total, pageSize) {
        const links = this.pagination(currentPage, Math.ceil(total / pageSize));
        return <div>
            {links.map(this.renderPageLink.bind(this))}
        </div>;
    }

    render() {
        const {actions, sortAttribute, sortAscending, total, maxResults, startAt} = this.state;
        return (
            <div className="l-mini">

                <div className="mod-history">
                    <h1>{I18n.t("history.title")}</h1>

                    <table>
                        <thead>
                        <tr>
                            {this.renderSortableHeader("percent_15", "requestDate")}
                            {this.renderSortableHeader("percent_15", "userName")}
                            {this.renderSortableHeader("percent_25", "type")}
                            {this.renderSortableHeader("percent_20", "jiraKey")}
                            {this.renderSortableHeader("percent_25", "status")}
                        </tr>
                        </thead>
                        <tbody>
                        {sort(actions, sortAttribute, sortAscending)
                            .map(this.renderAction.bind(this))}
                        </tbody>
                    </table>
                    {this.renderPagination((startAt+1), total, maxResults)}
                </div>
            </div>
        );
    }

    renderAction(action) {
        return (
            <tr key={action.jiraKey}>
                <td className="percent_15">{moment(action.requestDate).format("DD-MM-YYYY")}</td>
                <td className="percent_15">{action.userName}</td>
                <td className="percent_25">{I18n.t("history.action_types." + action.type, {serviceName: action.spName})}</td>
                <td className="percent_20">{action.jiraKey}</td>
                <td className="percent_25">{action.status}</td>
            </tr>
        );
    }

    convertRequestDateForSort(value) {
        return Date.parse(value);
    }
}

History.contextTypes = {
    currentUser: React.PropTypes.object
};

export default History;
