/** @jsx React.DOM */

App.Components.ApplicationUsagePanel = React.createClass({
  mixins: [
    React.addons.LinkedStateMixin,
    App.Mixins.Chart,
  ],

  render: function() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("application_usage_panel.title")}</h1>
        </div>

        <div className="mod-usage">
          <div className="mod-usage">
            <div className="header">
              {this.renderTitle()}
              <div className="options">
                {this.renderPeriodSelect()}
                {this.renderDownloadButton()}
              </div>
            </div>
            {this.renderChart()}
          </div>
        </div>
      </div>
    );
  }
});
