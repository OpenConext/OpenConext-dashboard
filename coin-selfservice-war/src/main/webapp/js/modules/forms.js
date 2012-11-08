var app = app || {};

app.forms = function() {
  var init = function() {
    initIdpLmngList();
    
  };


  var initIdpLmngList = function() {
    var buttonPressed = null;

    function editSubmit() {
      buttonPressed = 'submit';
    }

    function confirmDeletion() {
      buttonPressed = 'clear';
    }

    function performSubmit(e) {
      if (buttonPressed === 'submit') {
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
    }

    var forms = $('.lmng-id-edit');
    $('button[name="submitbutton"]', forms).on('click', editSubmit);
    $('button[name="clearbutton"]', forms).on('click', confirmDeletion);
    forms.on('submit', performSubmit);
  };


  return {
    init : init
  };
}();

app.register(app.forms);
