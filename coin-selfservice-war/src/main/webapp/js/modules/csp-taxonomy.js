var app = app || {};

app.taxonomy = function () {

  var noSelection = function () {
    $("#no_labels_selected").toggle($("#selected_facet_values").find("li").length === 0);
  }

  var linkUnlink = function(facetValueId, isLink, callback) {
    var tokencheck = $('#taxonomy_sp_configuration').data('token-check');
    var cspId = $('#taxonomy_sp_configuration').data('csp-id');
    $.ajax("facet-value-csp/" + facetValueId + "/" + cspId + ".shtml?tokencheck=" + tokencheck,
      {
        type: "POST",
        data: { value: isLink }
      })
      .done(function (data) {
        callback();
      })
      .fail(function (data) {
        var $mess = $("<span>" + app.message.i18n('failed.save') + "</span>");
        $("#selected_facets").prepend($mess);
      });
  }

  var init = function () {

    if ($("#taxonomy_sp_configuration").length === 0) {
      return;
    }

    noSelection();

    $("label[id^='link_facet_value_']").live("click", function () {
      var $elem = $(this);
      var link = $elem.hasClass("link");

      $elem.toggleClass("btn-primary-alt btn-primary link unlink");
      $elem.find("i").toggleClass("icon-minus icon-plus");

      var facetValueId = $elem.parent("li").data("facet-value-id");
      var $facetValues = $("#selected_facet_values");
      if (link) {
        var html = "<li data-facet-value-id='" + facetValueId + "'>";
        html += "<a id='facet_value_pointer_' + facetValueId + ' href='#' class='local-link'>" + $elem.find("span").html().trim() + "</a></li>";
        linkUnlink(facetValueId, true, function(){
          $facetValues.append(html);
        });
      } else {
        var $li = $facetValues.find("li[data-facet-value-id='" + facetValueId + "']");
        linkUnlink(facetValueId, false, function(){
          $li.fadeOut("slow", function () {
            $li.remove();
            noSelection();
          });
        });
      }
    });

    $("a[id^='facet_value_pointer_']").live("click", function () {
      var facetValueId = $(this).parent("li").data("facet-value-id");
      var $li = $("#fieldaccordion").find("li[data-facet-value-id='" + facetValueId + "']");
      $li.parents(".accordion-body").collapse('show');
      $li.find("i.icon-arrow-right").fadeIn().delay(250).fadeOut();
    });

  };

  return {
    init: init
  };
}();

app.register(app.taxonomy);
