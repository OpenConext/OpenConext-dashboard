import React from "react";
  // mixins: [
  //   React.addons.LinkedStateMixin,
  //   App.Mixins.Chart,
  // ],
class ApplicationUsagePanel extends React.Component {
  constructor() {
    super();

    this.state = {
      chart: {
        type: 'idpsp',
        periodFrom: moment().subtract(1, 'months'),
        periodTo: moment(),
        periodType: 'm',
        periodDate: moment(),
      }
    }
  }

  componentDidMount() {
    this.retrieveSp(this.props.app.spEntityId, function(sp) {
      var newState = React.addons.update(this.state, {
        chart: {sp: {$set: sp.id}}
      });
      this.setState(newState);
    }.bind(this));
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
                {this.renderDownload()}
                {this.renderPeriodSelect()}
              </div>
            </div>
            {this.renderChart()}
          </div>
        </div>
      </div>
    );
  }
}

export default ApplicationUsagePanel;
