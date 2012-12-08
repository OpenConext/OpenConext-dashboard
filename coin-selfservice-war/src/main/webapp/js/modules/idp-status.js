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

  };

  return {
    init : init
  };
}();

app.register(app.idpStatus);
