import React from "react";
import PropTypes from "prop-types";
import {Route, Redirect} from "react-router-dom";
import CurrentUser from "../models/current_user";

export function ProtectedRoute({component, currentUser, ...rest}) {
    if (currentUser.dashboardAdmin) {
        return <Route component={component} {...rest} />;
    }
    return <Redirect to={"/apps"}/>;
}

export function SuperUserProtectedRoute({component, currentUser, ...rest}) {
    if (currentUser.superUser) {
        return <Route component={component} {...rest} />;
    }
    return <Redirect to={"/apps"}/>;
}

ProtectedRoute.propTypes = {
    component: PropTypes.oneOfType([
        PropTypes.element,
        PropTypes.func
    ]).isRequired,
    currentUser: PropTypes.instanceOf(CurrentUser)
};

ProtectedRoute.displayName = "ProtectedRoute";
