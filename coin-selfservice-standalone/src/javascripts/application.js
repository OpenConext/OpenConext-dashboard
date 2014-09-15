var BASE_URL = "/selfservice/api";

$(document).ajaxError(App.ajaxError);
$(document).ajaxSend(function(event, jqxhr, settings) {
  if(App.currentUser) {
    jqxhr.setRequestHeader("X-IDP-ENTITY-ID", App.currentUser.currentIdp.id);
  }
});

I18n.locale = "en";

App.initialize();
