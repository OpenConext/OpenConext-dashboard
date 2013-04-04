var app = app || {};

app.shopAdmin = function () {

  var init = function () {

    $('a.more-down-less-up').click(function () {
      $self = $(this);
      $self.parent('.data-table-holder').find('.data-table-wrapper').slideToggle();
      $self.find('i').toggle();
    });

  };

  return {
    init:init
  };
}();

app.register(app.shopAdmin);
