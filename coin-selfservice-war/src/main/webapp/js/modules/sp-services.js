var app = app || {};

app.spServices = function () {

  var init = function () {
    var servicesTable = $('#sp_overview_table');

    if (servicesTable.length === 0) {
      return;
    }

    var performAjaxUpdate = function(elem, methodPart) {
      var $elm = $(elem);
      var tokencheck = $elm.parent("td").find("input[name='tokencheck']").val();
      var value = $elm.is(':checked');
      var cspId = $elm.data('compound-service-provider-id');
      $.ajax("update-csp-" + methodPart + "-api/" + cspId + "/" + value + ".shtml?tokencheck=" + tokencheck,
        {
          type: "PUT"
        })
        .done(function (data) {
          var $mess = $("<span>" + app.message.i18n('success.save') + "</span>");
          $elm.before($mess);
          $mess.fadeOut(750);
        })
        .fail(function (data) {
          var $mess = $("<span>" + app.message.i18n('failed.save') + "</span>");
          $elm.before($mess);
        });
    }

    $('#sp_overview_table').find("input[type='checkbox'][name='hideInPublicShowroom']").click(function () {
      performAjaxUpdate(this, "public");
    });

    $('#sp_overview_table').find("input[type='checkbox'][name='hideInProtectedShowroom']").click(function () {
      performAjaxUpdate(this, "protected");
    });

  };

  return {
    init:init
  };
}();

app.register(app.spServices);
