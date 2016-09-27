import React from 'react';
import I18n from "../lib/i18n";
import $ from "jquery";

import { STATS_HOST } from "../api/stats";

class Chart extends React.Component {
  constructor() {
    super();
    this.state = {
      error: null
    }
  }

  componentDidUpdate(prevProps, prevState) {
    if (this.props.chart === prevProps.chart) {
      return;
    }
    if (!this.props.chart.idp) {
      return;
    }
    if (this.props.chart.type === 'idpsp' && !this.props.chart.sp) {
      return;
    }

    var chartId = this.chart.id;
    var setMinimumHeightOfChart = function (height) {
      $("#" + chartId).css('min-height', height + 'px');
    };
    var options = {
      idp: this.props.chart.idp,
      imagePath: STATS_HOST + "/api/js/graphs-v1/images/amcharts/"
    };

    switch (this.props.chart.type) {
      case 'idpspbar':
        options = _.merge(options, {
          period: this.getPeriod(),
          dataCallbacks: [function(data) {
            var height = Math.max(data.numRecords * 25, 300);
            setMinimumHeightOfChart(height);
          }]
        });
        break;
      case 'idpsp':
        options = _.merge(options, {
          sp: this.props.chart.sp,
          period: this.getPeriod(), // why is this needed??
          periodFrom: this.props.chart.periodFrom.format("YYYY-MM-DD"),
          periodTo: this.props.chart.periodTo.format("YYYY-MM-DD"),
          dataCallbacks: [function(data) {
            var height = Math.min(data.entities[0].records.reduce(function(prevValue, currentValue) {
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

  getPeriod() {
    var moment = this.props.chart.periodDate;
    switch (this.props.chart.periodType) {
      case 'y':
        return moment.year();
      case 'q':
        return moment.year() + 'q' + moment.quarter();
      case 'm':
        return moment.year() + 'm' + (moment.month() + 1);
      case 'w':
        return moment.year() + 'w' + moment.week();
      case 'd':
        return moment.year() + 'd' + moment.dayOfYear();
    }
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
