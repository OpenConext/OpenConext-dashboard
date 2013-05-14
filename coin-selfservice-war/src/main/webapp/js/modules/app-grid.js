var app = app || {};

app.appgrid = function () {
  var gridElm, keywords, facetValues;

  keywords = [];
  facetValues = [];

  var init = function () {
    gridElm = $('.app-grid');

    if (gridElm.length === 0) {
      return;
    }

    gridElm.addClass('app-grid-js');

    setHover();
    setSearch();
  };

  var setHover = function () {
    gridElm.find('li').click(function (e) {
      var elm = $(this),
        target = $(e.target);

      if (target.is('a') || target.closest('a').length === 1) {
        return;
      }

      e.preventDefault();

      var link = elm.find('h2 a');

      location.href = link.attr('href');
    });
  };

  var setSearch = function () {
    var placeholderText = app.message.i18n('appgrid.search.placeholder'),
      filters = '';

    if (gridElm.hasClass('filters-available')) {
      filters = '<div>' +
        '<ul>';
      if (gridElm.hasClass('lmng-active')) {
        filters += '<li><a href="#" data-filter="licensed">' + app.message.i18n('appgrid.filter.license') + '</a></li>' +
          '<li><a href="#" data-filter="not-licensed">' + app.message.i18n('appgrid.filter.no-license') + '</a></li>';
      }
      filters += '<li><a href="#" data-filter="connected">' + app.message.i18n('appgrid.filter.connected') + '</a></li>' +
        '<li><a href="#" data-filter="not-connected">' + app.message.i18n('appgrid.filter.not-connected') + '</a></li>' +
        '</ul>' +
        '</div>';
    }

//    gridElm.parents('section').prepend('<nav class="filter-grid' + (gridElm.hasClass('filters-available') ? ' filters-available' : '') + '">' +
//      '<input type="search" class="app-grid-search" placeholder="' + placeholderText + '">' +
//      filters +
//      '</nav>');
//
    var searchElm = $('.app-grid-search-2'),
      filterLinks = $('.filter-grid a'),
      timer = null,
      activeFilters = [],
      facetLinks = $(".facet-search a");


    function checkFocusKeyUp(e) {
      if (e.which === 191 && !$(e.target).is('input,textarea,a,submit,button')) {
        searchElm[0].focus();
      }
    }


    function setTimer() {
      if (timer) {
        clearTimeout(timer);
      }
      timer = setTimeout(doSearch, 250);
    }


    function doSearch(time, afterAnimationCallback) {
      var isSearch = searchElm.val().length !== 0;

      if (time === undefined) {
        time = 250;
      }

      keywords = [];

      if (isSearch) {
        keywords = searchElm.val().toLowerCase().split(' ');
      }

      gridElm.tickback('filter', {
        duration: time ,
        afterAnimationCallback: afterAnimationCallback
      });
    }


    function doFilter(e) {
      e.preventDefault();

      var clickedFilter = $(this),
        theFilter = clickedFilter.data('filter'),
        index = $.inArray(theFilter, activeFilters);

      if (index === -1) {
        activeFilters.push(theFilter);
        clickedFilter.addClass('active-filter');
      }
      else {
        activeFilters.splice(index, 1);
        clickedFilter.removeClass('active-filter');
      }

      doSearch(250);
    }

    function doFacetSearch(e) {
      e.preventDefault();

      var $elm = $(this);
      $elm.toggleClass("active inactive");
      var facetValue = $elm.data("facet-search-term");
      var index = $.inArray(facetValue, facetValues);
      if (index > -1) {
        facetValues.splice(index, 1);
      } else {
        facetValues.push(facetValue);
      }
      doSearch(250, function(){
        /*
         * We need to change the count label of each facet value to display the number that is 'left'
         */
        var notHidden = $("ul.app-grid li:not(.grid-item-hidden)");
        $("ul.facets-values li a").each(function(i){
          var $elm = $(this);
          var count = 0;
          notHidden.each(function(j){
            if ($(this).hasClass($elm.data("facet-search-term"))) {
              count++;
            }
          });
          $elm.find("span").html("(" + count + ")");
        }) ;
      });
    }

    function mustDisplay(elm) {
      var display = true,
        text = elm.text().toLowerCase();

      $.each(keywords, function (i, kw) {
        if (display === false) {
          return;
        }

        display = false;

        if (text.indexOf(kw) !== -1) {
          display = true;
        }
      });

      filters = ignoreObsoleteFilters(activeFilters);

      $.each(filters, function (i, filter) {
        if (display === false) {
          return;
        }

        display = false;

        //TODO use a data element to filter on and not the class name
        if (elm.hasClass(filter)) {
          display = true;
        }
      });

      if (display) {
        $.each(facetValues, function (i, facetValue) {
          if (!elm.hasClass(facetValue)) {
            display = false;
          }
        });
      }

      return !display;
    }


    function ignoreObsoleteFilters(filters) {
      var cantHaveBoth = [
          ['connected', 'not-connected'],
          ['licensed', 'not-licensed']
        ],
        inspecting = null,
        pos0, pos1,
        toReturn = $.extend(true, [], filters);

      for (var l = cantHaveBoth.length - 1; l >= 0; --l) {
        inspecting = cantHaveBoth[l];

        pos0 = $.inArray(inspecting[0], toReturn);
        pos1 = $.inArray(inspecting[1], toReturn);

        if (pos0 !== -1 && pos1 !== -1) {
          toReturn.splice(pos0, 1);
          toReturn.splice($.inArray(inspecting[1], toReturn), 1);
        }
      }

      return toReturn;
    }


    function howToSort(a, b) {
      var aText = $.trim(a.find('h2').text().toLowerCase()),
        bText = $.trim(b.find('h2').text().toLowerCase());

      if (aText < bText) {
        return -1;
      }
      if (aText > bText) {
        return 1;
      }
      return 0;
    }


    searchElm.bind('keyup change', setTimer);
    filterLinks.on('click', doFilter);
    facetLinks.live("click", doFacetSearch);

    $(window).keyup(checkFocusKeyUp);

    gridElm.tickback({
      activeItemsFirst: true,
      duration: 250,
      filterCallback: mustDisplay,
      itemInactiveClass: 'grid-item-hidden',
      itemInactiveStyles: {
        opacity: 0.35
      },
      itemActiveStyles: {
        opacity: 1
      },
      sortCallback: howToSort
    });

  };

  return {
    init: init
  };
}();

app.register(app.appgrid);