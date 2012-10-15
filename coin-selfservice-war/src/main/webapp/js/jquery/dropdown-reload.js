(function($){
	$.fn.dropdownReload = function(dataUrl, otherSelect, options) {
		var defaults = {
		    afterReload: function(data){},
		    valueProp: "id",
		    displayProp: "name"
		};
		var opts = $.extend(defaults, options);
		var inputSelect = this;

		inputSelect.change(function() {
			$.getJSON(dataUrl.replace("{}", inputSelect.val()), function(responseData) {
				var options = $.map(responseData, function(object, i) {
					return '<option value="'+object[opts.valueProp]+'" data-bandwidth-max="'+object['maxBandwidth']+'">'+object[opts.displayProp]+'</option>';
				});
                otherSelect.each(function(i, select) {
                    $(select).html(options.join(""));
                    selectedIndex = options.length >= i ? i :options.length;
                    $(select).prop('selectedIndex', selectedIndex);
                });
				opts.afterReload.call(this, responseData);
			});
		});
	};
})(jQuery);