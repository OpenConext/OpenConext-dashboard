import React from "react";
  // mixins: [
  //   React.addons.LinkedStateMixin,
  //   App.Mixins.Chart,
  // ],
class Stats extends React.Component {

  constructor() {
    super();
    this.state = {
      chart: {
        type: 'idpspbar',
        periodFrom: moment().subtract(1, 'months'),
        periodTo: moment().subtract(1, 'days'),
        periodType: 'm',
        periodDate: moment().subtract(1, 'days'),
      }
    }
  }

  render() {
    return (
      <div className="l-main">
        <div className="l-left">
          <div className="mod-filters">
            <div className="header">
              <h1>{I18n.t('stats.filters.name')}</h1>
              {this.renderDownload()}
            </div>
            <form>
              {this.renderChartTypeSelect()}
              {this.renderPeriodSelect()}
            </form>
          </div>
        </div>
        <div className="l-right">
          <div className="mod-chart">
            {this.renderChart()}
          </div>
        </div>
      </div>
    );
  }
}

export default Stats;
