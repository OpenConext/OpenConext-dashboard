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

var app = app || {};

app.global = function() {

    var init = function() {

        initEventHandlers();
        initPlugins();

    };

    var initEventHandlers = function() {

        initUserSelection();

    };

    var initPlugins = function() {

        initTooltips();
        initPopovers();

    };

    var initUserSelection = function() {

        var form = $('.dropdown-menu');

        form.on('click', 'li', function(event) {

            var item = $(event.target).closest('li')[0];
            var roleId = item.getAttribute('data-roleId');

            $('<input>').attr({
                type: 'hidden',
                name: 'roleId',
                value: roleId
            }).appendTo(form);

            form[0].submit();

        })

    };

    var _placement = function(popup, element) {
        popup.setAttribute('data-type', element.getAttribute('data-type'))
        return 'top';
    }

    var initTooltips = function() {

       	$('[rel="tooltip"]').tooltip({
            placement: _placement
        });

    };

    var initPopovers = function() {

        $('[rel="popover"]').popover({
            placement: _placement
        });

    };

    return {
        init: init
    };

}();

app.register(app.global);
