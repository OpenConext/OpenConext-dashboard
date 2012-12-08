var app = app || {};

app.idpStatus = function() {

  var init = function() {
    var filteredIdpId = $('#filteredIdpId');

    if (filteredIdpId.length === 0) {
      return;
    }

    filteredIdpId.select2().change(function() {
      $('#selectIdpForm').submit();
    });

    $('input[aria-controls="csp-statusses"]').attr('placeholder',
        app.message.i18n('jsp.csp.status.search_placeholder'));
  };

  return {
    init : init
  };
}();

app.register(app.idpStatus);
