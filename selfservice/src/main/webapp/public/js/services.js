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
		if (window.location['host'] == 'localhost:8282') {
      // Local development
			url = "http://localhost:8282/csa";
		} else {
			url = 'https://' + window.location.host;
		}
		return url;
	}

	var publicApiUrl = getUrlBase() + "/api/public/services.json";
	var protectedApiUrl = getUrlBase() + "/api/protected/services.json";

	var auth = new OAuth({
		  context:window,
		  clientId:"csa_example_gadget_app",
		  redirectUri: window.location,
		  authorizationEndpoint: "https://" + window.location.host.replace(/^csa\./, "apis.") + "/oauth2/authorize"
	});
	

	return {
        displayResult : function(data, htmlElement) {
        	var content = '<section class="services-list"><ul>';
        	var services = [];
        	jq.each(data, function (i, service) {
        		var serviceItem = '<li>';
        		if (service.logoUrl) {
        			serviceItem += "<img class='app-logo' src='" + service.logoUrl + "'>";
        		}
        		serviceItem += "<p>" + service.name + "</p>";

	          if (service.websiteUrl != undefined) {
	            serviceItem += "<div class='website-service'><a href='" + service.websiteUrl + "' target='_blank'><i class='icon-external-link'></i><span>" + service.websiteUrl + "</span></a></div>";
	          }
	          var license = (service.crmUrl != undefined);
	          var licenseCss = license ? "license" : "no-license";
	          var title = license ? "Deze dienst heeft een licentieovereenkomst met SURFMarket" : "Deze applicatie is mogelijk ook toegankelijk zonder SURFmarket licentie";
	          serviceItem += "<div class='" + licenseCss + "'>";
	          if (license) {
	            serviceItem += "<a href='" + service.crmUrl + "' target='_blank'>";
	          }
	          serviceItem += "<i title='"+title+"' class='icon-globe'></i>";
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
