import React from "react";
import PropTypes from "prop-types";
import {Route} from "react-router-dom";
import CurrentUser from "../models/current_user";

export default function ProtectedRoute({ component, currentUser, ...rest }) {
  if (currentUser.dashboardAdmin) {
    return <Route component={component} {...rest} />;
  }

  return null;
}

ProtectedRoute.propTypes = {
  component: PropTypes.oneOfType([
    PropTypes.element,
    PropTypes.func
  ]).isRequired,
  currentUser: PropTypes.instanceOf(CurrentUser)
};

ProtectedRoute.displayName = "ProtectedRoute";
