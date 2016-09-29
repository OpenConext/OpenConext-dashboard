import React from "react";
import Match from "react-router/Match";

import CurrentUser from "../models/current_user";

export default function ProtectedRoute({ component, currentUser, ...rest }) {
  if (currentUser.dashboardAdmin) {
    return <Match component={component} {...rest} />;
  } else {
    return null;
  }
}

ProtectedRoute.propTypes = {
  component: React.PropTypes.oneOfType([
    React.PropTypes.element,
    React.PropTypes.func
  ]).isRequired,
  currentUser: React.PropTypes.instanceOf(CurrentUser)
};

ProtectedRoute.displayName = "ProtectedRoute";
