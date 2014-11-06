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
            {this.renderChartContainer()}
          </div>
        </div>
      </div>
    );
  },

  dataURL: function(params) {
    return App.apiUrl("/stats/id/:id", $.extend({ id: this.props.app.id }, params));
  },

  downloadURL: function(params) {
    return App.apiUrl("/stats/id/:id/download", $.extend({
      id: this.props.app.id,
      idpEntityId: App.currentIdpId()
    }, params));
  }
});
