require("../stylesheets/application.sass");
require("../css/select2.min.css");
require("es6-promise").polyfill();
require("isomorphic-fetch");

import React from "react";
import { render } from "react-dom";
import Router from "react-router/BrowserRouter";
import Match from 'react-router/Match';
import Redirect from 'react-router/Redirect';
import I18n from "./lib/i18n";
import { browserSupported } from "./lib/browser_supported";

import CurrentUser from "./models/current-user";

import { getUserData } from "./api";
import Header from "./components/header";
import Footer from "./components/footer";
import Navigation from "./components/navigation";
import BrowserNotSupported from "./pages/browser_not_supported";

import AppDetail from "./pages/app_detail";
import AppOverview from "./pages/app_overview";
import PolicyOverview from "./pages/policy_overview";
import PolicyDetail from "./pages/policy_detail";

import "./locale/en";
import "./locale/nl";

const MatchStartRoute = () => {
    //the redirect_uri goes to /, but we have stored the requested href in the state parameter
    var locationHash = window.location.hash.substr(1);
    var url = locationHash.substr(locationHash.indexOf("state=")).split("&")[0].split("=")[1];
    if (!url) {
      return <Redirect to={{
        pathname: "/apps"
      }} />
    }
    var parser = document.createElement('a');
    parser.href = decodeURIComponent(url);
    var pathname = parser.pathname;
    return <Redirect to={{
      pathname: pathname
    }} />
};

const ProtectedRoute = ({ component: Component, currentUser: currentUser, ...rest }) => {
  if (currentUser.dashboardAdmin) {
    return <Match component={Component} {...rest} />
  } else {
    window.location = window.location.protocol + "//" + window.location.host + "/dashboard/api/forbidden";
  }
};

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
            <Navigation />
          </div>

          <MatchStartRoute exactly pattern="/" />
          <Match exactly pattern="/apps/:id/:activePanel" component={AppDetail} />
          <Match exactly pattern="/apps/:id" component={AppDetail} />
          <Match exactly pattern="/apps" component={AppOverview} />
          <Match exactly pattern="/policies" component={PolicyOverview} />
          <ProtectedRoute currentUser={this.props.currentUser} exactly pattern="/policies/new" component={PolicyDetail} />

          <Footer />
        </div>
      </Router>
    );
  }
}

App.childContextTypes = {
  currentUser: React.PropTypes.object,
  router: React.PropTypes.object
};

function fetchStatsToken() {
  var locationHash = window.location.hash.substr(1);
  return locationHash.substr(locationHash.indexOf("access_token=")).split("&")[0].split("=")[1];
}

function authorizeStats(statsUrl) {
  window.location = statsUrl + "&state=" + window.location;
}

if (browserSupported()) {
  getUserData()
  .catch(err => window.location = window.location.protocol + "//" + window.location.host + "/dashboard/api/forbidden")
  .then(json => {
    I18n.locale = json.language;
    const currentUser = new CurrentUser(json.payload);
    currentUser.statsToken = fetchStatsToken();

    if (!currentUser.statsToken) {
      return authorizeStats(currentUser.statsUrl);
    }

    render(<App currentUser={currentUser} />, document.getElementById("app"));
  });
} else {
  render(<BrowserNotSupported />, document.getElementById("app"));
}
