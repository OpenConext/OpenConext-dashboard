//capture the jQuery 1.9 version
var jq19 = jQuery.noConflict();

var displayElement;
    
    function displayResult(data) {
      console.log('displaying results in ' + displayElement);  
      
      var content = '<section id="personal" class="services-list"><ul>'
          var services = [];
          jq19.each(data, function (i, service) {
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
          content += '</ul></section>'
          displayElement.html(content);
      
    }
    
   jq19("#personal-tab").click(function() {
     console.log('switching to personal tab');
     displayElement = jq19("#personal");
     jq19.ajax({url:'http://openconext.github.io/OpenConext-selfservice/api/public/services.json?callback=servicesCallback', dataType:"jsonp", jsonpCallback:"servicesCallback"})
        .done(displayResult).fail(function(jqxhr, textstatus){
          console.log('textstatus ' + textstatus);
        });
     
   });
   
   jq19("#public-tab").click(displayPublic);
   
   jq19("#idp-tab").click(function() {
     console.log('switching to institution tab');
     displayElement = jq19("#idp");
     jq19.ajax({url:'http://openconext.github.io/OpenConext-selfservice/api/public/services.json?callback=servicesCallback', dataType:"jsonp", jsonpCallback:"servicesCallback"})
        .done(displayResult).fail(function(jqxhr, textstatus){
          console.log('textstatus ' + textstatus);
        });
   });
   
   
   function displayPublic() {
     console.log('switching to public tab');
         displayElement = jq19("#public");
         jq19.ajax({url:'http://openconext.github.io/OpenConext-selfservice/api/public/services.json?callback=servicesCallback', dataType:"jsonp", jsonpCallback:"servicesCallback"})
            .done(displayResult).fail(function(jqxhr, textstatus){
              console.log('textstatus ' + textstatus);
            });
   }
   
   jq19(displayPublic);