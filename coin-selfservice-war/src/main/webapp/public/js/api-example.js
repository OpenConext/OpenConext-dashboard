var apiServicesExample = {

  bootstrap:function () {

    $.getJSON('/selfservice/api/public/services.json')
      .done(function (data) {
        var content = '<ul>'
        var services = [];
        $.each(data, function (i, service) {
          var serviceItem = '<li>';
          serviceItem += "<img class='app-logo' src='" + service.logo_url_service + "'>";
          serviceItem += "<p>" + service.name + "</p>";

          if (service.website_service != undefined) {
            serviceItem += "<div class='website-service'><a href='" + service.website_service + "' target='_blank'><i class='icon-external-link'></i><span>" + service.website_service + "</span></a></div>";
          }
          var license = (service.surfmarket_url != undefined)
          var licenseCss = license ? "license" : "no-license"
          var title = license ? "Deze dienst heeft een licentieovereenkomst met SURFMarket" : "Deze applicatie is mogelijk ook toegankelijk zonder SURFmarket licentie";
          serviceItem += "<div class='" + licenseCss + "'>"
          if (license) {
            serviceItem += "<a href='" + service.surfmarket_url + "' target='_blank'>"
          }
          serviceItem += "<i title='"+title+"' class='icon-cloud-upload'></i>";
          if (license) {
            serviceItem += "</a>"
          }
          serviceItem += "</div>"
          if (service.name) {
            services.push(serviceItem);
          }
        });
        content += services.join('');
        content += '</ul>'
        $(".api-example").html(content);
      });

  }

};

$(apiServicesExample.bootstrap);