var app = app || {};

app.identitySwitch = function() {

  var init = function() {
    var switchIdentity = $('#switch-identity');

    if (switchIdentity.length === 0) {
      return;
    }

    switchIdentity.click(function(e) {
      e.preventDefault();
      var jsp = $.get($(this).attr('href'));
      $.when(jsp).done(renderModal);
    });
  };

  var renderModal = function(jsp) {
    var html = $(jsp);
    html.find("#referenceIdentityProviders").select2();
    html.modal({
      backdrop : "static",
      keyboard : "false"
    });
  }

  return {
    init : init
  };

}();

app.register(app.identitySwitch);