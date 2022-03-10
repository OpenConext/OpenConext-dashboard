import React from 'react'
import {Redirect, Route} from 'react-router-dom'

export function SuperUserProtectedRoute({component, currentUser, ...rest}) {
    if (currentUser.superUser) {
        return <Route component={component} {...rest} />
    }
    return <Redirect to={'/404'}/>
}
