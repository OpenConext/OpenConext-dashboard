import "../stylesheets/application.css"
import React from "react";
import PropTypes from "prop-types";
import {BrowserRouter as Router, Redirect, Route, Switch} from "react-router-dom";

import CurrentUser from "./models/current_user";
import Header from "./components/header";
import Footer from "./components/footer";
import Navigation from "./components/navigation";
import ProtectedRoute from "./components/protected_route";

import AppDetail from "./pages/app_detail";
import AppOverview from "./pages/app_overview";
import PolicyOverview from "./pages/policy_overview";
import PolicyDetail from "./pages/policy_detail";
import PolicyRevisions from "./pages/policy_revisions";
import History from "./pages/history";
import Profile from "./pages/profile";
import Stats from "./pages/stats";
import MyIdp from "./pages/idp";
import NotFound from "./pages/not_found";
import SearchUser from "./pages/search_user";
import EditMyIdp from "./pages/edit_my_idp";

import "./locale/en";
import "./locale/nl";
import InviteRequest from "./pages/invite_request";


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
                        <Header/>
                        <Navigation/>
                    </div>
                    <Switch>
                        <Route exact path="/" render={() => <Redirect to="/statistics"/>}/>
                        <Route exact path="/apps/:id/:type/:activePanel" component={AppDetail}/>
                        <Route exact path="/apps/:id/:type"
                               render={({params: {id, type}}) => <Redirect to={`/apps/${id}/${type}/overview`}/>}/>
                        <Route exact path="/apps/:back?" component={AppOverview}/>
                        <Route exact path="/policies" component={PolicyOverview}/>
                        <Route exact path="/history" component={History}/>
                        <Route exact path="/profile" component={Profile}/>
                        <Route exact path="/statistics" render={props => <Stats view="full" {...props}/>}/>
                        <Route exact path="/my-idp" component={MyIdp}/>
                        <Route exact path="/my-idp/edit" component={EditMyIdp}/>
                        <Route exact path="/users/search" component={SearchUser}/>
                        <Route exact path="/users/invite" component={InviteRequest}/>
                        <Route exact path="/policies/:id/revisions" component={PolicyRevisions}/>
                        <ProtectedRoute currentUser={this.props.currentUser} path="/policies/:id"
                                        component={PolicyDetail}/>
                        <Route component={NotFound}/>
                    </Switch>
                    <Footer/>
                </div>
            </Router>
        );
    }

}

App.childContextTypes = {
    currentUser: PropTypes.object,
    router: PropTypes.object
};

App.propTypes = {
    currentUser: PropTypes.instanceOf(CurrentUser).isRequired
};

export default App;