/** @jsx React.DOM */

App.Pages.Stats = React.createClass({
  mixins: [
    React.addons.LinkedStateMixin,
    App.Mixins.Chart,
  ],

  getInitialState: function () {
    return {
      chart: {
        spId: "*"
      }
    }
  },

  render: function () {
    return (
      <div className="l-mini">
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
    );
  }
});

