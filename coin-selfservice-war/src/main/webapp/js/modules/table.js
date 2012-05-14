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

app.table = function(){

    var init = function() {

        initEventHandlers();

    };

    var initEventHandlers = function() {

        initRowDetails();
        initTeamsFilter();

    };

    var initRowDetails = function() {

        $('.table').on('click', '.rowdetails td:first-child', function(event) {

            var row = $(event.target).closest('.rowdetails');
            var details = row.find('.rowdetails-content');

            var rowHeight = row.closest('table').find('tr').height();

            if(!row.hasClass('expanded')) {

                details[0]._originalHeight = details[0]._originalHeight || details.height();
                details.height(0);

                row.animate({
                    height: rowHeight + details[0]._originalHeight + 20
                })

                details.animate({
                    height: details[0]._originalHeight,
                    opacity: 1
                })
            } else {

                row.animate({
                    height: rowHeight
                })

                details.animate({
                    height: 0,
                    opacity: 0
                })
            }

            row.toggleClass('expanded');
        });
    };

    var initTeamsFilter = function() {

        var component = $('[data-component="teams-filter"]');

        if(component.length) {

            var tableStripeFix = function(table, reset) {
                if(reset) {
                    table.find('tr.existing').removeClass('odd even');
                } else {
                    table.find('tr.existing').filter(':odd').addClass('even'); // jQuery's ':odd' filter broken?!
                    table.find('tr.existing').filter(':even').addClass('odd');
                }

            }

            var table = component.siblings('table'),
                rows = table.find('tr.new'),
                selectedRadio = component.find(':radio:checked'),
                showAll = selectedRadio.val() === 'all';

            // Initial state
            tableStripeFix(table, showAll);
            rows.find('td').wrapInner('<span></span>');
            rows.css({ display: showAll ? 'table-row' : 'none' }).find('span').css({opacity: showAll ? 1 : 0});

            component.find(':radio').on('change', function() {

                showAll = component.find(':radio:checked').val() === 'all';

                if(showAll) {
                    tableStripeFix(table, showAll); // Fix before animation
                }

                rows.css({
                    display: 'table-row'
                }).find('span').animate({
                    opacity: showAll ? 1 : 0
                }, 800, function() {
                    if(!showAll) {
                        tableStripeFix(table, showAll);  // Fix after animation
                    }
                    rows.css({
                        display: showAll ? 'table-row' : 'none'
                    })
                })

            })

        }

    }

    return {
        init: init
    }

}();

app.register(app.table);
