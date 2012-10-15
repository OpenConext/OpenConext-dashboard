var app = app || {};

app.message = function() {

    var showInfo = function(message) {

        var closeLink = $('<a class="close" href="#" data-dismiss="alert">&times;</a>');
        var record = $("<div/>", {html: message, "class": "alert alert-info fade in"}).append(closeLink).alert();

        $("#alerts").prepend(record);

    };

    return {
        showInfo: showInfo
    };

}();

app.register(app.message);

