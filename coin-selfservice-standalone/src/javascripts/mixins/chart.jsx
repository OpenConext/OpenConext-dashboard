/** @jsx React.DOM */

App.Mixins.Chart = {
  chartHeight: 400,

  getInitialState: function() {
    return {
      period: "last_three_months",
      error: false
    }
  },

  shouldComponentUpdate: function(nextProps, nextState) {
    // only rerender when the period or error state is changed
    return nextState.period != this.state.period || nextState.error != this.state.error;
  },

  componentDidMount: function() {
    $(window).on("resize", this.resizeGraph);
    this.initChart();
  },

  componentWillUnmount: function() {
    $(window).off("resize", this.resizeGraph);
    this.chart = null;
  },

  componentDidUpdate: function() {
    if (!this.state.error) {
      this.chart.dataURL = this.getDataURL();
      this.chart.request();
    }
  },

  renderError: function() {
    if (this.state.error) {
      return <span dangerouslySetInnerHTML={{ __html: I18n.t("application_usage_panel.error_html") }} />;
    }
  },

  renderPeriodSelect: function() {
    return (
      <select valueLink={this.linkState("period")}>
        {this.renderOption("last_week")}
        {this.renderOption("last_month")}
        {this.renderOption("last_three_months")}
        {this.renderOption("last_year")}
      </select>
    );
  },

  renderDownloadButton: function() {
    return (
      <a href={this.getDownloadURL()} className="c-button" target="_blank">{I18n.t("application_usage_panel.download")}</a>
    );
  },

  renderChartContainer: function() {
    return (
      <div className="body">
        {this.renderError()}
        <div className="chart-container">
          <div className="y" ref="y" />
          <div className="chart" ref="chart" />
        </div>
      </div>
    );
  },

  renderOption: function(key) {
    return <option key={key} value={key}>{I18n.t("application_usage_panel." + key)}</option>;
  },

  getDataURL: function() {
    return this.dataURL(this.params());
  },

  getDownloadURL: function() {
    return this.downloadURL(this.params());
  },

  params: function() {
    return {
      start: I18n.strftime(this.startDateForPeriod(this.state.period), "%Y%m%d"),
      end: I18n.strftime(new Date(), "%Y%m%d")
    };
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
    this.chart.graph.configure({
      width: $(this.refs.chart.getDOMNode()).width(),
      height: this.chartHeight
    });
    this.chart.graph.render();
  },

  initChart: function() {
    var data = [ { x: 0, y: 40 }, { x: 1, y: 49 }, { x: 2, y: 17 }, { x: 3, y: 42 } ];

    var self = this;

    this.chart = new Rickshaw.Graph.Ajax({
      element: this.refs.chart.getDOMNode(),
      width: $(this.refs.chart.getDOMNode()).width(),
      height: this.chartHeight,
      renderer: "line",
      dataURL: this.getDataURL(),
      onError: function() {
        self.setState({error: true});
      },
      onData: function(d) {
        var series = [];

        for (index in d.payload) {
          var service = d.payload[index];
          var data = [];

          for (timestamp in service.graphData) {
            data.push({x: parseInt(timestamp), y: service.graphData[timestamp]});
          }

          series.push({
            color: self.stringToColour(service.spEntityId),
            name: service.spName,
            data: data
          });
        }

        return series;
      },
      onComplete: function(transport) {
        var graph = transport.graph;

        if (!self.hoverDetail) {
          self.hoverDetail = new Rickshaw.Graph.HoverDetail({
            graph: graph,
            formatter: function(series, x, y) {
              var date = '<span class="date">' + I18n.strftime(new Date(x * 1000), "%d %B %Y") + '</span>';
              var content = I18n.t("stats.logins_for", { service: series.name }) + ": " + parseInt(y) + '<br>' + date;
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
            element: self.refs.y.getDOMNode()
          });
        }

        if (self.refs.legend) {
          if (!self.legend) {
            self.legend = new Rickshaw.Graph.Legend({
              graph: graph,
              element: self.refs.legend.getDOMNode()
            });
          }

          if (!self.highlight) {
            self.highlight = new Rickshaw.Graph.Behavior.Series.Highlight({
              graph: graph,
              legend: self.legend
            });
          }

          if (!self.toggle) {
            self.toggle = new Rickshaw.Graph.Behavior.Series.Toggle({
              graph: graph,
              legend: self.legend
            });
          }
        }

        graph.update();
      }
    });
  },

  stringToColour: function(str) {
    var hash = 0;
    for (var i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    var colour = '#';
    for (var i = 0; i < 3; i++) {
      var value = (hash >> (i * 8)) & 0xFF;
      colour += ('00' + value.toString(16)).substr(-2);
    }
    return colour;
  }
}
