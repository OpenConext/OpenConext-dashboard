<%@ include file="include.jsp" %>


<!DOCTYPE html>
<%--
  Copyright 2012 SURFnet bv, The Netherlands

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  --%>

<html lang="en">
    <head>
        <meta charset="UTF-8"/>
        <title>Bandwidth On Demand</title>

      <c:choose>
          <c:when test="${dev eq true}">
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-2.0.2.min.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/font-awesome.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-alert.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-button.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-datepicker.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-dropdown.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-form.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-generic.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-navbar.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-pagination.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-popover.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-table.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-tooltip.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/bootstrap-modal.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/layout.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/generic.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/component-userbox.css"/>"/>
            <link rel="stylesheet" href="<c:url value="/css/component-autoSuggest.css"/>"/>
          </c:when>
          <c:otherwise>
            <link rel="stylesheet" href="<c:url value="/css/style.min.css"/>"/>
          </c:otherwise>
        </c:choose>

        <!--[if lt IE 9]>
        <script src="<c:url value="/js/tools/html5shiv.js"/>"></script>
        <![endif]-->

    </head>
    <body>

        <div class="wrapper">

            <header>
                <h1>Bandwidth on Demand</h1>
            </header>

            <div class="row">

                <div class="span8">

                    <section>

                        <h2>Form</h2>

                        <div class="content">

                            <h3 class="h3">Fill in the form</h3>

                            <form class="form form-horizontal">

                                <fieldset>

                                    <div class="control-group">
                                        <label class="control-label">Text input (default)</label>
                                        <div class="controls">
                                            <input type="text" class="input-xlarge">
                                        </div>
                                    </div>

                                    <div class="control-group error">
                                        <label class="control-label">Text input (w/ error)</label>
                                        <div class="controls">
                                            <input type="text" class="input-xlarge">
                                            <p class="help-block">Error message</p>
                                        </div>
                                    </div>

                                    <div class="control-group warning">
                                        <label class="control-label">Text input (w/ warning)</label>
                                        <div class="controls">
                                            <input type="text" class="input-xlarge">
                                            <p class="help-block">Warning message</p>
                                        </div>
                                    </div>

                                    <div class="control-group success">
                                        <label class="control-label">Text input (w/ success)</label>
                                        <div class="controls">
                                            <input type="text" class="input-xlarge">
                                            <p class="help-block">Success message</p>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Label (w/ plain text)</label>
                                        <div class="controls">
                                            <output>Just content, no form control</output>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (w/ placeholder)</label>
                                        <div class="controls">
                                            <input type="email" class="input-xlarge" placeholder="Fill me in...">
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (w/ help text)</label>
                                        <div class="controls">
                                            <input type="text" class="input-xlarge">
                                            <span class="help-inline">Inline help.</span>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (w/ help text)</label>
                                        <div class="controls">
                                            <input type="text" class="input-xlarge">
                                            <p class="help-block">Block help.</p>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (w/ icon)</label>
                                        <div class="controls">
                                            <input type="text" class="input-xlarge">
                                            <span class="help-inline"><i class="icon-ok"></i></span>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (w/ append)</label>
                                        <div class="controls">
                                            <div class="input-append">
                                                <input type="text" class="input-xlarge"><span class="add-on">Mbps</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (w/ append)</label>
                                        <div class="controls">
                                            <div class="input-append">
                                                <input type="text" class="input-xlarge"><span class="add-on">y-m-d</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (w/ label)</label>
                                        <div class="controls">
                                            <input type="text" class="input-xlarge">
                                            <span class="label label-success">20</span>
                                        </div>
                                    </div>

                                    <div class="message">
                                        <h3>Message</h3>
                                        <p>Message, <strong>strong message</strong>, message content.</p>
                                    </div>

                                    <div class="message">
                                        <h3>Message</h3>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (w/ badge)</label>
                                        <div class="controls">
                                            <input type="text" class="input-xlarge">
                                            <span class="badge badge-success">20</span>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (w/ badge + popover)</label>
                                        <div class="controls">
                                            <input type="text" class="input-xlarge">
                                            <a href="#" class="badge badge-success" rel="popover" data-type="success" data-content="And here's some amazing content. It's very engaging. right?">?</a>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (w/ auto-suggest)</label>
                                        <div class="controls">
                                            <input
                                                name="${input_search_name}"
                                                type="text"
                                                data-component="autoSuggest"
                                                data-prefillId=""
                                                data-prefillName=""
                                                data-suggestUrl="stub.autosuggest.json"  />
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (date + datepicker)</label>
                                        <div class="controls">
                                            <div class="input-append">
                                                <input type="text" class="input-date input-datepicker"><span class="add-on">yyyy-mm-dd</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (disabled)</label>
                                        <div class="controls">
                                            <div class="input-append">
                                                <input type="text" class="input-date" disabled><span class="add-on">yyyy-mm-dd</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Text input (time)</label>
                                        <div class="controls">
                                            <div class="input-append">
                                                <input type="text" class="input-time"><span class="add-on">hh:mm</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="control-group" data-component="start-now">
                                        <label class="control-label">Text input (date + time)</label>
                                        <div class="controls">
                                            <div class="input-append">
                                                <input type="text" class="input-date-with-time" value="2013-12-31"><span class="add-on">yyyy-mm-dd</span>
                                                <input type="text" class="input-time-with-date" value="12:01"><span class="add-on">hh:mm</span>
                                                <span class="help-inline">
                                                    <label class="checkbox inline">
                                                        <input type="checkbox" value="now"> now
                                                    </label>
                                                </span>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Textarea</label>
                                        <div class="controls">
                                            <textarea class="input-xlarge" rows="3"></textarea>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Select (team-selector)</label>
                                        <div class="controls">
                                            <select
                                                class="input-xlarge"
                                                name="virtualResourceGroup"
                                                data-component="team-selector"
                                                data-url="stub.ports.json">
                                                <option value="1">Team 1</option>
                                                <option value="2">Team 2</option>
                                            </select>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Select</label>
                                        <div class="controls">
                                            <select class="input-xlarge" data-component="bandwidth-selector-source">
                                                <option data-bandwidth-max="200">Option A (max-bandwidth: 200)</option>
                                                <option data-bandwidth-max="500">Option B (max-bandwidth: 500)</option>
                                            </select>
                                        </div>
                                    </div>

                                    <div class="control-group">
                                        <label class="control-label">Select</label>
                                        <div class="controls">
                                            <select class="input-xlarge" data-component="bandwidth-selector-source">
                                                <option data-bandwidth-max="1500">Option C (max-bandwidth: 1500)</option>
                                                <option data-bandwidth-max="400">Option D (max-bandwidth: 400)</option>
                                                <option data-bandwidth-max="125">Option E (max-bandwidth: 125)</option>
                                            </select>
                                        </div>
                                    </div>

                                    <div class="control-group" data-component="bandwidth-selector">
                                        <label class="control-label">Bandwidth</label>
                                        <div class="controls">
                                            <input type="text" class="input-mini" value="0">
                                            <span class="help-inline">
                                                <div class="btn-group">
                                                  <button class="btn btn-mini" data-bandwidth-multiplier="0.25">Low</button>
                                                  <button class="btn btn-mini" data-bandwidth-multiplier="0.5">Med</button>
                                                  <button class="btn btn-mini" data-bandwidth-multiplier="1">High</button>
                                                </div>
                                            </span>
                                        </div>
                                    </div>

                                    <div class="actions">
                                        <button type="submit" class="btn btn-primary">Send</button>
                                        <a href="#">Cancel</a>
                                    </div>

                                </fieldset>

                            </form>

                        </div>

                    </section>
                </div>

                <div class="span4">

                    <section>
                        <h2>Help</h2>
                        <div class="content">
                            <h3>First Step</h3>
                            <p>Paragraph in right column</p>
                            <h3>Second Step</h3>
                            <p>Paragraph in right column</p>
                        </div>
                    </section>

                </div>
            </div>
        </div>

        <script src="<c:url value="/js/jquery/jquery-1.7.2.min.js"/>"></script>
        <script src="<c:url value="/js/bootstrap/bootstrap-2.0.2.min.js"/>"></script>
        <c:choose>
          <c:when test="${dev eq true}">
            <script src="<c:url value="/js/main.js"/>"></script>
            <script src="<c:url value="/js/modules/global.js"/>"></script>
            <script src="<c:url value="/js/modules/form.js"/>"></script>
            <script src="<c:url value="/js/modules/message.js"/>"></script>
            <script src="<c:url value="/js/modules/table.js"/>"></script>
            <script src="<c:url value="/js/modules/reservation.js"/>"></script>
          </c:when>
          <c:otherwise>
            <script src="<c:url value="/js/script.min.js"/>"></script>
            </c:otherwise>
                </c:choose>

            <spring:url var="url_plugin_socket" value="/js/jquery/jquery-socket-1.0a.js"/>
                <spring:url var="url_plugin_autoSuggest" value="/js/jquery/jquery-autoSuggest.js"/>
                <spring:url var="url_plugin_datepicker" value="/js/datepicker/bootstrap-datepicker.js"/>
                <spring:url var="url_plugin_dropdownReload" value="/js/jquery/dropdown-reload.js"/>

                <script>
                app.plugins = {
              jquery:{
                socket:'<c:out value="${url_plugin_socket}"/>',
                autoSuggest:'<c:out value="${url_plugin_autoSuggest}"/>',
                datepicker:'<c:out value="${url_plugin_datepicker}"/>',
                dropdownReload:'<c:out value="${url_plugin_dropdownReload}"/>'
              }
            }
            </script>
    </body>
</html>

