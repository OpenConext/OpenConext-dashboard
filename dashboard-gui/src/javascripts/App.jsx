import '../stylesheets/application.css'
import React from 'react'
import PropTypes from 'prop-types'
import { BrowserRouter as Router, Redirect, Route, Switch } from 'react-router-dom'

import CurrentUser from './models/current_user'
import Header from './components/header'
import Footer from './components/footer'
import { ProtectedRoute, SuperUserProtectedRoute } from './components/protected_route'

import AppDetail from './pages/app_detail'
import AppOverview from './pages/app_overview'
import PolicyOverview from './pages/policy_overview'
import PolicyDetail from './pages/policy_detail'
import Dummy from './pages/dummy'
import PolicyRevisions from './pages/policy_revisions'
import History from './pages/history'
import Profile from './pages/profile'
import Welcome from './components/welcome'
import Stats from './pages/stats'
import MyIdp from './pages/idp'
import NotFound from './pages/not_found'
import SearchUser from './pages/search_user'
import EditMyIdp from './pages/edit_my_idp'
import ServicesOverview from './pages/services_overview'
import ServiceDetail from './pages/service_detail'

import './locale/en'
import './locale/nl'
import './locale/pt'
import InviteRequest from './pages/invite_request'
import ResendInvite from './pages/resend_invite'

export const CurrentUserContext = React.createContext({ currentUser: null })

class App extends React.Component {
  getChildContext() {
    return {
      currentUser: this.props.currentUser,
    }
  }

  render() {
    const { currentUser } = this.props
    const isViewerOrAdmin = currentUser.dashboardAdmin || currentUser.dashboardViewer || currentUser.superUser
    const isAllowedToMaintainPolicies =
      currentUser.dashboardAdmin || currentUser.getCurrentIdp().allowMaintainersToManageAuthzRules
    const nonGuest = !currentUser.guest
    const showStats = currentUser.showStats()
    return (
      <CurrentUserContext.Provider value={{ currentUser: currentUser }}>
        <Router>
          <>
            <div className="l-header">
              <Header />
              <Welcome />
            </div>
            <div className="l-content">
              <Switch>
                <Route exact path="/" render={() => <Redirect to="/apps/connected" />} />

                <Route path="/apps/:id/:type" component={ServiceDetail} />
                <ProtectedRoute
                  currentUser={currentUser}
                  path="/apps/:id/:type/:activePanel/:jiraKey/:action"
                  component={ServiceDetail}
                />
                <Route exact path="/apps/:id/:type/:activePanel" component={AppDetail} />

                <Route
                  exact
                  path="/apps/:id/:type"
                  render={({
                    match: {
                      params: { id, type },
                    },
                  }) => <Redirect to={`/apps/${id}/${type}/overview`} />}
                />

                <Route exact path="/apps/connected" render={() => <ServicesOverview key="connected" connected />} />
                <Route exact path="/apps/all" component={ServicesOverview} />
                <Route exact path="/apps/:back?" component={AppOverview} />
                {isViewerOrAdmin && <Route exact path="/policies" component={PolicyOverview} />}
                {isViewerOrAdmin && <Route exact path="/tickets" component={History} />}
                {nonGuest && <Route exact path="/profile" component={Profile} />}
                {showStats && <Route exact path="/statistics" render={(props) => <Stats view="full" {...props} />} />}
                {nonGuest && <Route exact path="/my-idp" component={MyIdp} />}
                {currentUser.dashboardAdmin && <Route exact path="/my-idp/edit" component={EditMyIdp} />}
                <SuperUserProtectedRoute currentUser={currentUser} path="/users/search" component={SearchUser} />
                <SuperUserProtectedRoute currentUser={currentUser} path="/users/invite" component={InviteRequest} />
                <SuperUserProtectedRoute
                  currentUser={currentUser}
                  path="/users/resend_invite/:jiraKey"
                  component={ResendInvite}
                />
                {isAllowedToMaintainPolicies && (
                  <Route exact path="/policies/:id/revisions" component={PolicyRevisions} />
                )}
                {isAllowedToMaintainPolicies && <Route exact path="/policies/:id" component={PolicyDetail} />}
                <Route exact path="/dummy" component={Dummy} />
                <Route component={NotFound} />
              </Switch>
            </div>
            <Footer currentUser={currentUser} />
          </>
        </Router>
      </CurrentUserContext.Provider>
    )
  }
}

App.childContextTypes = {
  currentUser: PropTypes.object,
  router: PropTypes.object,
}

App.propTypes = {
  currentUser: PropTypes.instanceOf(CurrentUser).isRequired,
}

export default App
