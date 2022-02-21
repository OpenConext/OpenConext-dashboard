import React from 'react'
import PropTypes from 'prop-types'
import { Route, Redirect } from 'react-router-dom'
import CurrentUser from '../models/current_user'
import {login} from "../utils/utils";

export function ProtectedRoute({ component, currentUser, ...rest }) {
  if (!currentUser.guest) {
    return <Route exact component={component} {...rest} />
  }
  login();
}

export function SuperUserProtectedRoute({ component, currentUser, ...rest }) {
  if (currentUser.superUser) {
    return <Route component={component} {...rest} />
  }
  return <Redirect to={'/404'} />
}

ProtectedRoute.propTypes = {
  component: PropTypes.oneOfType([PropTypes.element, PropTypes.func]).isRequired,
  currentUser: PropTypes.instanceOf(CurrentUser),
}

ProtectedRoute.displayName = 'ProtectedRoute'
