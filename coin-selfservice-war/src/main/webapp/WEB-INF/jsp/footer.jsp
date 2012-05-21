

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