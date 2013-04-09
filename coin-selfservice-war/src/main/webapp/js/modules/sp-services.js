var app = app || {};

app.spServices = function () {

  var init = function () {
    var servicesTable = $('#sp_overview_table');

    if (servicesTable.length === 0) {
      return;
    }

    $('#sp_overview_table').find("input[type='checkbox'][name='hideInPublicShowroom']").click(function () {
      var $elm = $(this);
      var tokencheck = $elm.parent("td").find("input[name='tokencheck']").val();
      var value = $elm.is(':checked');
      var cspId = $elm.data('compound-service-provider-id');
      $.ajax("update-csp-public-api/" + cspId + "/" + value +".shtml?tokencheck=" + tokencheck,
        {
          type:"PUT"
        })
        .done(function(data){
          var $mess = $("<span>"+app.message.i18n('success.save')+"</span>");
          $elm.before($mess);
          $mess.fadeOut(750);
        })
        .fail(function(data){
          var $mess = $("<span>"+app.message.i18n('failed.save')+"</span>");
          $elm.before($mess);
        });
    });

  };

  return {
    init:init
  };
}();

app.register(app.spServices);
