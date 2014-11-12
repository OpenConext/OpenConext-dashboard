;(function(window, $){
  "use strict";

  function SurfChart(args) {
    if (!(this instanceof SurfChart)){
      return new SurfChart(args);
    }

    function _format(subject) {
      var str = subject, len = arguments.length+1;

      // For each {0} {1} {n...} replace with the argument in that position.  If
      // the argument is an object or an array it will be stringified to JSON.
      for (var i=0, arg; i < len; arg = arguments[i++ + 1]) {
        var safe = typeof arg === 'object' ? JSON.stringify(arg) : arg;
        str = str.replace(RegExp('\\{'+(i-1)+'\\}', 'g'), safe);
      }
      return str;
    }

    var today = new Date();
    var Defaults = {
      lang: "en",
      height: 400,
      autoresize: true,
      renderer: "line",
      hoverDetail: true,
      xAxis: true,
      yAxis: true,
      legendHighlight: true,
      legendToggle: true,
      title: true,
      downloadURL: true,
      periodType: "year",
      periodYear: today.getFullYear(),
      periodNum: today.getMonth(),
      dataURLFormat: "https://stats.surfconext.nl/api/v1/logins/sp/{0}/{1}.json?from={2}&to={3}",

      hoverFormatter: _hoverFormatter,

      onError: _onError,
      onData: _onData,
      onComplete: _onComplete,
      onLoad: $.noop,
    };

    var Text = {
      nl: {
        show: "Selectie:",
        month: "Maand",
        quarter: "Kwartaal",
        year: "Jaar",
        last: "Afgelopen",
        download: "Download",
        months: {
          1: "Januari",
          2: "Februari",
          3: "Maart",
          4: "April",
          5: "Mei",
          6: "Juni",
          7: "Juli",
          8: "Augustus",
          9: "September",
          10: "Oktober",
          11: "November",
          12: "December",
        },
        titles: {
          "logins/sp/month": "Logins per maand"
        }
      },
      en: {
        show: "Show:",
        month: "Month",
        quarter: "Quarter",
        year: "Year",
        last: "Last",
        download: "Download",
        months: {
          1: "January",
          2: "February",
          3: "March",
          4: "April",
          5: "May",
          6: "June",
          7: "July",
          8: "August",
          9: "September",
          10: "October",
          11: "November",
          12: "December",
        },
        titles: {
          "logins/sp/month": "Logins per month"
        }
      }
    };

    var $numSelect;
    var $typeSelect;
    var $yearSelect;
    var initialized = false;
    var options = $.extend({}, Defaults, args);
    options.element = options.chartElement || options.element; // alias option
    options.spId = [].concat(options.spId);
    options.dataURL = options.dataURL || _dataURL(); // build data url if none defined

    function _dataURL() {
      var period = _currentDateRange();
      return _format(options.dataURLFormat, options.spId.join(","), options.periodType == "month" ? "day" : "week", period.from, period.to);
    };

    function _currentDateRange() {
      var from, to;
      switch (options.periodType) {
        case "month":
          from = new Date(options.periodYear, options.periodNum - 1, 1);
          to = new Date(options.periodYear, options.periodNum, 1);
          break;
        case "quarter":
          from = new Date(options.periodYear, options.periodNum * 3 - 3, 1);
          to = new Date(from.getTime());
          to.setMonth(to.getMonth() + 3);
          break;
        case "year":
          from = new Date(options.periodYear, 0, 1);
          to = new Date(parseInt(options.periodYear) + 1, 0, 1);
          break;
      };

      return {
        from: _formatDate(from),
        to: _formatDate(to)
      }
    };

    function _formatDate(date) {
      if (date) {
        return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
      } else {
        return "";
      };
    };

    function _stringToColour(str) {
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
    };

    function _onData(d) {
      var series = [];

      if (options.title === true) {
        options.title = _i18n("titles." + d.request.type);
      }

      if (options.downloadURL === true) {
        options.downloadURL = d.request.urlCsv;
      }

      for (var index in d.entities) {
        var entity = d.entities[index];
        var data = [];

        for (var recordIndex in entity.records) {
          var record = entity.records[recordIndex];
          data.push({x: record.period.startTimestamp, y: record.logins});
        }

        series.push({
          color: _stringToColour(entity.entityId),
          name: entity.name,
          data: data
        });
      }

      return series;
    };

    function _onError() {
      console.error("Couldn't render SurfChart");
    };

    function _onComplete(graph) {
      if (!initialized) {
        if (options.hoverDetail) _initHoverDetail(graph);
        if (options.xAxis) _initXAxis(graph);
        if (options.yAxis) _initYAxis(graph);
        if (options.periodElement) _initPeriodSelect(graph);
        if (options.downloadElement && options.downloadURL) _initDownloadButton();
        if (options.titleElement && options.title) _initTitle();

        if (options.legendElement) {
          var legend = _initLegend(graph);
          if (options.legendHighlight) _initLegendHighlight(graph, legend);
          if (options.legendToggle) _initLegendToggle(graph, legend);
        }

        if (options.onLoad) options.onLoad(graph);

        initialized = true;
      }

      graph.graph.update();
    };

    function _initPeriodSelect(graph) {
      var $label = $("<span class=\"surf_chart period_select show\"/>").text(_i18n("show"));

      $typeSelect = $("<select class=\"surf_chart period_select type_select\" />").
        append(new Option(_i18n("month"), "month")).
        append(new Option(_i18n("quarter"), "quarter")).
        append(new Option(_i18n("year"), "year")).
        on("change", _renderNumberOptions).
        on("change", _refreshGraph(graph));

      $yearSelect = $("<select class=\"surf_chart period_select year_select\" />").
        on("change", _refreshGraph(graph));
      $numSelect = $("<select class=\"surf_chart period_select num_select\" />").
        on("change", _refreshGraph(graph));

      $typeSelect.val(options.periodType);

      _renderNumberOptions();

      $yearSelect.val(options.periodYear);
      $numSelect.val(options.periodNum);

      $(options.periodElement).
        append($label).
        append($typeSelect).
        append($yearSelect).
        append($numSelect);
    };

    function _renderNumberOptions() {
      var numOptions = [];
      var yearOptions = [];

      var year = new Date().getFullYear();
      yearOptions.push(new Option(year, year));
      yearOptions.push(new Option(year - 1, year - 1));
      yearOptions.push(new Option(year - 2, year - 2));

      switch ($typeSelect.val()) {
        case "month":
          for (var month in Text[options.lang]["months"]) {
            var name = Text[options.lang]["months"][month];
            numOptions.push(new Option(name, month));
          }
          $numSelect.show();
          break;
        case "quarter":
          numOptions.push(new Option("Q1", "1"));
          numOptions.push(new Option("Q2", "2"));
          numOptions.push(new Option("Q3", "3"));
          numOptions.push(new Option("Q4", "4"));
          $numSelect.show();
          break;
        case "year":
          $numSelect.hide();
          break;
      }

      $numSelect.empty();
      $yearSelect.empty();
      $numSelect.append(numOptions);
      $yearSelect.append(yearOptions);
    };

    function _refreshGraph(graph) {
      return function() {
        options.periodType = $typeSelect.val();
        options.periodYear = $yearSelect.val();
        options.periodNum = $numSelect.val();

        graph.dataURL = _dataURL();
        graph.request();
      };
    };

    function _initDownloadButton() {
      var $downloadButton = $("<a href=\"#\" class=\"surf_chart download\" target=\"_blank\" />").text(_i18n("download"));
      $downloadButton.attr("href", options.downloadURL);

      $(options.downloadElement).append($downloadButton);
    };

    function _initTitle() {
      var $title = $("<h1 class=\"surf_chart title\" />").text(options.title);

      $(options.titleElement).append($title);
    };

    function _initHoverDetail(graph) {
      return new Rickshaw.Graph.HoverDetail({
        graph: graph.graph,
        formatter: options.hoverFormatter
      });
    };

    function _initXAxis(graph) {
      return new Rickshaw.Graph.Axis.Time({
        graph: graph.graph
      });
    };

    function _initYAxis(graph) {
      return new Rickshaw.Graph.Axis.Y({
        graph: graph.graph,
        orientation: 'right',
        tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
      });
    };

    function _hoverFormatter(series, x, y) {
      var date = new Date(x * 1000);
      var dateString = '<span class="date">' + date.getDate() + "-" + (date.getMonth() + 1) + "-" + date.getFullYear() + '</span>';
      var content = "Logins " + series.name + ": " + parseInt(y) + '<br>' + dateString;
      return content;
    };

    function _initLegend(graph) {
      return new Rickshaw.Graph.Legend({
        graph: graph.graph,
        element: options.legendElement
      });
    };

    function _initLegendHighlight(graph, legend) {
      return new Rickshaw.Graph.Behavior.Series.Highlight({
        graph: graph.graph,
        legend: legend
      });
    };

    function _initLegendToggle(graph, legend) {
      return new Rickshaw.Graph.Behavior.Series.Toggle({
        graph: graph.graph,
        legend: legend
      });
    };

    function _i18n(key) {
      var keyList = key.split(".");
      var text = Text[options.lang];
      for (var i in keyList) {
        text = text[keyList[i]];
      }
      return text;
    };

    this.graph = new Rickshaw.Graph.Ajax(options);
    if (options.autoresize) {
      this.bind();
    };
  };

  SurfChart.prototype.redrawGraph = function() {
    this.graph.graph.configure({
      width: $(this.graph.args.element).width()
    });
    this.graph.graph.render();
  };

  SurfChart.prototype.bind = function() {
    $(window).on("resize", this.redrawGraph.bind(this));
  };

  SurfChart.prototype.unbind = function() {
    $(window).off("resize", this.redrawGraph.bind(this));
  };

  SurfChart.prototype.destroy = function() {
    this.unbind();
    $(this.graph.args.element).remove();
  };

  window.SurfChart = SurfChart;

}(window, jQuery));
