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
    var placeholderText = app.message.i18n('appgrid.search.placeholder');

    var searchElm = $('.app-grid-search-2'),
      timer = null,
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
          var facetSearchTerm = $elm.data("facet-search-term");
          var count = 0;
          notHidden.each(function(j){
            var arr = $(this).data("facet-values").trim().split(" ");
            if ($.inArray(facetSearchTerm, arr) !== -1) {
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

      if (display) {
        var arr = elm.data("facet-values").trim().split(" ");
        $.each(facetValues, function (i, facetValue) {
          if (display && $.inArray(facetValue, arr) === -1) {
            display = false;
          }
        });
      }

      return !display;
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