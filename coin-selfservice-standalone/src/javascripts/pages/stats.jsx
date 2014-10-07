/** @jsx React.DOM */

App.Pages.Stats = React.createClass({
  mixins: [
    React.addons.LinkedStateMixin,
    App.Mixins.Chart,
  ],

  render: function() {
    return (
      <div className="l-main">
        <div className="l-left">
          <div className="mod-legend" ref="legend">
            <h1>{I18n.t("stats.legend")}</h1>
          </div>
        </div>
        <div className="l-right">
          <div className="mod-usage">
            <div className="header">
              <h2>{I18n.t("application_usage_panel.description")}</h2>
              {this.renderDownloadButton()}
              {this.renderPeriodSelect()}
            </div>
            {this.renderChartContainer()}
          </div>
        </div>
      </div>
    );
  },

  dataURL: function(params) {
    return App.apiUrl("/stats", params);
  },

  downloadURL: function(params) {
    return App.apiUrl("/stats/download", $.extend({ idpEntityId: App.currentIdpId() }, params));
  }
});
