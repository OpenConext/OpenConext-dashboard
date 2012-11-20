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
        },
        "show.all.attributes" : {
            en : "Show all…",
            nl : "Alles tonen…"
        },
        "stats.title.overview_default" : {
            en : "Stats",
            nl : "Statistieken"
        },
        "stats.title.overview_zoomed" : {
            en : "Stats for #{range}",
            nl : "Statistieken over #{range}"
        },
        "stats.title.sp_overview" : {
            en : "Stats for #{sp}",
            nl : "Statistieken van #{sp}"
        },
        "stats.title.sp_zoomed" : {
            en : "Stats of #{sp} for #{range}",
            nl : "Statistieken van #{sp} over #{range}"
        },
        "stats.menu.all" : {
            en : "All",
            nl : "Alles"
        },
        "stats.menu.quarter" : {
            en : "Quearter",
            nl : "Kwartaal"
        },
        "stats.menu.month" : {
            en : "Month",
            nl : "Maand"
        },
        "stats.menu.week" : {
            en : "Week",
            nl : "Week"
        },
        "stats.menu.months" : {
            en : "January|February|March|April|May|June|July|August|September|October|November|December",
            nl : "januari|februari|maart|april|mei|juni|juli|augustus|september|oktober|november|december"
        },
        "stats.logins_per_day" : {
            en : "Logins per day",
            nl : "Inloggen per dag"
        },
        "stats.days" : {
            en : "Days",
            nl : "Dagen"
        },
        "stats.weekof" : {
            en : "Week #{week} of #{year}",
            nl : "Week #{week} van #{year}"
        },
        "stats.total_logins" : {
            en : "Total logins",
            nl : "Aantal inloggen"
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

