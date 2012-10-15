var app = app || {};

app.respond = function() {
    var menuSizeWatcher;


    var init = function() {
        setSizeWatcher();
        $('body').on('click', closeMenu).removeClass('js-loading').addClass('js-loaded');
    };


    var setSizeWatcher = function() {
        $('.primary-navigation').wrap('<div class="menu-respond-holder">');

        menuSizeWatcher = new SizeWatcher('body');

        menuSizeWatcher.breakpoint(0, 768, {
            className: 'condensed',
            enter: function() {
                $('.header').append('<a href="#" class="menu-trigger">Menu</a>').find('.menu-trigger').click(openMenu);
                $('.menu-respond-holder').addClass('hide');
            },
            leave: function() {
                $('.header .menu-trigger').remove();
                $('.menu-respond-holder').removeClass('hide');
            }
        });

        menuSizeWatcher.breakpoint(0, 951, {
            enter: function() {
                if ($('.app-grid-wrapper').length === 1) {
                    $('body').addClass('no-sticky-app-grid-wrapper-menu');
                }
                else {
                    $('body').removeClass('no-sticky-app-grid-wrapper-menu');
                }
            },
            leave: function() {
                $('body').removeClass('no-sticky-app-grid-wrapper-menu');
            }
        });


        var columnSizeWatcher = new SizeWatcher('.has-left-right');

        columnSizeWatcher.breakpoint(0, 950, {
            className: 'narrow',
            order: ['.column-left', '.column-center', '.column-right']
        });

        columnSizeWatcher.breakpoint(950, Infinity, {
            order: ['.column-left', '.column-right', '.column-center']
        });



        var gallerySizeWatcher = new SizeWatcher('.gallery-holder');

        gallerySizeWatcher.breakpoint(0, 400, {
            className: 'narrow-gallery'
        });
    };


    var openMenu = function(e) {
        e.preventDefault();

        $('.menu-respond-holder').toggleClass('hide');
    };


    var closeMenu = function(e) {
        var tgt = $(e.target);

        window.setTimeout(function() {
            if (tgt.closest('.menu-respond-holder,.menu-trigger').length === 0) {
                $('.condensed .menu-respond-holder').addClass('hide');
            }
        }, 50);
    };


    return {
        init: init
    };
}();

app.register(app.respond);