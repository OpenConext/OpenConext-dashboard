var app = app || {};

app.message = function() {

    var showInfo = function(message) {

        var closeLink = $('<a class="close" href="#" data-dismiss="alert">&times;</a>');
        var record = $("<div/>", {html: message, "class": "alert alert-info fade in"}).append(closeLink).alert();

        $("#alerts").prepend(record);

    };
    
    var bundle = {
        "success.save" : {
            en : "Successfully saved.",
            nl : "Correct opgeslagen."
        },
        "failed.save" : {
            en : "Failed to save.",
            nl : "Niet opgeslagen"
        },
        "appgrid.search.placeholder" : {
            en : "Search in applications",
            nl : "Zoek in applicaties"
        },
        "appgrid.filter.haslicense" : {
            en: "Has license",
            nl: "Heeft licentie"
        },
        "appgrid.filter.isconnected" : {
            en : "Is connected",
            nl : "Is verbonden"
        },
        "arp.button.text" : {
            en : " Attributes that are shared",
            nl : " Attributen die worden gedeeld"
        },
        "lmng.identifier.error" : {
            en : "Wrong format for LMNG ID (be sure to include the brackets)",
            nl : "Verkeerde LMNG ID (let op: accolades zijn verplicht)"
        }
    };
    
    var i18n = function(key) {
        var lang = $('html').attr('lang').substring(0, 2);
        
        if (lang) {
            return bundle[key][lang];
        }
    };

    return {
        showInfo: showInfo,
        i18n: i18n
    };

}();

app.register(app.message);

