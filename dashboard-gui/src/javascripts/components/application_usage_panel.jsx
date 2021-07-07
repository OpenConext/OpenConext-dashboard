import React from "react";
import PropTypes from "prop-types";
import moment from "moment";

import {AppShape} from "../shapes";
import Stats from "../pages/stats";

class ApplicationUsagePanel extends React.Component {
  constructor() {
    super();

    this.state = {
      chart: {
        type: "idpsp",
        periodFrom: moment().subtract(1, "months"),
        periodTo: moment(),
        periodType: "m",
        periodDate: moment(),
        error: false
      }
    };
  }

  render() {
    return (
      <div className="l-middle-app-detail">
        <div className="mod-usage">
          <Stats view="minimal" sp={this.props.app.spEntityId}/>
        </div>
      </div>
    );
  }
}

ApplicationUsagePanel.contextTypes = {
  currentUser: PropTypes.object
};

ApplicationUsagePanel.propTypes = {
  app: AppShape.isRequired
};

export default ApplicationUsagePanel;
