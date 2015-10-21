/** @jsx React.DOM */

App.Mixins.Chart = {
  getInitialState: function () {
    return {
      error: false
    }
  },

  shouldComponentUpdate: function (nextProps, nextState) {
    return this.state != nextState;
  },

  componentDidMount: function () {
    var self = this;
    $.get(
      "https://" + STATS_HOST + "/api/v1/entity/idp.json",
      {
        "entityid": App.currentIdp().id,
        "institution": App.currentIdp().institutionId,
        "access_token": App.currentUser.statsToken
      }
    ).done(
      function (data) {
        var idp = data.records.find(function (element) {
          return element.state === 'PA'
        }).id;
        if (this.isMounted()) {
          var newState = React.addons.update(this.state, {
            chart: {idp: {$set: idp}}
          });
          this.setState(newState);
        }

      }.bind(this)
    ).fail(this.handleError);
  },

  componentDidUpdate: function (prevProps, prevState) {
    if (this.state.chart.idp !== prevState.chart.idp) {
      var chartId = this.refs.chart.getDOMNode().id;
      this.chart = new SurfCharts(
        chartId,
        'idpbar',
        App.currentUser.statsToken,
        {
          idp: this.state.chart.idp,
          periodFrom: '2015-01-01',
          periodTo: '2015-10-19',
          periodType: 'd',
          period: "2015d293",
          imagePath: "https://" + STATS_HOST + "/api/js/graphs-v1/images/amcharts/",
          dataCallbacks: [function(data) {
            var height = Math.max(300, data.entities.length * 25);
            console.log(height);
            $("#" + chartId).css('min-height', height + 'px');
          }]

        }
      );
    }

  },

  handleError: function () {
    this.setState({error: true});
  },

  componentWillUnmount: function () {
    //this.chart.destroy();
    this.chart = null;
  },

  renderError: function () {
    if (this.state.error) {
      return <span dangerouslySetInnerHTML={{ __html: I18n.t("application_usage_panel.error_html") }}/>;
    }
  },

  renderLegend: function () {
    return (
      <div ref="legend"/>
    );
  },

  renderPeriodSelect: function () {
    return (
      <div ref="period"/>
    );
  },

  renderDownloadButton: function () {
    return (
      <div ref="download"/>
    );
  },

  renderTitle: function () {
    return (
      <div ref="title"/>
    );
  },

  renderChart: function () {
    return (
      <div className="body">
        {this.renderError()}
        <div className="chart-container">
          <div id="chart" className="chart" ref="chart"/>
        </div>
      </div>
    );
  },
}
