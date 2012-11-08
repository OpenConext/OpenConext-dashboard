var app = app || {};

app.forms = function() {
  var init = function() {
    initIdpLmngList();
    
  };


  var initIdpLmngList = function() {
    function editSubmit(e) {
      var submitClicked = $(this),
          formInUse = submitClicked.closest('form'),
          inputField = $('.lmngIdentifier', formInUse),
          regexp = /\{[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}\}/;

      if (!inputField.val() || !regexp.test(inputField.val())) {
        e.preventDefault();

        if (inputField.hasClass('error')) {
          return;
        }

        inputField.addClass('error');
        formInUse.append('<p class="error">' + app.message.i18n('lmng.identifier.error') + '</p>');
      }
    }

    function confirmDeletion(e) {
      if (!e.isDefaultPrevented()) {
        $(this).closest('form').get(0).submit();
      }
    }

    var forms = $('.lmng-id-edit');
    forms.on('submit', function(e){ e.preventDefault(); });
    $('button[name="submitbutton"]', forms).on('click', editSubmit);
    $('button[name="clearbutton"]', forms).on('click', confirmDeletion);
  };


  return {
    init : init
  };
}();

app.register(app.forms);
