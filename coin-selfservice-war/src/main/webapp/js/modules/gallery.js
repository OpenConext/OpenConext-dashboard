var app = app || {};

app.gallery = function() {
    var maxWidth = 0,
        currentGallery = null;


    var init = function() {
        $('.gallery-holder').each(function(index, elm) {
            elm = $(elm);

            elm.addClass('gallery-holder-js');

            elm.prepend('<div class="large-image"><img src="" alt="">');

            elm.on('click', 'a', viewImage);

            var firstElm = elm.find('ul li:first a');
            firstElm.trigger('click');
        });
    };


    var viewImage = function(e) {
        e.preventDefault();

        var elm = $(this),
            imgUrl = elm.attr('href'),
            img = new Image();

        img.onload = function() {
            currentGallery = elm.closest('.gallery-holder');
            currentGallery.find('.large-image img').attr('src', this.src);
            maxWidth = this.width;
        };

        img.src = imgUrl;
    };


    return {
        init: init
    };
}();

app.register(app.gallery);
