var app = app || {};

app.graphs = function() {
  var chartOverviewElm, chartDetailElm, originalData, consolidatedData, filterElm, filterType, filterOffset, firstDate, currentData, currentlyShownSp, currentTitle, wrapperElm, isGod;

  var mock = "mock";

  currentlyShownSp = null;
  currentTitle = app.message.i18n('stats.title.overview_default');

  filterType = 'all';
  filterOffset = 0;

  var init = function() {
    chartOverviewElm = $('#sp-overview-chart');
    chartDetailElm = $('#sp-detail-chart');

    // Only launch graphs app when these elements are on the apge
    if (chartOverviewElm.length === 0 || chartDetailElm.length === 0) {
      return;
    }

    wrapperElm = $('.statistics-holder');
    isGod = chartOverviewElm.data('is-god');
    if (isGod) {
      wrapperElm.addClass('statistics-holder-idp-switch');
      $('#idp-select2').select2({
        placeholder: app.message.i18n('stats.select_idp')
      }).change(idpChange);
    }

    // Fetch dependencies
    var highcharts = $.ajax({
      url : contextPath + '/js/highcharts.js',
      cache : true,
      dataType : 'script'
    });

    var jsonUrl = (isGod ? '/stats/loginsperspperday.json'
        : '/stats/loginsperspperdaybyidp.json')

    var data = $.ajax({
      url : contextPath + jsonUrl,
      cache : true,
      dataType : 'json'
    });

    var formattedData = data.then(formatData);

    $.when(highcharts, formattedData).done(initRendering);

    initFilters();
  };

  var idpChange = function() {
    // TODO can be also called when in SP selected modus
    var newIdp = $("#idp-select2 option:selected").val();
    if (newIdp === 'ALL') {
      renderOverview(consolidatedData);
    } else {
      renderOverview(filterOutIdp(originalData, newIdp));
    }
  }

  var filterOutIdp = function(data, newIdp) {
    var newArray = [];
    for ( var i = 0, l = data.length; i < l; ++i) {
      if (data[i].idpEntityId === newIdp) {
        newArray.push(data[i]);
      }
    }
    return newArray;
  }

  var initI18n = function() {
    Highcharts.setOptions({
      lang : {
        resetZoom : app.message.i18n('stats.reset_zoom'),
        shortMonths : app.message.i18n('stats.short_months').split('|')
      }
    });
  };

  var initFilters = function() {
    filterElm = $('.statistics-filters');

    filterElm.on('click', '.show a', setTimeframe);
    filterElm.on('change', '#choose-time-offset', setTimeframe);
    wrapperElm.on('click', '.back', function() {
      location.hash = '';
    });
  };

  var initRendering = function(highcharts, data) {
    initI18n();

    chartOverviewElm.removeClass('ajax-loader');

    if (arguments.length === 2) {
      // originalData = data;

      renderOverview(filterData(data, filterType, filterOffset));

      filterElm.find('.show a:first').trigger('click');

      if ('onhashchange' in window) {
        $(window).on('hashchange', setHash);
      }

      if (location.hash.length) {
        setHash();
      }
    } else {
      if (highcharts) {
        highcharts.preventDefault();
      }
      currentTitle = app.message.i18n('stats.title.overview_default');
      renderOverview(currentData, true);
    }
  };

  var renderOverview = function(data, back) {
    currentData = data;

    if (back) {
      chartDetailElm.stop().fadeOut(500);
      // chartOverviewElm.stop().fadeOut(500).fadeIn(500);
      chartOverviewElm.stop().fadeIn(1000);
    }

    currentlyShownSp = null;

    var formattedData = formatForOverview(data), categories = formatCategories(formattedData), height = Math
        .max(formattedData.length * 40 + 150, 400);

    chartOverviewElm.closest('section').height(height);

    $('.back, .forward', wrapperElm).addClass('hide');

    var chart = new Highcharts.Chart({
      chart : {
        animation : false,
        height : height,
        renderTo : chartOverviewElm.attr('id'),
        type : 'bar'
      },
      credits : {
        text : 'Highcharts'
      },
      plotOptions : {
        bar : {
          animation : false,
          borderColor : '#4FB3CF',
          borderWidth : 1,

          dataLabels : {
            enabled : true
          },
          events : {
            click : setSp
          },
          fillColor : {
            linearGradient : {
              x1 : 0,
              y1 : 0,
              x2 : 0,
              y2 : 1
            },
            stops : [ [ 0, 'rgba(79, 179, 207, 0.75)' ],
                [ 1, 'rgba(79, 179, 207, 0.2)' ] ]
          },
          color : '#4FB3CF',
          pointPlacement : null,
          shadow : false,
          pointPadding : 0.2,
          minPointLength : 10
        }
      },
      series : [ {
        data : formattedData,
        name : 'SPs'
      } ],
      title : {
        text : currentTitle
      },
      xAxis : {
        categories : categories,
        labels : {
          enabled : true
        },
        lineColor : '#7F7F7F',
        title : {
          enabled : false
        }
      },
      yAxis : {
        gridLineColor : '#E5E5E5',
        gridLineWidth : 1,
        title : {
          text : app.message.i18n('stats.total_logins'),
          style : {
            color : '#7F7F7F'
          }
        }
      }
    });

    $('.highcharts-axis-labels:first tspan', chartOverviewElm).on('click',
        function() {
          setSp($(this).text());
        }).hover(function() {
      $(this).parent().css({
        cursor : 'pointer',
        fill : '#4FB3CF'
      });
    }, function() {
      $(this).parent().css({
        cursor : 'default',
        fill : '#666'
      });
    });
  };

  var renderChart = function(which) {
    var data = currentData[which];

    currentlyShownSp = which;

    // if coming from overview TODO move to generic function (or helper, or
    // jquery function)
    $('.back, .forward', wrapperElm).removeClass('hide');
    chartOverviewElm.stop().fadeOut(500).promise().done(function() {
      chartDetailElm.stop().fadeOut(500).promise().done(function() {
        createDetailChart(data);
        chartDetailElm.closest('section').height(400);
        chartDetailElm.fadeIn(500);
      });
    });
  };

  var createDetailChart = function(data) {
    return new Highcharts.Chart(
        {
          chart : {
            animation : false,
            renderTo : chartDetailElm.attr('id'),
            type : 'areaspline',
            zoomType : 'x'
          },
          credits : {
            text : 'Highcharts'
          },
          plotOptions : {
            areaspline : {
              animation : false,
              borderWidth : 0,
              fillColor : 'rgba(79, 179, 207, 0.2)',
              color : '#4FB3CF',
              pointPlacement : null,
              shadow : false,
              pointPadding : 0.2
            }
          },
          series : [ {
            name : app.message.i18n('stats.logins'),
            data : data.data,
            pointStart : Math.max(data.pointStart, filterOffset),
            pointInterval : data.pointInterval
          } ],
          tooltip : {
            pointFormat : '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b><br/>',
            valueDecimals : 0,
            shared : true
          },
          title : {
            text : currentTitle
          },
          xAxis : {
            type : 'datetime',
            lineColor : '#7F7F7F',
            maxZoom : 4 * 24 * 3600000
          },
          yAxis : {
            gridLineColor : '#E5E5E5',
            gridLineWidth : 1,
            title : {
              text : app.message.i18n('stats.logins_per_day'),
              style : {
                color : '#7F7F7F'
              }
            }
          }
        });
  }

  var setHash = function() {
    setSp(decodeURIComponent(location.hash.substring(1)));
  };

  var setSp = function(arg) {
    if (typeof arg === 'string') {
      if (arg.length > 0
          && decodeURIComponent(location.hash).indexOf(arg) !== 1) {
        location.hash = encodeURIComponent(arg);
        return;
      } else {
        currentlyShownSp = false;
        for ( var l = currentData.length - 1; l > -1; --l) {
          /*
           * We either get the link from the SP overview table (name) or from
           * the detail page (spEntityId).
           */
          if (arg.length > 0
              && (currentData[l].name.indexOf(arg) === 0 || currentData[l].spEntityId
                  .indexOf(arg) === 0)) {
            currentlyShownSp = l;
            break;
          }
        }

        if (currentlyShownSp === false) {
          initRendering();
          return;
        }
      }
    } else {
      currentlyShownSp = arg.point.config[2];

      var name = currentData[currentlyShownSp].name.substring(0, 30);

      if (decodeURIComponent(location.hash).indexOf() !== 1) {
        location.hash = encodeURIComponent(name);
        return;
      }
    }

    currentTitle = app.message.i18n('stats.title.sp_overview').replace('#{sp}',
        currentData[currentlyShownSp].name).replace('#{total}',
        currentData[currentlyShownSp].total);

    if (filterType !== 'all') {
      currentTitle += ' over '
          + $('#choose-time-offset option:selected').text();
    }

    renderChart(currentlyShownSp);
  };

  var setTimeframe = function(e) {
    e.preventDefault();

    var elm = $(this), menuItems;

    if (elm.is('a')) {
      filterType = elm.attr('data-show');
      filterOffset = getDateOffset(firstDate, filterType);
      menuItems = getMenuItems(filterOffset, filterType); // get an object with
      // time-friendly time
      // ranges
      filterOffset = buildMenu(menuItems); // returns the last time offset

      elm.closest('div').find('.active').removeClass('active');
      elm.addClass('active');
    } else {
      filterOffset = elm.val();
    }

    filterOffset = parseInt(filterOffset, 10);

    var newData = [];

    var dataToShow = whichDataToShow();

    for ( var i = 0, l = dataToShow.length; i < l; ++i) {
      newData.push(jQuery.extend(true, {}, dataToShow[i]));
    }

    if (currentlyShownSp !== null) {
      currentData = filterData(newData, filterType, filterOffset);

      if (filterType === 'all') {
        currentTitle = app.message.i18n('stats.title.sp_overview').replace(
            '#{sp}', currentData[currentlyShownSp].name).replace('#{total}',
            currentData[currentlyShownSp].total);
      } else {
        currentTitle = app.message.i18n('stats.title.sp_zoomed').replace(
            '#{sp}', currentData[currentlyShownSp].name).replace('#{range}',
            $('#choose-time-offset option:selected').text());
      }

      renderChart(currentlyShownSp);
    } else {
      if (filterType === 'all') {
        currentTitle = app.message.i18n('stats.title.overview_default');
      } else {
        currentTitle = app.message.i18n('stats.title.overview_zoomed').replace(
            '#{range}', $('#choose-time-offset option:selected').text());
      }

      renderOverview(filterData(newData, filterType, filterOffset));
    }
  };

  var whichDataToShow = function() {
    return isGod ? consolidatedData : originalData;
  }

  var getDateOffset = function(firstDate, filterType) {
    var date = new Date(firstDate), first, refDate;

    switch (filterType) {
    case 'week':
      first = date.getTime() - date.getDay() * 60 * 60 * 24 * 1000;
      refDate = new Date(first);
      break;
    case 'month':
      first = date.getTime() - (date.getDate() - 1) * 60 * 60 * 24 * 1000;
      refDate = new Date(first);
      break;
    case 'quarter':
      first = date.getTime() - (date.getDate() - 1) * 60 * 60 * 24 * 1000;
      var newDate = new Date(first), newMonth = newDate.getMonth(), refMonth = 0;
      if (newMonth > 2) {
        refMonth = 3;
      }
      if (newMonth > 5) {
        refMonth = 6;
      }
      if (newMonth > 8) {
        refMonth = 9;
      }
      refDate = new Date(newDate.getFullYear(), refMonth, 1);
      break;
    default:
      refDate = date;
      break;
    }

    return refDate;
  };

  var getMenuItems = function(dateOffset, filterType) {
    var day = 24 * 60 * 60 * 1000, months = app.message.i18n(
        'stats.menu.months').split('|'), today = new Date(), dates = {}, dateOffsetTime = dateOffset
        .getTime(), display = true, newMonth, newYear;

    switch (filterType) {
    case 'week':
      while (dateOffset < today) {
        dateOffsetTime = dateOffset.getTime();
        dates[dateOffsetTime] = app.message.i18n('stats.weekof').replace(
            '#{week}', dateOffset.getWeek()).replace('#{year}',
            dateOffset.getFullYear());
        dateOffset = new Date(dateOffsetTime + day * 7);
      }
      break;
    case 'month':
      while (dateOffset < today) {
        dateOffsetTime = dateOffset.getTime();
        dates[dateOffsetTime] = months[dateOffset.getMonth()] + ' '
            + dateOffset.getFullYear();
        newMonth = dateOffset.getMonth() + 1;
        newYear = dateOffset.getFullYear();
        if (newMonth === 12) {
          newMonth = 0;
          ++newYear;
        }
        dateOffset = new Date(newYear, newMonth, 1);
      }
      break;
    case 'quarter':
      while (dateOffset < today) {
        dateOffsetTime = dateOffset.getTime();
        dates[dateOffsetTime] = 'Q'
            + Math.floor((dateOffset.getMonth() + 3) / 3) + ' '
            + dateOffset.getFullYear();
        newMonth = dateOffset.getMonth() + 3;
        newYear = dateOffset.getFullYear();
        if (newMonth === 12) {
          newMonth = 0;
          ++newYear;
        }
        dateOffset = new Date(newYear, newMonth, 1);
      }
      break;
    default:
      display = false;
    }

    return {
      display : display,
      dates : dates
    };
  };

  // Create a dropdown list of all menu items
  var buildMenu = function(menuItems) {
    var menu = $('#choose-time-offset');

    $('option', menu).remove();

    var maxTime = 0;

    if (menuItems.display) {
      $.each(menuItems.dates, function(time, text) {
        menu.prepend('<option value="' + time + '">' + text + '</option>');
        if (time > maxTime) {
          maxTime = time;
        }
      });
      menu.attr('disabled', false);
    } else {
      menu.attr('disabled', true);
    }

    $('option:first', menu).attr('selected', true);

    return maxTime;
  };

  /*
   * Sum up individual SP totals for overview
   * 
   */
  var formatData = function(data) {
    var total;

    firstDate = Infinity;

    for ( var i = 0, l = data.length; i < l; ++i) {
      total = 0;
      for ( var j = 0, m = data[i].data.length; j < m; ++j) {
        total += data[i].data[j];
      }
      if (firstDate > data[i].pointStart) {
        firstDate = data[i].pointStart;
      }
      data[i].total = total;
    }

    filterOffset = firstDate;

    originalData = data;

    if (isGod) {
      consolidatedData = consolidateSPs(data);
      return consolidatedData;
    }

    return data;
  };

  var consolidateSPs = function(data) {
    if (data.length === 0) {
      return data;
    }
    data.sort(function(a, b) {
      return a.spEntityId > b.spEntityId ? 1
          : ((b.spEntityId > a.spEntityId) ? -1 : 0);
    });
    var result = [];
    var previous = copySpDataObject(data[0]);
    for ( var i = 1, l = data.length; i < l; ++i) {
      if (previous.spEntityId === data[i].spEntityId) {
        var next = data[i];
        /*
         * now enrich/merge the previous with the data[i]
         */
        var interval = previous.pointInterval;

        var end = Math.max(previous.pointStart
            + (interval * previous.data.length), next.pointStart
            + (interval * next.data.length));
        var start = Math.min(previous.pointStart, next.pointStart);

        var nbrOfDays = Math.round((end - start) / interval);
        /*
         * Create a new array for the previous.data with the two data sets
         * combined
         */
        var newData = [];
        for ( var j = 0; j < nbrOfDays; ++j) {
          newData.push(getLoginsForData(previous, start)
              + getLoginsForData(next, start));
          start += interval;
        }
        previous.data = newData;
        previous.pointStart = Math.min(previous.pointStart, next.pointStart);
        previous.total += next.total;

      } else {
        result.push(previous);
        previous = copySpDataObject(data[i]);
      }
    }
    return result;
  }

  var getLoginsForData = function(data, start) {
    if (start < data.pointStart) {
      return 0;
    }
    if (start > (data.pointStart + (data.pointInterval * data.data.length))) {
      return 0;
    }
    var pos = Math.round((start - data.pointStart) / data.pointInterval);
    // better be on the safe side with extra check
    return (pos < data.data.length) ? data.data[pos] : 0;
  }

  /*
   * To maintain two sets of data (one consolidated for all Idp's and one per
   * Idp) we need to make deep copies as we otherwise end up with
   * cross-references between the two datasets. Note that we don't copy the
   * idpEntityId as this does not make sense in consolidated set. We also copy
   * the dataInstance.data as it might be that there is only one Sp presence
   * 
   * We could use jQuery extend, but it would be overkill
   * 
   * TODO jQuery.extend(true, {}, dataInstance)
   */
  var copySpDataObject = function(dataInstance) {
    return {
      'name' : dataInstance.name,
      'pointInterval' : dataInstance.pointInterval,
      'pointStart' : dataInstance.pointStart,
      'spEntityId' : dataInstance.spEntityId,
      'total' : dataInstance.total,
      'data' : dataInstance.data.slice(0)
    };
  }

  // Slice of data from the beginning and end of the arrays, to fit the
  // specified time filters
  var filterData = function(data, filterType, filterOffset) {
    var newData, dataOffset, dataInterval, spliceTil, total, spliceFrom = Infinity;

    switch (filterType) {
    case 'week':
      spliceFrom = 7;
      break;
    case 'month':
      spliceFrom = 30;
      break;
    case 'quarter':
      spliceFrom = 90;
      break;
    default:
      spliceFrom = Infinity;
    }

    for ( var i = 0, l = data.length; i < l; ++i) {
      newData = [], dataOffset = data[i].pointStart,
          dataInterval = data[i].pointInterval, spliceTil = 0;

      // Calculate start offset from start date in data and filter date
      if (dataOffset <= filterOffset) {
        spliceTil = Math.round((filterOffset - dataOffset) / dataInterval);
      }

      data[i].data.splice(0, spliceTil);
      data[i].data.splice(spliceFrom, Infinity);

      // Calculate new totals
      total = 0;
      for ( var j = 0, m = data[i].data.length; j < m; ++j) {
        total += data[i].data[j];
      }
      if (firstDate > data[i].pointStart) {
        firstDate = data[i].pointStart;
      }
      data[i].total = total;
    }

    return data;
  };

  // Put the data in a new array, with only the name and the total; order it.
  var formatForOverview = function(data) {
    var newArray = [];

    for ( var i = 0, l = data.length; i < l; ++i) {
      newArray.push([ data[i].name, data[i].total, i ]);
    }

    newArray.sort(function(a, b) {
      if (a[1] < b[1]) {
        return 1;
      }
      if (a[1] > b[1]) {
        return -1;
      }
      return 0;
    });

    return newArray;
  };

  // Names used in overview graph, take only a flat array with the titles.
  var formatCategories = function(data) {
    var newArray = [];

    for ( var i = 0, l = data.length; i < l; ++i) {
      newArray.push(data[i][0].substring(0, 30));
    }

    return newArray;
  };

  return {
    init : init
  };
}();

app.register(app.graphs);

Date.prototype.getWeek = function() {
  var onejan = new Date(this.getFullYear(), 0, 1);
  return Math.ceil((((this - onejan) / 86400000) + onejan.getDay() + 1) / 7);
};