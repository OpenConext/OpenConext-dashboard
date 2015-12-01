/** @jsx React.DOM */

App.Pages.Stats = React.createClass({
  mixins: [
    React.addons.LinkedStateMixin,
    App.Mixins.Chart,
  ],

  getInitialState: function () {
    return {
      chart: {
        type: 'idpspbar',
        periodFrom: moment().subtract(1, 'months'),
        periodTo: moment().subtract(1, 'days'),
        periodType: 'm',
        periodDate: moment().subtract(1, 'days'),
      }
    }
  },

  render: function () {
    return (
      <div className="l-main">
        <div className="l-left">
          <div className="mod-filters">
            <div className="header">
              <h1>{I18n.t('stats.filters.name')}</h1>
            </div>
            {this.renderChartTypeSelect()}
            {this.renderPeriodSelect()}
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
});

