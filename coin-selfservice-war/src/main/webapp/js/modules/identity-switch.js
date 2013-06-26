var app = app || {};

app.identitySwitch = function() {

  var loadingIdentitySwitchModal;
  
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
    // done returns the entire jqXHR object
    var html = $(jsp);

    html.find("a#close-switch-identity-modal").click(function() {
      // Explicitly hide and remove modals, because when opening a new, next modal, we would get confused by
      // the reuse of element ids etc.
      html.modal('hide');
      // wait for css transition to end before removing the modal.
      $(html).on('hidden', function () {
        html.remove();
        loadingModal.modal('hide');
        loadingModal.remove();
      });
    });

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
