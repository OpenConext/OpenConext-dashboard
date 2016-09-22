require("../stylesheets/application.sass");
require("../css/select2.min.css");
require("es6-promise").polyfill();
require("isomorphic-fetch");

import React from "react";
import { render } from "react-dom";
import Router from "react-router/BrowserRouter";
import Match from 'react-router/Match';
import I18n from "./lib/i18n";
import { browserSupported } from "./lib/browser_supported";

import CurrentUser from "./models/current-user";

import { getUserData } from "./api";
import Header from "./components/header";
import Footer from "./components/footer";
import Navigation from "./components/navigation";
import BrowserNotSupported from "./pages/browser_not_supported";
import AppOverview from "./pages/app_overview";

import "./locale/en";
import "./locale/nl";

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

          {this.props.page}
          <Match pattern="/apps" component={AppOverview} />

          <Footer />
        </div>
      </Router>
    );
  }
}

App.childContextTypes = {
  currentUser: React.PropTypes.object
};

if (browserSupported()) {
  getUserData()
  .catch(err => window.location = window.location.protocol + "//" + window.location.host + "/dashboard/api/forbidden")
  .then(json => {
    I18n.locale = json.language;
    render(<App currentUser={new CurrentUser(json.payload)} />, document.getElementById("app"));
  });
} else {
  render(<BrowserNotSupported />, document.getElementById("app"));
}
