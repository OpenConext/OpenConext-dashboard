import React from "react";
import Match from "react-router/Match";

import CurrentUser from "../models/current_user";
import Redirect from "react-router/Redirect";

export default function ProtectedRoute({ component, currentUser, ...rest }) {
  if (currentUser.dashboardAdmin) {
    return <Match component={component} {...rest} />;
  }
  return <Redirect to={"/apps"}/>
}

ProtectedRoute.propTypes = {
  component: React.PropTypes.oneOfType([
    React.PropTypes.element,
    React.PropTypes.func
  ]).isRequired,
  currentUser: React.PropTypes.instanceOf(CurrentUser)
};

ProtectedRoute.displayName = "ProtectedRoute";
