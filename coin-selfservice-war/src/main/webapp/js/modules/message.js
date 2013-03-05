var app = app || {};

app.message = function() {

  var showInfo = function(message) {

    var closeLink = $('<a class="close" href="#" data-dismiss="alert">&times;</a>');
    var record = $("<div/>", {
      html : message,
      "class" : "alert alert-info fade in"
    }).append(closeLink).alert();

    $("#alerts").prepend(record);

  };

  var bundle = {
    "success.save" : {
      en : "Successfully saved.",
      nl : "Correct opgeslagen."
    },
    "failed.save" : {
      en : "Failed to save",
      nl : "Niet opgeslagen"
    },
    "appgrid.search.placeholder" : {
      en : "Search in applications...",
      nl : "Zoek in applicaties..."
    },
    "appgrid.filter.license" : {
      en : "License",
      nl : "Licentie"
    },
    "appgrid.filter.no-license" : {
      en : "No license",
      nl : "Geen licentie"
    },
    "appgrid.filter.connected" : {
      en : "Connected",
      nl : "Verbonden"
    },
    "appgrid.filter.not-connected" : {
      en : "Not connected",
      nl : "Niet verbonden"
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
    "stats.select_idp" : {
      en : "Select an Identity Provider",
      nl : "Selecteer een Identity Provider"
    },
    "stats.title.overview_default" : {
      en : "Logins",
      nl : "Logins"
    },
    "stats.title.overview_zoomed" : {
      en : "Logins for #{range}",
      nl : "Logins over #{range}"
    },
    "stats.title.sp_overview" : {
      en : "Logins for #{sp} (total #{total})",
      nl : "Logins van #{sp} (totaal #{total})"
    },
    "stats.title.sp_no_data" : {
      en : "No data available",
      nl : "Geen gegevens"
    },
    "stats.title.sp_zoomed" : {
      en : "Logins of #{sp} for #{range}",
      nl : "Logins van #{sp} over #{range}"
    },
    "stats.menu.months" : {
      en : "January|February|March|April|May|June|July|August|September|October|November|December",
      nl : "januari|februari|maart|april|mei|juni|juli|augustus|september|oktober|november|december"
    },
    "stats.short_months" : {
      en : "Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec",
      nl : "jan|feb|maa|apr|mei|jun|jul|aug|sep|okt|nov|dec"
    },

    "stats.logins_per_day" : {
      en : "Logins per day",
      nl : "Inloggen per dag"
    },
    "stats.logins" : {
      en : "Logins",
      nl : "Logins"
    },
    "stats.weekof" : {
      en : "Week #{week} of #{year}",
      nl : "Week #{week} van #{year}"
    },
    "stats.total_logins" : {
      en : "Total logins",
      nl : "Aantal inloggen"
    },
    "stats.reset_zoom" : {
      en : "Reset zoom",
      nl : "Zoom herstellen"
    },
    "jsp.notifications.too_many" : {
      en : "Too many notifications.",
      nl : "Te veel notificaties."
    },
    "jsp.search_placeholder" : {
      en : "Search...",
      nl : "Zoek..."
    },
    "jsp.recommendations.email_required" : {
      en : "At least one valid email is required .",
      nl : "Tenminste 1 valide email adres is verplicht."
    },
    "jsp.recommendations.loading_contacts" : {
      en : "Please wait, your group and member information is being loaded....",
      nl : "Even geduld, uw groep en leden informatie wordt opgehaald...."
    }
  };

  var i18n = function(key) {
    var lang = $('html').attr('lang').substring(0, 2);

    if (lang) {
      return bundle[key][lang];
    }
  };

  return {
    showInfo : showInfo,
    i18n : i18n
  };

}();

app.register(app.message);
