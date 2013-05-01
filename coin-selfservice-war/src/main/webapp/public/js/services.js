//capture the jQuery 1.8 version
var jq = jQuery.noConflict();

var services = function() {
	var tokenInfo;	
	
	var oauthAjax = function(options) {
	  if (options.beforeSend) {
		var originalBeforeSend = options.beforeSend;
	  }
	  options.beforeSend = function(xhr, settings) {
	    originalBeforeSend && originalBeforeSend(xhr, settings);
	    xhr.setRequestHeader('Authorization', "Bearer " + options.accessToken);
	  };
	  return jq.ajax(options);
	};
	
	function getUrlBase() {
		var url = "";
		if (window.location['host'] == 'localhost:8280') {			
			url = 'http://';
			url += window.location['host'];
			url += '/selfservice';
		} else {
			url = 'https://showroom.test.showroom.surfconext.nl';
		}
		return url;
	};
	
	function getEndpoint(protected) {
		url = getUrlBase();
		url += '/api/';
		if (protected) {
			url += 'protected';
		} else {
			url += 'public';
		}
		url += '/services.json';
		return url;
	};
	
	var publicApiUrl = getEndpoint(false);
	var protectedApiUrl = getEndpoint(true);
	var baseUrl = window.location;
	
	var auth = new OAuth({
		  context:window,
		  scope:"read",
		  clientId:"cdk_example_gadget_app",
		  redirectUri:baseUrl,
		  authorizationEndpoint:"https://apis.test.showroom.surfconext.nl/oauth2/authorize"
	});
	

	return {
		displayResult : function(data, htmlElement) {	    
	    var content = '<section class="services-list"><ul>';
	        var services = [];
	        jq.each(data, function (i, service) {
	          var serviceItem = '<li>';
	          serviceItem += "<img class='app-logo' src='" + service.logo_url_service + "'>";
	          serviceItem += "<p>" + service.name + "</p>";

	          if (service.website_service != undefined) {
	            serviceItem += "<div class='website-service'><a href='" + service.website_service + "' target='_blank'><i class='icon-external-link'></i><span>" + service.website_service + "</span></a></div>";
	          }
	          var license = (service.surfmarket_url != undefined);
	          var licenseCss = license ? "license" : "no-license";
	          var title = license ? "Deze dienst heeft een licentieovereenkomst met SURFMarket" : "Deze applicatie is mogelijk ook toegankelijk zonder SURFmarket licentie";
	          serviceItem += "<div class='" + licenseCss + "'>";
	          if (license) {
	            serviceItem += "<a href='" + service.surfmarket_url + "' target='_blank'>";
	          }
	          serviceItem += "<i title='"+title+"' class='icon-cloud-upload'></i>";
	          if (license) {
	            serviceItem += "</a>";
	          }
	          serviceItem += "</div>";
	          if (service.name) {
	            services.push(serviceItem);
	          }
	        });
	        content += services.join('');
	        content += '</ul></section>';
	        htmlElement.html(content);
	  },
	
	displayPublic : function() {
		   
	   htmlElement = jq("#public");
	       jq.ajax({url:publicApiUrl, dataType:"json"})
	          .done(function(data) {
	        	  services.displayResult(data, htmlElement);
	        	  }).fail(function(jqxhr, textstatus){
	            console.log('textstatus ' + textstatus);
	          });
	 },
	
	displayPersonal : function() {
		services.checkForToken('personal');
	  jq("#personal").html('Services I have recently used:');	   
	},
	
	checkForToken : function(state) {
		  if (auth.isTokenPresent()) {
			  tokenInfo = auth.extractTokenInfo();
			    authenticationToken = tokenInfo.accessToken;
			 
		  } else if (!tokenInfo){
			  // give away control
			  auth.authorize(state);
		  }
	},
	
	displayInstitution : function() {
		services.checkForToken('idp');
	  htmlElement = jq("#idp");
	    // apparently we got back control after authentication
  	    oauthAjax({accessToken: tokenInfo.accessToken, url:protectedApiUrl, dataType:"json"})
          .done(function(data){
        	  services.displayResult(data, htmlElement);
          }).fail(function(jqxhr, textstatus){
		     	console.log('textstatus ' + textstatus);
		   });
	},
	
	isTokenPresent : function() {
		return auth.isTokenPresent();
	},
	
	extractTokenInfo : function() {
		tokenInfo = auth.extractTokenInfo();
		return tokenInfo;
	},
	
	showTab : function(state) {
		if (state == 'idp') {
			services.displayInstitution();
			jq("#idp-tab a:last").tab('show');
		}
	},
	
	};
}();
    
 
 jq(function() {
	if (services.isTokenPresent()) {
		//get state
		tokenInfo = services.extractTokenInfo();
		services.showTab(tokenInfo.state);
		//goto right tab
	} else {
		// switch to public tab
		services.displayPublic();
		jq("#public-tab a:last").tab('show');
	}
 });
 
 jq("#personal-tab").click(services.displayPersonal);
 jq("#public-tab").click(services.displayPublic);
 jq("#idp-tab").click(services.displayInstitution);