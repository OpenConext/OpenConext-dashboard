import React from "react";
import I18n from "i18n-js";
import $ from "jquery";

import { STATS_HOST } from "../api/stats";
import { getPeriod } from "../utils/period";

class Chart extends React.Component {
  constructor() {
    super();
    this.state = {
      error: false
    };
  }

  componentWillMount() {
    this.setState({ error: this.props.chart.error });
  }

  componentWillUpdate(newProps) {
    if (this.props.chart.error !== newProps.chart.error) {
      this.setState({ error: newProps.chart.error });
    }
  }

  componentDidUpdate(prevProps, prevState) {
    if (this.props.chart === prevProps.chart) {
      return;
    }
    if (!this.props.chart.idp) {
      return;
    }
    if (this.props.chart.type === "idpsp" && !this.props.chart.sp) {
      return;
    }

    this.setState({ error: false });

    const chartId = this.chart.id;
    const setMinimumHeightOfChart = function (height) {
      $("#" + chartId).css("min-height", height + "px");
    };
    let options = {
      idp: this.props.chart.idp,
      imagePath: STATS_HOST + "/api/js/graphs-v1/images/amcharts/"
    };

    const { chart } = this.props;

    switch (chart.type) {
    case "idpspbar":
      options = _.merge(options, {
        period: getPeriod(chart.periodDate, chart.periodType),
        dataCallbacks: [function(data) {
          const height = Math.max(data.numRecords * 25, 300);
          setMinimumHeightOfChart(height);
        }]
      });
      break;
    case "idpsp":
      options = _.merge(options, {
        sp: chart.sp,
        period: getPeriod(chart.periodDate, chart.periodType), // why is this needed??
        periodFrom: this.props.chart.periodFrom.format("YYYY-MM-DD"),
        periodTo: this.props.chart.periodTo.format("YYYY-MM-DD"),
        dataCallbacks: [function(data) {
          const height = Math.min(data.entities[0].records.reduce(function(prevValue, currentValue) {
            return Math.max(prevValue, currentValue.logins * 10);
          }, 300), 800);
          setMinimumHeightOfChart(height);
        }]
      });
      break;
    }

    this.chart = new SurfCharts(
      chartId,
      this.props.chart.type,
      this.context.currentUser.statsToken,
      options
    );
  }

  renderError() {
    if (this.state.error) {
      return <span dangerouslySetInnerHTML={{ __html: I18n.t("application_usage_panel.error_html") }}/>;
    }
  }

  render() {
    return (
      <div className="body">
        {this.renderError()}
        <div className="chart-container">
          <div id="chart" className="chart" ref={(chart) => this.chart = chart }/>
        </div>
      </div>
    );
  }
}

Chart.contextTypes = {
  currentUser: React.PropTypes.object
};

export default Chart;
