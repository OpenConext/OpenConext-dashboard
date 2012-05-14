/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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