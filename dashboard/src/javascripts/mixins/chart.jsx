/** @jsx React.DOM */

App.Mixins.Chart = {
  getInitialState: function () {
    return {
      sps: [],
      error: false
    }
  },

  shouldComponentUpdate: function (nextProps, nextState) {
    return this.state != nextState;
  },

  componentWillMount: function () {
    this.subscribeToken = App.PubSub.subscribe('idpSwitched', function(idp) {
      var newState = React.addons.update(this.state, {
        error: {$set: false},
        sps: {$set: []},
        chart: {
          idp: {$set: null},
          type: {$set: 'idpspbar'},
          sp: {$set: null}
        }
      });
      this.setState(newState);
      this.loadIdpData();
    }.bind(this));
  },

  componentDidMount: function () {
    this.loadIdpData();
  },

  loadIdpData: function () {
    var loadSps = function () {
      this.retrieveSps(function (sps) {
        var newSps = sps.map(function (sp) {
          return {display: sp.name, value: sp.id};
        });
        var newState = React.addons.update(this.state, {
          sps: {$set: newSps}
        });
        this.setState(newState);
      }.bind(this));
    }.bind(this);

    this.retrieveIdp(function (idp) {
      var newState = React.addons.update(this.state, {
        chart: {idp: {$set: idp.id}}
      });
      this.setState(newState);
      loadSps();
    }.bind(this));
  },

  retrieveIdp: function (callback) {
    $.get(
      "https://" + STATS_HOST + "/api/v1/entity/idp.json",
      {
        "entityid": App.currentIdp().id,
        "institution": App.currentIdp().institutionId,
        "state": "PA",
        "access_token": App.currentUser.statsToken
      }
    ).done(function (data) {
      if (data.records[0]) {
        callback(data.records[0]);
      } else {
        console.log("Could not find ", App.currentIdp());
        this.handleError();
      }
    }.bind(this)).fail(this.handleError);
  },

  retrieveSp: function (entityId, callback) {
    $.get(
      "https://" + STATS_HOST + "/api/v1/entity/sp.json",
      {
        "entityid": entityId,
        "active": 1,
        "state": "PA",
        "access_token": App.currentUser.statsToken
      }
    ).done(function (data) {
      if (data.records[0]) {
        callback(data.records[0]);
      } else {
        console.log("Could not find entityId", entityId);
        this.handleError();
      }
    }.bind(this)).fail(this.handleError);
  },

  retrieveSps: function (callback) {
    $.get(
      "https://" + STATS_HOST + "/api/v1/active/idp/" + this.state.chart.idp,
      {
        "access_token": App.currentUser.statsToken
      }
    ).done(function (data) {
      callback(data.entities.filter(function (sp) {
        return sp.name;
      }).sort(function (a, b) {
        return a.name.localeCompare(b.name);
      }));
    }).fail(this.handleError);
  },

  componentDidUpdate: function (prevProps, prevState) {
    if (this.state.chart === prevState.chart) {
      return;
    }
    if (!this.state.chart.idp) {
      return;
    }
    if (this.state.chart.type === 'idpsp' && !this.state.chart.sp) {
      return;
    }

    var chartId = this.refs.chart.getDOMNode().id;
    var setMinimumHeightOfChart = function (height) {
      $("#" + chartId).css('min-height', height + 'px');
    };
    var options = {
      idp: this.state.chart.idp,
      imagePath: "https://" + STATS_HOST + "/api/js/graphs-v1/images/amcharts/"
    };

    switch (this.state.chart.type) {
      case 'idpspbar':
        options = $.extend(options, {
          period: this.getPeriod(),
          dataCallbacks: [function(data) {
            var height = Math.max(data.numRecords * 25, 300);
            setMinimumHeightOfChart(height);
          }]
        });
        break;
      case 'idpsp':
        options = $.extend(options, {
          sp: this.state.chart.sp,
          period: this.getPeriod(), // why is this needed??
          periodFrom: this.state.chart.periodFrom.format("YYYY-MM-DD"),
          periodTo: this.state.chart.periodTo.format("YYYY-MM-DD"),
          dataCallbacks: [function(data) {
            var height = Math.min(data.entities[0].records.reduce(function(prevValue, currentValue) {
              return Math.max(prevValue, currentValue.logins * 10);
            }, 300), 800);
            setMinimumHeightOfChart(height);
          }]
        });
        break;
    }

    this.chart = new SurfCharts(
      chartId,
      this.state.chart.type,
      App.currentUser.statsToken,
      options
    );
  },

  getPeriod: function () {
    var moment = this.state.chart.periodDate;
    switch (this.state.chart.periodType) {
      case 'y':
        return moment.year();
      case 'q':
        return moment.year() + 'q' + moment.quarter();
      case 'm':
        return moment.year() + 'm' + (moment.month() + 1);
      case 'w':
        return moment.year() + 'w' + moment.week();
      case 'd':
        return moment.year() + 'd' + moment.dayOfYear();
    }
  },

  handleError: function () {
    this.setState({error: true});
  },

  componentWillUnmount: function () {
    App.PubSub.unsubscribe(this.subscribeToken);
    this.chart = null;
  },

  renderError: function () {
    if (this.state.error) {
      return <span dangerouslySetInnerHTML={{ __html: I18n.t("application_usage_panel.error_html") }}/>;
    }
  },

  renderChartTypeSelect: function () {
    var options = [
      {display: I18n.t('stats.chart.type.idpspbar'), value: 'idpspbar'},
      {display: I18n.t('stats.chart.type.idpsp'), value: 'idpsp'}
    ];

    var handleChange = function(value) {
      var newState = React.addons.update(this.state, {
        chart: {"type": {$set: value}}
      });
      if (value === 'idpsp' && !this.state.chart.sp && this.state.sps.length > 0) {
        newState = React.addons.update(newState, {
          chart: {sp: {$set: this.state.sps[0].value}}
        });
      }
      this.setState(newState);
    }.bind(this);

    return (
      <div>
        <fieldset>
          <h2>{I18n.t('stats.chart.type.name')}</h2>
          <App.Components.Select2Selector
            defaultValue={this.state.chart.type}
            select2selectorId='chart-type'
            options={options}
            multiple={false}
            handleChange={handleChange} />
        </fieldset>
        {this.renderSpSelect()}
      </div>
    );
  },

  renderSpSelect: function () {
    if (this.state.chart.type !== 'idpsp') {
      return;
    }
    var handleChange = function (sp) {
      var newState = React.addons.update(this.state, {
        chart: {sp: {$set: sp}}
      });
      this.setState(newState);
    }.bind(this);

    return (
      <fieldset>
        <h2>{I18n.t('stats.chart.sp.name')}</h2>
        <App.Components.Select2Selector
          defaultValue={this.state.chart.sp}
          select2selectorId='sp'
          options={this.state.sps}
          multiple={false}
          handleChange={handleChange} />
      </fieldset>
    );
  },

  renderPeriodSelect: function () {
    if (this.state.chart.type === 'idpsp') {
      var handleChangePeriodFrom = function (moment) {
        var newState = React.addons.update(this.state, {
          chart: {periodFrom: {$set: moment}}
        });
        this.setState(newState);
      }.bind(this);

      var handleChangePeriodTo = function (moment) {
        var newState = React.addons.update(this.state, {
          chart: {periodTo: {$set: moment}}
        });
        this.setState(newState);
      }.bind(this);

      return (
        <div>
          <App.Components.Period
            initialDate={this.state.chart.periodFrom}
            title={I18n.t('stats.chart.periodFrom.name')}
            handleChange={handleChangePeriodFrom} />
          <App.Components.Period
            initialDate={this.state.chart.periodTo}
            title={I18n.t('stats.chart.periodTo.name')}
            handleChange={handleChangePeriodTo} />
        </div>
      );
    } else {
      var handleChange = function (moment) {
        var newState = React.addons.update(this.state, {
          chart: {periodDate: {$set: moment}}
        });
        this.setState(newState);
      }.bind(this);

      return (
        <div>
          {this.renderPeriodTypeSelect()}
          <App.Components.Period
            initialDate={this.state.chart.periodDate}
            title={I18n.t('stats.chart.periodDate.name')}
            handleChange={handleChange} />
        </div>
      );
    }
  },

  renderPeriodTypeSelect: function () {
    var options = [
      {display: I18n.t('stats.chart.period.day'), value: 'd'},
      {display: I18n.t('stats.chart.period.week'), value: 'w'},
      {display: I18n.t('stats.chart.period.month'), value: 'm'},
      {display: I18n.t('stats.chart.period.quarter'), value: 'q'},
      {display: I18n.t('stats.chart.period.year'), value: 'y'}
    ];

    var handleChange = function(value) {
      var newState = React.addons.update(this.state, {
        chart: {periodType: {$set: value}}
      });
      this.setState(newState)
    }.bind(this);

    return (
      <fieldset>
        <h2>{I18n.t('stats.chart.period.name')}</h2>
        <App.Components.Select2Selector
          value={this.state.chart.periodType}
          select2selectorId='period-type'
          options={options}
          handleChange={handleChange} />
      </fieldset>
    );
  },

  downloadIdpSpCsvFile: function (callback) {
    var url = "https://" + STATS_HOST + "/api/v1/idpsplogins/" + this.state.chart.idp + "/" + this.state.chart.sp + "/d.csv";
    window.open(url + "?access_token=" + App.currentUser.statsToken + "&from=" + this.state.chart.periodFrom.format("YYYY-MM-DD") + "&to=" + this.state.chart.periodTo.format("YYYY-MM-DD"));
  },

  downloadIdpSpBarCsvFile: function () {
    var url = "https://" + STATS_HOST + "/api/v1/splogins/" + this.state.chart.idp + ".csv";
    window.open(url + "?access_token=" + App.currentUser.statsToken + "&period=" + this.getPeriod());
  },

  renderDownload: function () {
    if (this.state.chart.type === "idpspbar" && this.state.chart.idp) {
      return (
          <App.Components.DownloadButton
            genFile={this.downloadIdpSpBarCsvFile}
            title={I18n.t("application_usage_panel.download")}
            fileName="splogins.csv"
            mimeType="text/csv"
            className="download-button c-button" />
      );
    } else if (this.state.chart.type === "idpsp" && this.state.chart.sp && this.state.chart.idp) {
      return (
          <App.Components.DownloadButton
            genFile={this.downloadIdpSpCsvFile}
            title={I18n.t("application_usage_panel.download")}
            fileName="idpsplogins.csv"
            mimeType="text/csv"
            className="download-button c-button" />
      );
    }
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
