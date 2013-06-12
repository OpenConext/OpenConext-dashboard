var app = app || {};

app.inlineHelp = function () {

  var init = function () {

    $('a.more-down-less-up').click(function () {
      $self = $(this);
      $self.parent('.data-table-holder').find('.data-table-wrapper').slideToggle();
      $self.find('i.icon-arrow-up,i.icon-arrow-down').toggle();
    });

    // Inline help for shop admin
    $("i.inlinehelp,p.recent-login").popover({
      'trigger': 'hover',
      'placement': 'top'});

  };

  return {
    init: init
  };
}();

app.register(app.inlineHelp);
