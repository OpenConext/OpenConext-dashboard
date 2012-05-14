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

app.reservation = function() {

    var table, url;

    var init = function() {

        table = $('[data-component="reservation"]');
        url = table.attr('data-url');

        if(table.length && url) {

            app.loadPlugin(!$.socket, app.plugins.jquery.socket, startPushConnection);

        }

    };

    var startPushConnection = function() {

        /* dev/stub code */
        if(url.indexOf('stub') !== -1) {
            setTimeout(function() {
                $.getJSON(url, processEvent);
            }, 1000)
            return;
        }
        /* dev/stub code */

        $.socket(url, {
            transports: "longpoll"
        })
        .open(function(){})
        .message(function(data) {
            processEvent(data);
        })
        .close(function(){});

    };

    var processEvent = function(event) {

        app.message.showInfo(event.message);
        updateReservationRow(event.id, event.status);

    };

    var updateReservationRow = function(id, newStatus) {

        var row = $('tr[data-reservationId="'+id+'"]'),
            cell = row.find('td.status').wrapInner('<span></span>'),
            span = cell.find('span');

        cell.css({
            overflow: 'hidden'
        })

        span.delay(500).animate(
            {
                opacity: 0,
                marginLeft: -130
            },
            1000,
            function() {
                span.text(newStatus);
                span.animate(
                    {
                        opacity: 1,
                        marginLeft: 0
                    },
                    1000
                );
            }
        );
    };

    return {
        init: init
    };

}();

app.register(app.reservation);
