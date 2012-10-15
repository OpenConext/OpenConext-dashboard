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

        gridElm.before('<input type="search" class="app-grid-search" placeholder="' + placeholder + '">');

        var searchElm = $('.app-grid-search'),
            timer = null;

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

                if (display) {
                    $elm.removeClass('hide');
                }
                else {
                    $elm.addClass('hide');
                }
            });
        }

        searchElm.bind('keyup change', setTimer);
    };


    return {
        init: init
    };
}();

app.register(app.appgrid);