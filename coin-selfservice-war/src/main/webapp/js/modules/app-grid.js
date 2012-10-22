var app = app || {};

app.appgrid = function() {
    var gridElm;

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

            if (target.is('a')) {
                return;
            }

            e.preventDefault();

            var link = elm.find('h2 a');

            location.href = link.attr('href');
        });
    };


    var setSearch = function() {
        var placeholder = gridElm.data('searchPlaceholder');

        gridElm.before('<nav class="filter-grid">' +
                         '<input type="search" class="app-grid-search" placeholder="' + placeholder + '">' +
                         '<ul>' +
                           '<li><a href="#foo" data-filter="licensed">Has license</a</li>' +
                           '<li><a href="#foo" data-filter="connected">Is connected</a</li>' +
                         '</ul>' +
                       '</nav>');

        var searchElm = $('.app-grid-search'),
            filterLinks = $('.filter-grid a'),
            timer = null,
            activeFilters = [];

        function setTimer() {
            if (timer) {
                clearTimeout(timer);
            }
            timer = setTimeout(doSearch, 100);
        }

        function doSearch() {
            var isSearch = searchElm.val().length !== 0,
                keywords = [];

            if (isSearch) {
                keywords = searchElm.val().toLowerCase().split(' ');
            }

            gridElm.find('li').each(function(index, elm) {
                var $elm = $(elm),
                    display = true,
                    text = elm.textContent.toLowerCase() || elm.innerText.toLowerCase();

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

                    if ($elm.hasClass(filter)) {
                        display = true;
                    }
                });

                if (display) {
                    $elm.removeClass('hide');
                }
                else {
                    $elm.addClass('hide');
                }
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

            doSearch();
        }

        searchElm.bind('keyup change', setTimer);
        filterLinks.on('click', doFilter);
    };


    return {
        init: init
    };
}();

app.register(app.appgrid);