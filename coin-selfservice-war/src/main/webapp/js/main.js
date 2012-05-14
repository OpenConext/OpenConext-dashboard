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

var app = {};

app.modules = [];

app.register = function(module) {

    app.modules[app.modules.length] = module;

}

app.bootstrap = function() {

    $.each(app.modules, function(i, module) {
        if(typeof module.init === 'function') {
            module.init.call(module);
        }
    })

}

app.loadPlugin = function(condition, url, callback) {

    if(condition) {

        $.getScript(url, callback);

    } else {

        callback.call();

    }

}

$(app.bootstrap);