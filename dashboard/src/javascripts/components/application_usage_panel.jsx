import React from "react";

import I18n from "i18n-js";
import moment from "moment";

import { AppShape } from "../shapes";
import { apiUrl, retrieveIdp, retrieveSp, retrieveSps } from "../api/stats";
import Period from "./period";
import DownloadButton from "./download_button";
import Chart from "./chart";

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

  componentWillMount() {
    const { currentUser } = this.context;

    retrieveSp(this.props.app.spEntityId, currentUser.statsToken).then(sp => {
      this.setState({ chart: { ...this.state.chart, sp: sp.id } });
    })
    .catch(() => this.setState({ chart: { ...this.state.chart, error: true } }));

    retrieveIdp(currentUser.getCurrentIdp().id, currentUser.getCurrentIdp().institutionId, currentUser.statsToken).then(idp => {
      this.setState({ chart: { ...this.state.chart, idp: idp.id } });

      retrieveSps(idp.id, currentUser.statsToken).then(sps => {
        const newSps = sps.map(sp => {
          return { display: sp.name, value: sp.id };
        });

        this.setState({ chart: { ...this.state.chart, sps: newSps } });
      });
    })
    .catch(() => this.setState({ chart: { ...this.state.chart, error: true } }));
  }

  downloadIdpSpCsvFile() {
    const { chart } = this.state;
    const { currentUser } = this.context;
    const url = apiUrl(`/idpsplogins/${chart.idp}/${chart.sp}/d.csv`);

    window.open(`${url}?access_token=${currentUser.statsToken}&from=${chart.periodFrom.format("YYYY-MM-DD")}&to=${chart.periodTo.format("YYYY-MM-DD")}`);
  }

  renderDownload() {
    const { chart } = this.state;

    if (chart.sp && chart.idp)  {
      return (
        <DownloadButton
          genFile={() => this.downloadIdpSpCsvFile()}
          title={I18n.t("application_usage_panel.download")}
          fileName="idpsplogins.csv"
          mimeType="text/csv"
          className="download-button c-button"
        />
      );
    }

    return null;
  }

  renderPeriodSelect() {
    const handleChangePeriodFrom = moment => {
      this.setState({ chart: { ...this.state.chart, periodFrom: moment } });
    };
    const handleChangePeriodTo = moment => {
      this.setState({ chart: { ...this.state.chart, periodTo: moment } });
    };

    return (
      <div>
        <Period
          initialDate={this.state.chart.periodFrom}
          title={I18n.t("stats.chart.periodFrom.name")}
          handleChange={handleChangePeriodFrom} />
        <Period
          initialDate={this.state.chart.periodTo}
          title={I18n.t("stats.chart.periodTo.name")}
          handleChange={handleChangePeriodTo} />
      </div>
    );
  }

  render() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("application_usage_panel.title")}</h1>
        </div>
        <div className="mod-usage">
          <div className="mod-usage">
            <div className="header">
              <div className="options">
                { this.renderDownload() }
                {this.renderPeriodSelect()}
              </div>
            </div>
            <Chart chart={this.state.chart} />
          </div>
        </div>
      </div>
    );
  }
}

ApplicationUsagePanel.contextTypes = {
  currentUser: React.PropTypes.object
};

ApplicationUsagePanel.propTypes = {
  app: AppShape.isRequired
};

export default ApplicationUsagePanel;
