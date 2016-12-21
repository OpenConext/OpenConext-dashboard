var app = app || {};

app.spServices = function () {

  var init = function () {
    var servicesTable = $('#sp_overview_table');

    if (servicesTable.length === 0) {
      return;
    }

    var toggleFormElements = function(checkboxes) {
      checkboxes.each(function(){
        var me = $(this);
        var formElements = me.closest('tr').find("form[id^='form-normenkader']").find('input,button');
        me.is(':checked') ? formElements.removeProp("disabled") : formElements.prop('disabled','disabled') ;
      });
    };

    toggleFormElements($('#sp_overview_table').find("input[type='checkbox'][name='normenkaderPresent']"));

    var performAjaxUpdate = function(elem, methodPart, newValue) {
      var $elm = $(elem);
      var tokencheck = $elm.parent("td").find("input[name='tokencheck']").val();
      var cspId = $elm.data('compound-service-provider-id');
      $.ajax(methodPart + "/" + cspId + "/" + newValue + ".shtml?tokencheck=" + tokencheck,
        {
          type: "PUT"
        })
        .done(function (data) {
          var $mess = $("<span>" + app.message.i18n('success.save') + "</span>");
          $elm.before($mess);
          $mess.fadeOut(1750);
        })
        .fail(function (data) {
          var $mess = $("<span>" + app.message.i18n('failed.save') + "</span>");
          $elm.before($mess);
        });
    };

    $('#sp_overview_table').find("input[type='checkbox'][name='normenkaderPresent']").click(function () {
      toggleFormElements($(this));
      performAjaxUpdate(this, "update-normenkader-present", $(this).is(':checked'));
    });

    $('#sp_overview_table').find("input[type='checkbox'][name='strongAuthentication']").click(function () {
      performAjaxUpdate(this, "update-strong-authentication", $(this).is(':checked'));
    });

    $('#sp_overview_table').find("select[name='licenseStatus']").change(function () {
      performAjaxUpdate(this, "update-license-status", this.value);
    });

  };

  return {
    init:init
  }
}();

app.register(app.spServices);
