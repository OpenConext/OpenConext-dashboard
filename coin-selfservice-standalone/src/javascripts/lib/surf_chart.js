;(function(window, $){
  "use strict";

  function SurfChart(args) {
    if (!(this instanceof SurfChart)){
      return new SurfChart(args);
    }

    var Defaults = {
      lang: "en",
      height: 400,
      renderer: "line",
      hoverDetail: true,
      xAxis: true,
      yAxis: true,
      title: true,
      downloadURL: true,
      dataURL: _dataURL(),

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
        }
      }
    };

    var $numSelect;
    var $typeSelect;
    var options = $.extend({}, Defaults, args);
    options.element = options.chartElement || options.element; // alias option

    function _dataURL() {
      return "http://localhost:8001/test.html";
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
        options.title = d.request.displayName;
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
      if (options.hoverDetail) _initHoverDetail(graph);
      if (options.xAxis) _initXAxis(graph);
      if (options.yAxis) _initYAxis(graph);

      if (options.onLoad) options.onLoad(graph);

      if (options.periodElement) _initPeriodSelect();
      if (options.downloadElement && options.downloadURL) _initDownloadButton();
      if (options.titleElement && options.title) _initTitle();

      graph.graph.update();
    };

    function _initPeriodSelect() {
      var $label = $("<span class=\"surf_chart period_select show\"/>").text(_i18n("show"));

      $typeSelect = $("<select class=\"surf_chart period_select type_select\" />").
        append(new Option(_i18n("month"), "month", true, true)).
        append(new Option(_i18n("quarter"), "quarter")).
        append(new Option(_i18n("year"), "year")).
        on("change", _renderNumberOptions);

      $numSelect = $("<select class=\"surf_chart period_select num_select\" />");
      _renderNumberOptions();

      $(options.periodElement).
        append($label).
        append($typeSelect).
        append($numSelect);
    };

    function _renderNumberOptions() {
      var newOptions;
      switch ($typeSelect.val()) {
        case "month":
          newOptions = Text[options.lang]["months"];
          break;
        case "quarter":
          newOptions = {
            "1": "Q1",
            "2": "Q2",
            "3": "Q3",
            "4": "Q4"
          }
          break;
        case "year":
          var year = new Date().getFullYear();
          newOptions = {}
          newOptions[year] = year;
          newOptions[year - 1] = year - 1;
          break;
      }

      $numSelect.empty();
      for (var key in newOptions) {
        var value = newOptions[key];
        $numSelect.append(new Option(value, key));
      }
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

    function _i18n(key) {
      return Text[options.lang][key];
    };

    this.graph = new Rickshaw.Graph.Ajax(options);
    this.bind();
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
