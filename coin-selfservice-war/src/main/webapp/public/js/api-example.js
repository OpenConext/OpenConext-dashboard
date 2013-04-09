var apiServicesExample = {

  bootstrap:function () {

    $.getJSON('/selfservice/api/public/services.json')
      .done(function (data) {
        var content = '<ul>'
        var services = [];
        $.each(data, function (i, service) {
          var serviceItem = '<li>';
          serviceItem += "<img src='" + service.logo_url_service + "'>";
          serviceItem += "<p><strong>" + service.name + "</strong></p>";

          var clazz = service.is_surfmarket_connected ? 'license-connect' : 'license-not-needed';
          var txt = service.is_surfmarket_connected ? 'Licentie aanwezig' : 'Geen licentie noodzakelijk';
          serviceItem += "<div class='" + clazz + "'>"
          if (service.website_service != undefined) {
            serviceItem += "<a href='" + service.website_service + "'><i class='icon-external-link'></i></a>";
          }
          serviceItem += "<span>" + txt + "</span></div> ";
          serviceItem += '</li>';
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