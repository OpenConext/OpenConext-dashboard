var app = app || {};

app.notifications = function() {
  var notificationsElm = null;


  var init = function() {
    checkNotifications();
  };


  var checkNotifications = function() {
    notificationsElm = $('.notifications');

    if (!notificationsElm.length) {
      return;
    }

    showNotification();
  };


  var showNotification = function() {
    notificationsElm.removeClass('hide');
    notificationsElm.attr('tabindex', '0');
    notificationsElm.css('right', '-300px');
    notificationsElm.stop();
    notificationsElm.on('click', showAllNotifications);

    var closeButton = $('<a class="close-notifications" href="#" title="Close">×</a>');
    closeButton.on('click', hideNotifications);
    closeButton.appendTo(notificationsElm);

    setTimeout(function() {
      notificationsElm.animate({
        right: '2.5em'
      }, 1000);
    }, 400);
  };


  var showAllNotifications = function(e) {
    e.preventDefault();

    var url = notificationsElm.attr('data-href');

    if (url && url.length) {
      location.href = url;
    }
  };


  var hideNotifications = function(e) {
    e.preventDefault();
    e.stopPropagation();

    notificationsElm.addClass('hide');
  };


  return {
    init : init
  };
}();

app.register(app.notifications);
