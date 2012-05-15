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

<!doctype html>
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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta content="width=device-width,initial-scale=1" name="viewport"/>
  <title>SURFconext - Selfservice home</title>

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
  <a href="<c:url value="/"/>">Home</a>

  <h1>SURFconext Self service</h1>
</header>

<section class="user-box content-dense">
  <span class="user-name">ReneS</span>
  <a href="#" class="logout">Logout <i class="icon-signout"></i></a>

  <!-- b:dropdown -->

  <div class="dropdown">

    <div class="dropdown-toggle user-role-noc" data-toggle="dropdown">
      <div class="user">
        NOC Engineer<br>
        Institute A
        <b class="caret"></b>
      </div>
    </div>

    <form action="#" method="POST" class="dropdown-menu">
      <ul>
        <li class="user-role-default" data-roleId="default">
          <div class="user">
            Default/New User
          </div>
        </li>
        <li class="user-role-manager" data-roleId="manager">
          <div class="user">
            Manager/Admin<br>
            Institute C
          </div>
        </li>
        <li class="user-role-user" data-roleId="user">
          <div class="user">
            BoD User<br>
            Some institute with a very long and uncommon name
          </div>
        </li>
        <li class="user-role-noc" data-roleId="noc">
          <div class="user">
            NOC Engineer
          </div>
        </li>
      </ul>
    </form>

  </div>

</section>

<!-- b:navbar -->

<nav class="navbar">
  <div class="navbar-inner">
    <div class="container">
      <ul class="nav">
        <li><a href="#">Overview</a></li>
        <li><a href="#">Reservations</a></li>
        <li><a href="#">Virtual ports</a></li>
        <li class="active"><a href="#">Physical ports</a></li>
        <li><a href="#">Teams</a></li>
      </ul>
    </div>
  </div>
</nav>

<div id="alerts">

  <div class="alert">
    <a class="close" data-dismiss="alert">&times;</a>
    Some message to warn the user.
  </div>
  <div class="alert alert-error">
    <a class="close" data-dismiss="alert">&times;</a>
    Some error message to tell the user the world has come to an end.
  </div>
  <div class="alert alert-success">
    <a class="close" data-dismiss="alert">&times;</a>
    Some message to tell the user the action <b>Save</b> was a success.
  </div>
  <div class="alert alert-info">
    <a class="close" data-dismiss="alert">&times;</a>
    Informing the user <b>Truus</b> about something...
  </div>

</div>

<section>

  <h2>Links that do a form post</h2>

  <div class="content">
    <a href="form.html?id=12&type=new" class="btn btn-primary" data-form="true">Post request</a>
    <a href="form.html" class="btn btn-primary" data-form="true" data-confirm="Are you sure?">Post request with a
      confirm</a>
    <a href="form.html" class="btn btn-primary" data-form="true" data-success="The post request was successfull">Post
      request with a succes message</a>
    <a href="form.html" data-form="true" data-confirm="Just press ok and see the message!"
       data-success="Yes we did it again!!">Post with confirm and success message</a>
  </div>

</section>

<section>

  <h2>Default content</h2>

  <div class="content">
    <p>Paragraph, with unordered list:</p>
    <ul>
      <li>
        <a href="form.html">Form</a>
      </li>
      <li>
        <a href="reservation.html">Reservation table</a>
      </li>
    </ul>
  </div>

</section>

<section>

  <h2>Label &amp; Icon Usage</h2>

  <div class="content">

    <p>Paragraph, with unordered list:</p>

    <table class="table">
      <thead>
      <tr>
        <th>Action / Label</th>
        <th>Icon</th>
        <th>Class (glyphicon)</th>
        <th>Remark</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>New</td>
        <td><i class="icon-plus"></i></td>
        <td>icon-plus</td>
        <td>Not e.g. &quot;Add&quot;, or &quot;Create&quot;</td>
      </tr>
      <tr>
        <td>Edit</td>
        <td><i class="icon-pencil"></i></td>
        <td>icon-pencil</td>
        <td>Not e.g. &quot;Change&quot;</td>
      </tr>
      <tr>
        <td>Allocate / Link</td>
        <td><i class="icon-resize-small"></i></td>
        <td>icon-resize-small</td>
        <td></td>
      </tr>
      <tr>
        <td>Unallocate / Unlink</td>
        <td><i class="icon-resize-full"></i></td>
        <td>icon-resize-full</td>
        <td></td>
      </tr>
      <tr>
        <td>Delete</td>
        <td><i class="icon-remove"></i></td>
        <td>icon-remove</td>
        <td></td>
      </tr>
      <tr>
        <td>More</td>
        <td><i class="icon-plus-sign"></i></td>
        <td>icon-plus-sign</td>
        <td></td>
      </tr>
      <tr>
        <td>Less</td>
        <td><i class="icon-minus-sign"></i></td>
        <td>icon-minus-sign</td>
        <td></td>
      </tr>
      <tr>
        <td>Request</td>
        <td><i class="icon-envelope"></i></td>
        <td>icon-envelope</td>
        <td>When requesting a virtual port</td>
      </tr>
      <tr>
        <td>Active</td>
        <td><i class="icon-ok"></i></td>
        <td>icon-ok</td>
        <td></td>
      </tr>
      <tr>
        <td>Inactive</td>
        <td><i class="icon-ban-circle"></i></td>
        <td>icon-ban-circle</td>
        <td></td>
      </tr>
      <tr>
        <td>Save</td>
        <td>-</td>
        <td>-</td>
        <td>Not e.g. &quot;Submit&quot;</td>
      </tr>
      <tr>
        <td>Send</td>
        <td>-</td>
        <td>-</td>
        <td>Goes with &quot;Request&quot;</td>
      </tr>
      <tr>
        <td>Cancel</td>
        <td>-</td>
        <td>-</td>
        <td>Link (not button) in forms (e.g. next to &quot;Send&quot;)</td>
      </tr>
      <tr>
        <td>Help</td>
        <td><i class="icon-question-sign"></i></td>
        <td>icon-question-sign</td>
        <td></td>
      </tr>
      <tr>
        <td>External link</td>
        <td><i class="icon-external-link"></i></td>
        <td>icon-external-link</td>
        <td>When a link leaves the application</td>
      </tr>
      <tr>
        <td>Logout</td>
        <td><i class="icon-signout"></i></td>
        <td>icon-signout</td>
        <td></td>
      </tr>
      </tbody>
    </table>
  </div>

</section>

<section>

  <h2>Columns (span9 + span3)</h2>

  <div class="row">
    <div class="span9">
      <div class="content">
        <p>Paragraph in left column</p>
      </div>
    </div>
    <div class="span3">
      <div class="content">
        <p>Paragraph in right column</p>
      </div>
    </div>
  </div>

</section>

<div class="row">
  <div class="span8">

    <section>
      <h2>Separated columns (span8 + span4)</h2>

      <div class="content">
        <p>Paragraph in left column</p>
      </div>
    </section>

  </div>
  <div class="span4">

    <section>
      <h2>Column title</h2>

      <div class="content">
        <p>Paragraph in right column</p>
        <ul class="tasks">
          <li>
            <a class="btn btn-primary" href="#">
              <i class="icon-plus"></i> Common Task A
            </a>
          </li>
          <li>
            <a class="btn btn-primary" href="#">
              <i class="icon-plus"></i> Common Task B
            </a>
          </li>
        </ul>
      </div>
    </section>

  </div>
</div>

<section>
  <h2>Form-like section</h2>

  <div class="content">
    <h3 class="h3">Section sub-title</h3>

    <div class="form">
      <div class="message">
        <h3>Message</h3>
      </div>
      <dl class="dl">
        <dt>
          <a href="#">Institute A</a>
        </dt>
        <dd>Institute A description.</dd>
        <dt>
          <a href="#">Institute B</a>
        </dt>
        <dd>Institute B description.</dd>
        <dt class="inactive">
          Institute C
          <i class="icon-ban-circle"></i>
        </dt>
        <dd>Institute C description.</dd>
        <dt>
          <a href="#">Institute D</a>
        </dt>
        <dd>Institute D description.</dd>
      </dl>

      <p>Some elements are not unavailable/inactive.</p>

      <ul class="ul">
        <li>
          <a href="#">Institute A</a>
        </li>
        <li>
          <a href="#">Institute B</a>
        </li>
        <li class="inactive">Institute C</li>
        <li>
          <a href="#">Institute D</a>
        </li>
      </ul>

      <div class="actions">
        <button type="submit" class="btn btn-primary">Send</button>
        <a href="#">Cancel</a>
      </div>
    </div>

  </div>
</section>

<section>

  <h2>Tooltips</h2>

  <div class="content">

    <h3>Tooltip (default/black)</h3>

    <a rel="tooltip" href="#" data-original-title="Edit object">
      <i class="icon-pencil"></i>
    </a>

    <h3>Tooltip (success/green)</h3>

    <a rel="tooltip" data-type="success" href="#" data-original-title="Tooltip message">
      <i class="icon-plus"></i>
    </a>

    <h3>Tooltip (info/blue)</h3>

    <p>This <a rel="tooltip" data-type="info" href="#" data-original-title="Tooltip message">tooltip</a> is in a
      sentence.</p>

    <h3>Popover (default)</h3>

    <a rel="popover" class="badge badge-success" href="#" data-original-title="Content title"
       data-content="And here's some amazing content. It's very engaging. right?">?</a>

    <h3>Popover (success/green)</h3>

    <a rel="popover" data-type="success" class="badge badge-success" href="#"
       data-content="And here's some amazing content. It's very engaging. right?">?</a>

    <h3>Popover (info/blue)</h3>

    <a rel="popover" data-type="info" class="badge badge-info" href="#"
       data-content="And here's some amazing content. It's very engaging. right?">?</a>

  </div>

</section>

<section>

  <h2>Table + action(s) + filter + pagination</h2>

  <div class="content">

    <!-- action(s) -->

    <div class="actions">
      <a href="#" class="btn btn-primary"><i class="icon-plus"></i> Add new</a>
    </div>

    <!-- filter -->

    <form action="#" class="form-inline" method="POST">
      <label>Filter</label>
      <input type="text" class="input-small"/>
      <select class="input-small">
        <option>Allocated</option>
        <option>Unallocated</option>
      </select>
      <button type="submit" class="btn">Filter</button>
      <label class="radio inline">
        <input type="radio" name="show" value="used"> Show used
      </label>
      <label class="radio inline">
        <input type="radio" name="show" value="used"> Show all
      </label>
      <label class="checkbox inline">
        <input type="checkbox" value="toggle"> Toggle
      </label>
    </form>

    <!-- b:table b:table-bordered b:table-striped -->

    <table class="table table-bordered table-striped table-above-pagination">
      <thead>
      <tr>
        <th>Label</th>
        <th>Location description</th>
        <th>Max. capacity</th>
        <th>Virtual ports</th>
        <th class="cw55 center">Active</th>
        <th class="cw55 small center">Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>UT12</td>
        <td>Steenweg</td>
        <td>100 Gbps</td>
        <td>3</td>
        <td class="center"><i class="icon-ok"></i></td>
        <td class="center">
          <a href="#" rel="tooltip" data-type="info" title="Edit object"><i class="icon-pencil"></i></a><a
            href="http://example.org/api/user/?id=5&amp;action=delete" data-type="info" rel="tooltip"
            title="Delete object" data-form="true" data-confirm="Are you sure?"><i class="icon-remove"></i></a>
        </td>
      </tr>
      <tr>
        <td>UT12</td>
        <td>Steenweg</td>
        <td>100 Gbps</td>
        <td>3</td>
        <td class="center"><i class="icon-ban-circle"></i></td>
        <td class="center">
          <a href="#" rel="tooltip" title="Edit object" data-type="info"><i class="icon-pencil"></i></a>
          <a href="http://example.org/api/user/?id=5&amp;action=delete" rel="tooltip" title="Delete object"
             data-type="info" data-form="true"><i class="icon-remove"></i></a>
        </td>
      </tr>
      <tr>
        <td>UT12</td>
        <td>Steenweg</td>
        <td>100 Gbps</td>
        <td>3</td>
        <td class="center"><i class="icon-ok"></i></td>
        <td class="center">
          <a href="#" rel="tooltip" data-type="info" title="Edit object"><i class="icon-pencil"></i></a>
          <a href="?id=5&amp;action=delete" rel="tooltip" title="Delete object" data-form="true" data-type="info"
             data-confirm="Are you sure?"><i class="icon-remove"></i></a>
        </td>
      </tr>
      </tbody>
    </table>

    <!-- b:pagination -->

    <div class="pagination pagination-below-table">
      <ul>
        <li><a href="#">&#8592;</a></li>
        <li class="active">
          <a href="#">1</a>
        </li>
        <li><a href="#">2</a></li>
        <li class="disabled"><a href="#">...</a></li>
        <li><a href="#">7</a></li>
        <li><a href="#">8</a></li>
        <li><a href="#">&#8594;</a></li>
      </ul>
    </div>

  </div>
</section>

<section>

  <h2>Pagination states</h2>

  <div class="content">
    <div class="pagination pagination-below-table">
      <ul>
        <li class="disabled"><a href="#">&#8592;</a></li>
        <li class="active"><a href="#">1</a></li>
        <li><a href="#">2</a></li>
        <li class="disabled"><a href="#">...</a></li>
        <li><a href="#">7</a></li>
        <li><a href="#">8</a></li>
        <li><a href="#">&#8594;</a></li>
      </ul>
    </div>
    <div class="pagination pagination-below-table">
      <ul>
        <li><a href="#">&#8592;</a></li>
        <li><a href="#">1</a></li>
        <li class="active"><a href="#">2</a></li>
        <li><a href="#">3</a></li>
        <li class="disabled"><a href="#">...</a></li>
        <li><a href="#">7</a></li>
        <li><a href="#">8</a></li>
        <li><a href="#">&#8594;</a></li>
      </ul>
    </div>
    <div class="pagination pagination-below-table">
      <ul>
        <li><a href="#">&#8592;</a></li>
        <li><a href="#">1</a></li>
        <li><a href="#">2</a></li>
        <li><a href="#">3</a></li>
        <li class="disabled"><a href="#">...</a></li>
        <li><a href="#">7</a></li>
        <li class="active"><a href="#">8</a></li>
        <li class="disabled"><a href="#">&#8594;</a></li>
      </ul>
    </div>
  </div>

</section>

<section>

  <h2>Table + detail fold-out</h2>

  <div class="content">

    <!-- b:table b:table-bordered b:table-striped -->

    <table class="table table-bordered table-striped table-above-pagination">
      <thead>
      <tr>
        <th class="cw55 small center">Extra info</th>
        <th>Label</th>
        <th>Location description</th>
        <th>Max. capacity</th>
        <th>Virtual ports</th>
        <th class="cw55 small center">Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr class="rowdetails">
        <td class="center">
          <i class="icon-plus-sign"></i>
          <i class="icon-minus-sign"></i>

          <div class="rowdetails-content">
            <dl class="dl-horizontal">
              <dt>Noc label</dt>
              <dd>Ut002A_CMED01_EHT-1-1-4</dd>
              <dt>Network ID</dt>
              <dd>00-1B-25-2d-DA-65_EHT-1-1-4</dd>
              <dt>Locatie (Lat)</dt>
              <dd>51.2435678</dd>
              <dt>Locatie (LNG)</dt>
              <dd>5.24357</dd>
            </dl>
          </div>
        </td>
        <td>UT12</td>
        <td>Steenweg</td>
        <td>100 Gbps</td>
        <td>3</td>
        <td class="center">
          <a href="#" rel="tooltip" data-type="info" title="Edit object"><i class="icon-pencil"></i></a>
          <a href="#" rel="tooltip" data-type="info" title="Delete object"><i class="icon-remove"></i></a>
        </td>
      </tr>
      <tr class="rowdetails">
        <td class="center">
          <i class="icon-plus-sign"></i>
          <i class="icon-minus-sign"></i>

          <div class="rowdetails-content">
            <div class="row-fluid">
              <div class="span6">
                <dl class="dl-horizontal">
                  <dt>Noc label</dt>
                  <dd>Ut002A_CMED01_EHT-1-1-4</dd>
                  <dt>Network ID</dt>
                  <dd>00-1B-25-2d-DA-65_EHT-1-1-4</dd>
                  <dt>Locatie (Lat)</dt>
                  <dd>51.2435678</dd>
                  <dt>Locatie (LNG)</dt>
                  <dd>5.24357</dd>
                </dl>
              </div>
              <div class="span6">
                <dl class="dl-horizontal">
                  <dt>Noc label</dt>
                  <dd>Ut002A_CMED01_EHT-1-1-4</dd>
                  <dt>Network ID</dt>
                  <dd>00-1B-25-2d-DA-65_EHT-1-1-4</dd>
                  <dt>Locatie (Lat)</dt>
                  <dd>51.2435678</dd>
                  <dt>Locatie (LNG)</dt>
                  <dd>5.24357</dd>
                </dl>
              </div>
            </div>
          </div>
        </td>
        <td>UT12</td>
        <td>Steenweg</td>
        <td>100 Gbps</td>
        <td>3</td>
        <td class="center">
          <a href="#" rel="tooltip" data-type="info" title="Edit object"><i class="icon-pencil"></i></a>
          <a href="#" rel="tooltip" data-type="info" title="Delete object"><i class="icon-remove"></i></a>
        </td>
      </tr>
      <tr>
        <td></td>
        <td>UT12</td>
        <td>Steenweg</td>
        <td>100 Gbps</td>
        <td>3</td>
        <td class="center">
          <a href="#" rel="tooltip" data-type="info" title="Edit object"><i class="icon-pencil"></i></a>
          <a href="#" rel="tooltip" data-type="info" title="Delete object"><i class="icon-remove"></i></a>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</section>

<section>

  <h2>Sortable table</h2>

  <div class="content">

    <!-- b:table b:table-bordered b:table-striped -->

    <table class="table table-bordered table-striped">
      <thead>
      <tr>
        <th class="sortable">
          <a href="#">
            <i class="icon-resize-vertical"></i>
            Label
          </a>
        </th>
        <th class="sortable">
          <a href="#">
            <i class="icon-resize-vertical"></i>
            Location description
          </a>
        </th>
        <th>Max. capacity</th>
        <th class="sortable headerSortUp">
          <a href="#">
            <i class="icon-resize-vertical"></i>
            Virtual ports
          </a>
        </th>
      </tr>
      </thead>
      <tbody>
      <tr class="rowdetails">
        <td>UT12</td>
        <td>Steenweg</td>
        <td>100 Gbps</td>
        <td>3</td>
      </tr>
      <tr class="rowdetails">
        <td>UT12</td>
        <td>Steenweg</td>
        <td>100 Gbps</td>
        <td>3</td>
      </tr>
      <tr>
        <td>UT12</td>
        <td>Steenweg</td>
        <td>100 Gbps</td>
        <td>3</td>
      </tr>
      </tbody>
    </table>
  </div>
</section>

<section class="section-modal">

  <h2>Modals</h2>

  <div class="content">

    <div class="modal modal-relative">
      <div class="modal-header">
        <h3>Error</h3>
      </div>
      <div class="modal-body">
        <i class="icon-remove-sign"></i>

        <p>You do not have access to virtual resources for Bandwith on Demand yet. You have to select a Collaboration
          team with whome you want to share Bandwith on Demand and ask an ICT manager of an organisation, which offers
          BoD, for virtual resources.</p>
      </div>
      <div class="actions">
        <a href="#" class="btn btn-primary"><i class="icon-plus"></i> New Port</a>
        <a href="#">Cancel</a>
      </div>
    </div>

    <div class="modal modal-relative">
      <div class="modal-header">
        <h3>Success</h3>
      </div>
      <div class="modal-body">
        <i class="icon-ok-sign"></i>

        <p>Your email address is confirmed and active. You can email now.</p>
      </div>
      <div class="actions">
      </div>
    </div>

    <div class="modal modal-relative">
      <div class="modal-header">
        <h3>Warning</h3>
      </div>
      <div class="modal-body">
        <i class="icon-exclamation-sign"></i>

        <p>You don't have any teams. Please create some teams first.</p>
      </div>
      <div class="actions">
        <a href="#" class="btn btn-primary"><i class="icon-external-link"></i> Go external</a>
      </div>
    </div>

    <div class="modal modal-relative">
      <div class="modal-header">
        <h3>Info</h3>
      </div>
      <div class="modal-body">
        <i class="icon-info-sign"></i>

        <p>Yessss!!!</p>
      </div>
      <div class="actions">
      </div>
    </div>

  </div>

</section>

<footer>
  <div class="content-some-dense">
    SURFnet bv | Postbus 190-35, 3501 DA Utrecht | T +31 302 305 305 | F +31 302 305 329 | <a
      href="mailto:admin@surfnet.nl">Admin@SURFnet.nl</a>
  </div>
</footer>
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