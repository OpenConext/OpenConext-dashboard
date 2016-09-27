import React from "react";
import moment from "moment";
import I18n from "../lib/i18n";

import { apiUrl, retrieveIdp, retrieveSps } from "../api/stats";
import { getPeriod } from "../utils/period";

import DownloadButton from "../components/download_button";
import Select2Selector from "../components/select2_selector";

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
  componentWillMount() {
    const { currentUser } = this.context;
    retrieveIdp(currentUser.getCurrentIdp().id, currentUser.getCurrentIdp().institutionId, currentUser.statsToken).then(idp => {
      this.setState({ chart: { ...this.state.chart, idp: idp.id }});

      retrieveSps(idp.id, currentUser.statsToken).then(sps => {
        var newSps = sps.map(function (sp) {
          return {display: sp.name, value: sp.id};
        });

        this.setState({ chart: { ...this.state.chart, sps: newSps }});
      });
    });
  }

  downloadIdpSpBarCsvFile() {
    const { chart } = this.state;
    const { currentUser } = this.context;
    const url = apiUrl(`/splogins/${chart.idp}.csv`);
    const period = getPeriod(chart.periodDate, chart.periodType);

    window.open(`${url}?access_token=${currentUser.statsToken}&period=${period}`);
  }

  renderDownload() {
    return (
      <DownloadButton
        genFile={() => this.downloadIdpSpBarCsvFile()}
        title={I18n.t("application_usage_panel.download")}
        fileName="splogins.csv"
        mimeType="text/csv"
        className="download-button c-button"
      />
    );
  }

  renderChartTypeSelect() {
    var options = [
      {display: I18n.t('stats.chart.type.idpspbar'), value: 'idpspbar'},
      {display: I18n.t('stats.chart.type.idpsp'), value: 'idpsp'}
    ];

    var handleChange = (value) => {
      this.setState({ chart: { ...this.state.chart, type: value }});
      if (value === 'idpsp' && !this.state.chart.sp && this.state.sps.length > 0) {
        this.setState({ chart: { ...this.state.chart, sp: this.state.sps[0].value }});
      }
    };

    return (
      <div>
        <fieldset>
          <h2>{I18n.t('stats.chart.type.name')}</h2>
          <Select2Selector
            defaultValue={this.state.chart.type}
            select2selectorId='chart-type'
            options={options}
            multiple={false}
            handleChange={handleChange.bind(this)} />
        </fieldset>
        {this.renderSpSelect()}
      </div>
    );
  }

  renderSpSelect() {
    if (this.state.chart.type !== 'idpsp') {
      return;
    }
    const handleChange = (sp) => {
      this.setState({ chart: { ...this.state.chart, sp: sp }});
    };

    return (
      <fieldset>
        <h2>{I18n.t('stats.chart.sp.name')}</h2>
        <Select2Selector
          defaultValue={this.state.chart.sp}
          select2selectorId='sp'
          options={this.state.sps}
          multiple={false}
          handleChange={handleChange.bind(this)} />
      </fieldset>
    );
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
              {/* {this.renderPeriodSelect()} */}
            </form>
          </div>
        </div>
        <div className="l-right">
          <div className="mod-chart">
            {/* {this.renderChart()} */}
          </div>
        </div>
      </div>
    );
  }
}

Stats.contextTypes = {
  currentUser: React.PropTypes.object
};

export default Stats;
