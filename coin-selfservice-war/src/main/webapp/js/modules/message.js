var app = app || {};

app.message = function() {

    var showInfo = function(message) {

        var closeLink = $('<a class="close" href="#" data-dismiss="alert">&times;</a>');
        var record = $("<div/>", {html: message, "class": "alert alert-info fade in"}).append(closeLink).alert();

        $("#alerts").prepend(record);

    };
    
    var bundle = { "success.save" : {"en" : "Successfully saved.", "nl" : "Correct opgeslagen." }};
    
    var i18n = function(key) {
    	var lang = $("span#locale_conext").attr("class");
    	if (lang) {
    		return bundle[key][lang];
    	}
    }

    return {
        showInfo: showInfo,
        i18n: i18n
    };

}();

app.register(app.message);

