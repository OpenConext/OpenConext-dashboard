require("../stylesheets/application.sass");
require("es6-promise").polyfill();
require("isomorphic-fetch");
require("lodash");

import React from "react";
import { render } from "react-dom";
import Router from "react-router/BrowserRouter";
import Match from "react-router/Match";
import Miss from "react-router/Miss";
import Redirect from "react-router/Redirect";
import I18n from "i18n-js";
import { browserSupported } from "./lib/browser_supported";
import moment from "moment";

import CurrentUser, { createCurrentUser } from "./models/current_user";

import { getUserData } from "./api";
import Header from "./components/header";
import Footer from "./components/footer";
import Navigation from "./components/navigation";
import ProtectedRoute from "./components/protected_route";
import MatchStartRoute from "./components/match_start_route";
import BrowserNotSupported from "./pages/browser_not_supported";

import AppDetail from "./pages/app_detail";
import AppOverview from "./pages/app_overview";
import PolicyOverview from "./pages/policy_overview";
import PolicyDetail from "./pages/policy_detail";
import PolicyRevisions from "./pages/policy_revisions";
import Notifications from "./pages/notifications";
import History from "./pages/history";
import Profile from "./pages/profile";
import Stats from "./pages/stats";
import MyIdp from "./pages/idp";
import NotFound from "./pages/not_found";
import SearchUser from "./pages/search_user";
import EditMyIdp from "./pages/edit_my_idp";

import "./locale/en";
import "./locale/nl";
import "react-select/dist/react-select.css";

class App extends React.Component {
  getChildContext() {
    return {
      currentUser: this.props.currentUser
    };
  }

  render() {
    return (
      <Router>
        <div>
          <div className="l-header">
            <Header />
            {this.renderNavigation()}
          </div>

          <MatchStartRoute />
          <Match exactly pattern="/apps/:id/:activePanel" component={AppDetail} />
          <Match exactly pattern="/apps/:id" render={({ params: { id } }) => {
            return <Redirect to={`/apps/${id}/overview`} />;
          }} />
          <Match exactly pattern="/apps" component={AppOverview} />
          <Match exactly pattern="/policies" component={PolicyOverview} />
          <Match exactly pattern="/notifications" component={Notifications} />
          <Match exactly pattern="/history" component={History} />
          <Match exactly pattern="/profile" component={Profile} />
          <Match exactly pattern="/statistics" component={Stats} />
          <Match exactly pattern="/my-idp" component={MyIdp} />
          <Match exactly pattern="/my-idp/edit" component={EditMyIdp} />
          <Match exactly pattern="/users/search" component={SearchUser} />
          <ProtectedRoute currentUser={this.props.currentUser} exactly pattern="/policies/:id" component={PolicyDetail} />
          <Match exactly pattern="/policies/:id/revisions" component={PolicyRevisions} />
          <Miss component={NotFound} />

          <Footer />
        </div>
      </Router>
    );
  }

  renderNavigation() {
    return this.props.currentUser.superUserNotSwitched() ? null : <Navigation />;
  }
}

App.childContextTypes = {
  currentUser: React.PropTypes.object,
  router: React.PropTypes.object
};

App.propTypes = {
  currentUser: React.PropTypes.instanceOf(CurrentUser).isRequired
};

if (browserSupported()) {
  getUserData()
  .catch(() => window.location = window.location.protocol + "//" + window.location.host + "/dashboard/api/home")
  .then(json => {
    I18n.locale = json.language;
    moment.locale(json.language);
    const currentUser = createCurrentUser(json.payload);
    const locationHash = window.location.hash.substr(1);
    currentUser.statsToken = locationHash.substr(locationHash.indexOf("access_token=")).split("&")[0].split("=")[1];

    if (!currentUser.statsToken) {
      window.location = currentUser.statsUrl + "&state=" + window.location;
    } else {
      render(<App currentUser={currentUser} />, document.getElementById("app"));
    }
  });
} else {
  render(<BrowserNotSupported />, document.getElementById("app"));
}
