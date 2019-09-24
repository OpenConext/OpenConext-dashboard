import "../stylesheets/application.css"
import React from "react";
import PropTypes from "prop-types";
import {BrowserRouter as Router, Redirect, Route, Switch} from "react-router-dom";

import CurrentUser from "./models/current_user";
import Header from "./components/header";
import Footer from "./components/footer";
import Navigation from "./components/navigation";
import {ProtectedRoute, SuperUserProtectedRoute} from "./components/protected_route";

import AppDetail from "./pages/app_detail";
import AppOverview from "./pages/app_overview";
import PolicyOverview from "./pages/policy_overview";
import PolicyDetail from "./pages/policy_detail";
import Dummy from "./pages/dummy";
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
import "./locale/pt";
import InviteRequest from "./pages/invite_request";


class App extends React.Component {

    getChildContext() {
        return {
            currentUser: this.props.currentUser
        };
    }

    render() {
        const {currentUser} = this.props;
        return (
            <Router>
                <div>
                    <div className="l-header">
                        <Header/>
                        <Navigation/>
                    </div>
                    <Switch>
                        <Route exact path="/" render={() => currentUser.guest ? <Redirect to="/apps"/> : <Redirect to="/statistics"/>}/>
                        <Route exact path="/apps/:id/:type/:activePanel/:jiraKey/:action" component={AppDetail}/>
                        <Route exact path="/apps/:id/:type/:activePanel" component={AppDetail}/>
                        <Route exact path="/apps/:id/:type"
                               render={({params: {id, type}}) => <Redirect to={`/apps/${id}/${type}/overview`}/>}/>
                        <Route exact path="/apps/:back?" component={AppOverview}/>
                        {!currentUser.guest && <Route exact path="/policies" component={PolicyOverview}/>}
                        {!currentUser.guest && <Route exact path="/tickets" component={History}/>}
                        {!currentUser.guest && <Route exact path="/profile" component={Profile}/>}
                        {!currentUser.guest && <Route exact path="/statistics" render={props => <Stats view="full" {...props}/>}/>}
                        {!currentUser.guest && <Route exact path="/my-idp" component={MyIdp}/>}
                        {!currentUser.guest && <Route exact path="/my-idp/edit" component={EditMyIdp}/>}
                        <SuperUserProtectedRoute currentUser={currentUser} path="/users/search"
                                                 component={SearchUser}/>
                        <SuperUserProtectedRoute currentUser={currentUser} path="/users/invite"
                                                 component={InviteRequest}/>
                        {!currentUser.guest &&  <Route exact path="/policies/:id/revisions" component={PolicyRevisions}/>}
                        <ProtectedRoute currentUser={currentUser} path="/policies/:id"
                                        component={PolicyDetail}/>
                        <Route exact path="/dummy" component={Dummy}/>
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
