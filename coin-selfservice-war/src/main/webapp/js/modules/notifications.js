var app = app || {};

app.notifications = function() {
  var messageBox = null;


  var init = function() {
    checkNotifications();
  };


  var checkNotifications = function() {
    if (typeof window.notifications !== 'undefined') {
      showNotifications(window.notifications);
    }
  };


  var showNotifications = function(notifications) {
    var lngt = notifications.length;
    if (lngt === 0) {
      return;
    }
 
    for (var i = 0, l = Math.min(3, lngt); i < l; ++i) {
      displaySingleNotification(notifications[i]);
    }

    if (lngt > 3) {
      displaySingleNotification('jsp.notifications.too_many');
    }
  };


  var displaySingleNotification = function(message) {
    if (typeof message === 'object') {
      message = app.message.i18n(message.messageKey).replace('{0}', message.arguments);
    }
    else {
      message = app.message.i18n(message);
    }


    if (!messageBox) {
      messageBox = $('<div class="notifications bottom-right bottom-left"></div>');
      messageBox.appendTo('body');
    }

    messageBox.notify({
      fadeOut: {
        enabled: true,
        delay: 10000
      },
      message: {
        text: message
      },
      type: 'info'
    }).show();
  };


  return {
    init : init
  };
}();

app.register(app.notifications);
