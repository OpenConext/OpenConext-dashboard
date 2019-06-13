import React from "react";
import I18n from "i18n-js";
import PropTypes from "prop-types";
import {Spinner} from "spin.js";
import "spin.js/spin.css"

import spinner from "../lib/spin";
import {isEmpty} from "../utils/utils";
import {searchJira} from "../api";
import stopEvent from "../utils/stop";
import {emitter} from "../utils/flash";

class Navigation extends React.Component {
    constructor() {
        super();
        this.state = {
            loading: false,
            awaitingInputTickets: 0,
        };
        this.callback = () => this.getAwaitingInputJiraTickets();
    }

    componentWillMount() {
        spinner.onStart = () => this.setState({loading: true});
        spinner.onStop = () => this.setState({loading: false});
        this.getAwaitingInputJiraTickets();
        emitter.addListener("invite_request_updates", this.callback);
    }

    getAwaitingInputJiraTickets = () => {
        const jiraFilter = {
            maxResults: 0,
            startAt: 0,
            statuses: ["Awaiting Input"],
            types: ["LINKINVITE"]
        };
        searchJira(jiraFilter).then(data => {
            const {total} = data.payload;
            this.setState({awaitingInputTickets: total})
        });
    };

    componentWillUnmount() {
        emitter.removeListener("invite_request_updates", this.callback);
    }

    componentDidUpdate() {
        if (this.state.loading) {
            if (!this.spinner) {
                this.spinner = new Spinner({
                    lines: 20, // The number of lines to draw
                    length: 15, // The length of each line
                    width: 3, // The line thickness
                    radius: 8, // The radius of the inner circle
                    color: "#4DB3CF", // #rgb or #rrggbb or array of colors
                    top: "40px",
                    position: "fixed"
                }).spin(this.spinnerNode);
            }
        } else {
            this.spinner = null;
        }
    }

    renderItem(href, value, activeTab, marker = 0) {
        return (
            <li>
                <a href={href} className={activeTab === href ? "active" : ""}
                   onClick={e => {
                       stopEvent(e);
                       if (href === "/tickets") {
                           this.getAwaitingInputJiraTickets();
                       }
                       this.context.router.history.replace(href);
                   }}>{I18n.t("navigation." + value)}</a>
                {marker > 0 && <span className="marker">{marker}</span>}
            </li>);
    }

    renderSpinner() {
        if (this.state.loading) {
            return <div className="spinner" ref={spinner => this.spinnerNode = spinner}/>;
        }
        return null;
    }

    render() {
        const {currentUser, router} = this.context;
        const {awaitingInputTickets} = this.state;
        const showInviteRequest = !isEmpty(currentUser) && currentUser.superUser;
        const activeTab = router.history.location.pathname;
        const hideTabs = currentUser.hideTabs.split(",").map(s => s.trim());
        return (
            <div className="mod-navigation">
                <ul>
                    {hideTabs.indexOf("statistics") === -1 && this.renderItem("/statistics", "stats", activeTab)}
                    {hideTabs.indexOf("apps") === -1 && this.renderItem("/apps", "apps", activeTab)}
                    {hideTabs.indexOf("policies") === -1 && this.renderItem("/policies", "policies", activeTab)}
                    {hideTabs.indexOf("tickets") === -1 && this.renderItem("/tickets", "history", activeTab, awaitingInputTickets)}
                    {hideTabs.indexOf("my_idp") === -1 && this.renderItem("/my-idp", "my_idp", activeTab)}
                    {(hideTabs.indexOf("user_invite") === -1 && showInviteRequest) && this.renderItem("/users/invite", "invite_request", activeTab)}
                </ul>

                {this.renderSpinner()}
            </div>
        );
    }
}

Navigation.contextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object
};

export default Navigation;
