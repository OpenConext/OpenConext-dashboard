var app = app || {};

app.shopAdmin = function () {

  var init = function () {

    $('a.more-down-less-up').click(function () {
      $self = $(this);
      $self.parent('.data-table-holder').find('.data-table-wrapper').slideToggle();
      $self.find('i.icon-arrow-up,i.icon-arrow-down').toggle();
    });

    // Inline help for shop admin
    $("i.inlinehelp").popover({
      'trigger': 'hover',
      'placement': 'top'});

  };

  return {
    init: init
  };
}();

app.register(app.shopAdmin);
