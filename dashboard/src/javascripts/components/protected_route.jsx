import React from "react";
import Match from 'react-router/Match';

export default function({ component, currentUser, ...rest }) {
  if (currentUser.dashboardAdmin) {
    return <Match component={component} {...rest} />;
  } else {
    return null;
  }
}
