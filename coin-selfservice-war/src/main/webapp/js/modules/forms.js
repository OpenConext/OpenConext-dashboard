var app = app || {};

app.forms = function() {

    var init = function() {
    	initIdpLmngList();
    	
    };
    
	var initIdpLmngList = function() {
		function editSubmit(e) {
			var inputField = $(this).find(".lmngIdentifier");
			var regexp = /\{[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}\}/;
			if(inputField.val() != null && inputField.val() != '' && !regexp.test(inputField.val())) {
				e.preventDefault();
				inputField.addClass("error");
				inputField.closest("form").append("<p class='error'>" + app.message.i18n('lmng.identifier.error') + "</p>")
			}
		}
		$(".lmng-id-edit").submit(editSubmit)
	};

    return {
        init : init
    };


}();

app.register(app.forms);
