/** @jsx React.DOM */

App.Components.ApplicationUsagePanel = React.createClass({
  mixins: [React.addons.LinkedStateMixin],

  chartHeight: 400,

  getInitialState: function() {
    return {
      period: "last_three_months"
    }
  },

  shouldComponentUpdate: function(nextProps, nextState) {
    // only rerender when the period state is changed
    return nextState.period != this.state.period;
  },

  componentDidMount: function() {
    $(window).on("resize", this.resizeGraph);
    this.initGraph();
  },

  componentWillUnmount: function() {
    $(window).off("resize", this.resizeGraph);
    this.graph = null;
  },

  componentDidUpdate: function() {
    this.graph.dataURL = this.dataURL();
    this.graph.request();
  },

  render: function() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("application_usage_panel.title")}</h1>
        </div>

        <div className="mod-usage">
          <div className="header">
            <h2>{I18n.t("application_usage_panel.description")}</h2>
            <a href={this.downloadURL()} className="c-button" target="_blank">{I18n.t("application_usage_panel.download")}</a>
            <select valueLink={this.linkState("period")}>
              {this.renderOption("last_week")}
              {this.renderOption("last_month")}
              {this.renderOption("last_three_months")}
              {this.renderOption("last_year")}
            </select>
          </div>
          <div className="body">
            <div className="chart-container">
              <div className="y" ref="y" />
              <div className="chart" ref="chart" />
            </div>
          </div>
        </div>
      </div>
    );
  },

  renderOption: function(key) {
    return <option key={key} value={key}>{I18n.t("application_usage_panel." + key)}</option>;
  },

  startDateForPeriod: function(period) {
    var date = new Date();

    switch (period) {
      case "last_week":
        date.setDate(date.getDate() - 7);
        break;
      case "last_month":
        date.setMonth(date.getMonth() - 1);
        break;
      case "last_three_months":
        date.setMonth(date.getMonth() - 3);
        break;
      case "last_year":
        date.setFullYear(date.getFullYear() - 1);
        break;
    }

    return date;
  },

  resizeGraph: function() {
    this.graph.configure({
      width: $(this.refs.chart.getDOMNode()).width(),
      height: this.chartHeight
    });
    this.graph.render();
  },

  dataURL: function() {
    return App.apiUrl("/stats/id/:id", {
      id: this.props.app.id,
      start: I18n.strftime(this.startDateForPeriod(this.state.period), "%Y%m%d"),
      end: I18n.strftime(new Date(), "%Y%m%d")
    });
  },

  downloadURL: function() {
    return App.apiUrl("/stats/id/:id/download", {
      id: this.props.app.id,
      idpEntityId: App.currentIdpId(),
      start: I18n.strftime(this.startDateForPeriod(this.state.period), "%Y%m%d"),
      end: I18n.strftime(new Date(), "%Y%m%d")
    });
  },

  initGraph: function() {
    var data = [ { x: 0, y: 40 }, { x: 1, y: 49 }, { x: 2, y: 17 }, { x: 3, y: 42 } ];

    var yAxis = this.refs.y.getDOMNode();
    var self = this;

    this.graph = new Rickshaw.Graph.Ajax({
      element: this.refs.chart.getDOMNode(),
      width: $(this.refs.chart.getDOMNode()).width(),
      height: this.chartHeight,
      renderer: "line",
      dataURL: this.dataURL(),
      onData: function(d) {
        var data = [];
        for (timestamp in d.payload) {
          data.push({x: parseInt(timestamp), y: d.payload[timestamp]});
        }

        return [{
          color: "#4DB3CF",
          name: I18n.t("application_usage_panel.logins"),
          data: data
        }];
      },
      onComplete: function(transport) {
        var graph = transport.graph;

        if (!self.hoverDetail) {
          self.hoverDetail = new Rickshaw.Graph.HoverDetail({
            graph: graph,
            formatter: function(series, x, y) {
              var date = '<span class="date">' + I18n.strftime(new Date(x * 1000), "%d %B %Y") + '</span>';
              var content = series.name + ": " + parseInt(y) + '<br>' + date;
              return content;
            }
          });
        }

        if (!self.xAxis) {
          self.xAxis = new Rickshaw.Graph.Axis.Time({
            graph: graph
          });
        }

        if (!self.yAxis) {
          self.yAxis = new Rickshaw.Graph.Axis.Y({
            graph: graph,
            orientation: 'right',
            tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
            element: yAxis
          });
        }

        graph.update();
      }
    });
  }
});
