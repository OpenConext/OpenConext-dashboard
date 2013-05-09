var app = app || {};

app.taxonomy = function () {

  var updateFacet = function (inputField, link) {
    var facetName = inputField.val();
    if (facetName === undefined || facetName.trim() === "") {
      return;
    }
    var tokencheck = $('#taxonomy').data('token-check');
    var facetId = link.parents(".accordion-group").data("facet-id");
    var type, url;
    if (facetId === undefined) {
      type = "POST";
      url = "facet.shtml?tokencheck=" + tokencheck;
    } else {
      type = "PUT";
      url = "facet/" + facetId + ".shtml?tokencheck=" + tokencheck;
    }
    $.ajax(url,
      {
        type: type,
        data: { name: facetName }
      })
      .done(function (data) {
        inputField.remove();
        link.html("<i class='icon-arrow-down'></i>" + inputField.val());
        //if this a new facet then we need to bind the accordion
        if (facetId === undefined) {
          link.prop("href","#" + data + "-body");
          var group = link.parents(".accordion-group");
          group.data("facet-id",data);
          var $accordionBody = group.find(".accordion-body");
          $accordionBody.prop("id",data + "-body" );
          $(".accordion-body.in").collapse('toggle');
          $accordionBody.collapse('show');
        }
        link.fadeToggle();
      })
      .fail(function (data) {
        var $mess = $("<span>" + app.message.i18n('failed.save') + "</span>");
        inputField.after($mess);
      });
  }

  var updateFacetValue = function (inputField, li) {
    var facetValue = inputField.val();
    if (facetValue === undefined || facetValue.trim() === "") {
      return;
    }
    var tokencheck = $('#taxonomy').data('token-check');
    var facetValueId = li.data("facet-value-id");
    var type, url;
    if (facetValueId === undefined) {
      type = "POST";
      url =  + li.parents(".accordion-group").data("facet-id") + "/facet-value.shtml?tokencheck=" + tokencheck;
    } else {
      type = "PUT";
      url = "facet-value/" + facetValueId + ".shtml?tokencheck=" + tokencheck;
    }
    $.ajax(url,
      {
        type: type,
        data: { value: facetValue }
      })
      .done(function (data) {
        inputField.remove();
        var $span = li.find("span");
        $span.html(inputField.val());
        //if this a new facetValue then we need to add the persistent identifier
        if (facetValueId === undefined) {
          li.data("facet-value-id",data);
        }
        $span.fadeToggle();
      })
      .fail(function (data) {
        var $mess = $("<span>" + app.message.i18n('failed.save') + "</span>");
        li.prepend($mess);
      });
  }

  var deleteFacet = function(facetDiv, facetId) {
    var tokencheck = $('#taxonomy').data('token-check');
    var url = "facet/" + facetId + ".shtml?tokencheck=" + tokencheck;
    $.ajax(url,
      {
        type: "DELETE"
      })
      .done(function (data) {
        facetDiv.fadeToggle("slow", function(){
          facetDiv.remove();
        });
      })
      .fail(function (data) {
        var $mess = $("<span>" + app.message.i18n('failed.save') + "</span>");
        facetDiv.find("a.with-options").after($mess);
      });
  }

  var deleteFacetValue = function($facetValue, facetValueId) {
    var tokencheck = $('#taxonomy').data('token-check');
    var url = "facet-value/" + facetValueId + ".shtml?tokencheck=" + tokencheck;
    $.ajax(url,
      {
        type: "DELETE"
      })
      .done(function (data) {
        $facetValue.fadeToggle("slow", function(){
          $facetValue.remove();
        });
      })
      .fail(function (data) {
        var $mess = $("<span>" + app.message.i18n('failed.save') + "</span>");
        $facetValue.prepend($mess);
      });
  }

  function bindInput($input, $link) {
    $input.blur(function () {
      updateFacet($(this), $link);
    });
    $input.keypress(function (event) {
      if (event.which == 13) {
        updateFacet($(this), $link);
      }
    });
  }

  function bindInputForFacetValue($input, $li) {
    $input.blur(function () {
      updateFacetValue($(this), $li);
    });
    $input.keypress(function (event) {
      if (event.which == 13) {
        updateFacetValue($(this), $li);
      }
    });
  }

  function getDeleteWarning(taxonomyType, usages) {
    var msg = "Are you sure you want to delete this " + taxonomyType + "?</br></br>";
    if (usages.length > 0) {
      msg += "The following FacetValue(s) - Service links will also be deleted:</br></br>";
      $.each(usages, function (i, usage) {
        msg += usage.facetValueValue + " - " + usage.compoundServiceProviderName + "</br>";
      });
    }
    return msg;
  }

  var init = function () {

    if ($("#taxonomy").length === 0) {
      return;
    }

    $(".edit-facet").live("click",function () {
      var $btn = $(this);
      var $heading = $btn.parents(".accordion-heading");
      var $link = $heading.find("a.with-options");
      $link.toggle();
      var $input = $("<input type='text' class='inline-edit'>");
      bindInput($input, $link);
      $heading.prepend($input);
      $input.focus();
      $clone = $link.clone();
      $clone.find("i").remove();
      $input.val($clone.html().trim());
    });

    $(".remove-facet").live("click",function () {
      var $facetDiv = $(this).parents(".accordion-group");
      var facetId = $facetDiv.data("facet-id");
      if (facetId === undefined) {
        return false;
      }
      var facetUsages = $.ajax({
          url : 'facet-used/' + facetId + '.shtml',
          dataType : 'json'
        });
      $.when(facetUsages).done(function(usages){
        var msg = getDeleteWarning("Facet", usages);
        bootbox.confirm(msg, function (result) {
          if (result) {
            deleteFacet($facetDiv, facetId);
          }
        });

      });

    });

    $("#add_facet").click(function(){
      var $html = $($("#new_facet_template").html());
      $("#fieldaccordion").append($html);
      var $input = $html.find("input.inline-edit");
      bindInput($input, $html.find("a.with-options"));
      $input.focus();
    });

    $(".edit-facet-value").live("click",function(){
      var $btn = $(this);
      var $li = $btn.parents("li");
      var $span = $li.find("span");
      $span.toggle();
      var $input = $("<input type='text' class='inline-edit'>");
      bindInputForFacetValue($input, $li);
      $li.prepend($input);
      $input.focus();
      $input.val($span.html().trim());
    });

    $(".remove-facet-value").live("click",function () {
      var $li = $(this).parents("li");
      var facetValueId = $li.data("facet-value-id");
      if (facetValueId === undefined) {
        return;
      }
      var facetUsages = $.ajax({
        url : 'facet-value-used/' + facetValueId + '.shtml',
        dataType : 'json'
      });
      $.when(facetUsages).done(function(usages){
        var msg = getDeleteWarning("FacetValue", usages);
        bootbox.confirm(msg, function (result) {
          if (result) {
            deleteFacetValue($li, facetValueId);
          }
        });

      });
    });

    $("a[id^='add_facet_value']").live("click",function() {
      var $html = $($("#new_facet_value_template").html());
      $(this).parents(".accordion-inner").find("ul.facet-values").append($html);
      var $input = $html.find("input.inline-edit");
      bindInputForFacetValue($input, $html);
      $input.focus();

    });

    $("a.with-options").live("click",function() {
      $(this).find("i").toggleClass("icon-arrow-down icon-arrow-up");
    });
  };

  return {
    init: init
  };
}();

app.register(app.taxonomy);
