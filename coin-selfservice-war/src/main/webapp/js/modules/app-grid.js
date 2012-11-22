var app = app || {};

app.appgrid = function() {
    var gridElm, keywords;

    keywords = [];

    var init  = function() {
        gridElm = $('.app-grid');

        if (gridElm.length === 0) {
            return;
        }

        gridElm.addClass('app-grid-js');

        setHover();
        setSearch();
    };


    var setHover = function() {
        gridElm.find('li').click(function(e) {
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


    var setSearch = function() {
        var placeholderText = app.message.i18n('appgrid.search.placeholder'),
            hasLicenseText = app.message.i18n('appgrid.filter.haslicense'),
            isConnectedText = app.message.i18n('appgrid.filter.isconnected'),
            filters = '';

        if (gridElm.hasClass('filters-available')) {
            filters = '<div>' +
                        '<ul>';
            if (gridElm.hasClass('lmng-active')) {
                filters += '<li><a href="#" data-filter="licensed">' + hasLicenseText + '</a></li>';
            }
            filters += '<li><a href="#" data-filter="connected">' + isConnectedText + '</a></li>' +
                      '</ul>' +
                    '</div>';
        }

        gridElm.before('<nav class="filter-grid' + (gridElm.hasClass('filters-available') ? ' filters-available' : '') + '">' +
                           '<input type="search" class="app-grid-search" placeholder="' + placeholderText + '">' +
                           filters +
                       '</nav>');

        var searchElm = $('.app-grid-search'),
            filterLinks = $('.filter-grid a'),
            timer = null,
            activeFilters = [];


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


        function doSearch(time) {
            var isSearch = searchElm.val().length !== 0;

            if (time === undefined) {
                time = 250;
            }
            
            keywords = [];

            if (isSearch) {
                keywords = searchElm.val().toLowerCase().split(' ');
            }

            gridElm.tickback('filter', {
                duration: time
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


        function mustDisplay(elm) {
            var display = true,
                text = elm.text().toLowerCase();

            $.each(keywords, function(i, kw) {
                if (display === false) {
                    return;
                }

                display = false;

                if (text.indexOf(kw) !== -1) {
                    display = true;
                }
            });

            $.each(activeFilters, function(i, filter) {
                if (display === false) {
                    return;
                }

                display = false;

                if (elm.hasClass(filter)) {
                    display = true;
                }
            });

            return !display;
        }


        function howToSort(a, b){
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