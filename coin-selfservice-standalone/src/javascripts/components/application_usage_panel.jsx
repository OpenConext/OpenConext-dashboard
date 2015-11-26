/** @jsx React.DOM */

App.Components.ApplicationUsagePanel = React.createClass({
  mixins: [
    React.addons.LinkedStateMixin,
    App.Mixins.Chart,
  ],

  getInitialState: function() {
    return {
      chart: {
        type: 'idpsp',
        periodFrom: moment().subtract(1, 'months'),
        periodTo: moment(),
        periodType: 'm',
        periodDate: moment(),
      }
    }
  },

  componentDidMount: function () {
    this.retrieveSp(this.props.app.spEntityId, function(sp) {
      var newState = React.addons.update(this.state, {
        chart: {sp: {$set: sp.id}}
      });
      this.setState(newState);
    }.bind(this));
  },

  render: function() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("application_usage_panel.title")}</h1>
        </div>

        <div className="mod-usage">
          <div className="mod-usage">
            <div className="header">
              <div className="options">
              </div>
            </div>
            {this.renderChart()}
          </div>
        </div>
      </div>
    );
  }
});
