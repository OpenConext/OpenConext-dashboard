var app = app || {};

app.notifications = function() {
  var notificationsElm = null;

  var init = function() {
    checkNotifications();
  };


  var checkNotifications = function() {
    notificationsElm = $('.notifications-popup');

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

    var closeButton = $('.close-notifications', notificationsElm);
    closeButton.on('click', hideNotifications);
    closeButton.appendTo(notificationsElm);

	setTimeout(function() {
		notificationsElm.animate({
			right: '2.5em'
		}, 1000);
		animationShown = true;
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
	  e.stopPropagation();
	  e.preventDefault();
	  var hideUrl = $(this).attr('href');
	  $.ajax({
		  url: hideUrl,
	  });

    notificationsElm.remove();
  };


  return {
    init : init
  };
}();

app.register(app.notifications);
