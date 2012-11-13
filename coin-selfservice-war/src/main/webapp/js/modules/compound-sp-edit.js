var app = app || {};

app.compoundSpEdit = function() {

  var init = function() {

  };

  var alertDiv = function(msg) {
    return $("<div />").addClass("alert").html(msg)
       .append("<button type='button'>&times;</button>").attr("data-dismiss", "alert").addClass("close")
  }

  $("button[name='usethis'],button[name='save']").click(function(e) {
    e.preventDefault();
    postForm(this); 
  });
  
  $("#screenshots-body").on("click", "a[id^=screenshot-remove-]", function(e){
    var elem =$(this);
	var id = elem .attr("id").substring("screenshot-remove-".length);
    var parent = elem .parents("div.screenshot-content");
    
    var tokencheck = $('input[name="tokencheck"]').first().attr('value');
    $.ajax("remove-screenshot/" + id + ".shtml?tokencheck=" + tokencheck,
        {
          type: "delete",
          failure: function(msg) {
                $(parent).prepend(alertDiv("Failure saving data. Details: " + msg));
          },
            success: function(result) {
                parent.remove();
              }
          
    });
        
  });
  
  
  var postForm = function(button) {
    var form = $(button).closest('form');
    var formData = form.serialize();
    //to include the pressed button in the formData
    formData = formData + "&" + button.name + "=" + button.value ;
    $.ajax(
          "compoundSp-update.shtml",
          {
            data: formData,
            type: "post",
            error: function(jqxhr, msg) {
              $(form).prepend(alertDiv(app.message.i18n('failed.save')));
            },
            success: function(result) {
              if (button.name === 'usethis') {
                //need to adjust the tab style above the form after the source has changed
                $(form).parents("div.accordion-inner").find("ul > li").each(function(index) {
                var li = $(this);  
                if (li.find("a").attr("href").indexOf(result.toLowerCase()) !== -1) {
                  li.addClass("source-selected"); 
                } else {
                  li.removeClass("source-selected");  
                } 
                });
                $(form).parents("div.accordion-group").first().removeClass("error");
              }
              $(form).prepend(alertDiv(app.message.i18n('success.save')));
            }
          });
  }

  /*
  Begin File upload plugin
   */

  $(function() {
    fileUploadInit();
  });

  function fileUploadInit() {

    var currentFileuploadForm;

    $('form.imageuploadform').submit(function(event) {
      event.preventDefault();
    });

    $('input.fileinput').fileupload({
      success: function (imageUrl) {
        var form = $(currentFileuploadForm);
        if (form.attr("id") === "form-screenshots-distributionchannel") {
          $("div.screenshot-contents").
          append("<div class='screenshot-content'><img src='"+ contextPath + imageUrl.fileUrl +  "?" + new Date().getTime() + "'/>"
              + "<a id='screenshot-remove-" + imageUrl.id + "' href='#'>&times;</a></div>" );
        } else {
            var img = $(currentFileuploadForm).find("img");
            var newimg = "<img src='" + contextPath +  imageUrl +  "?" + new Date().getTime() + "'/>";
            if (img.length) {
              img.replaceWith(newimg);
            } else {
              form.prepend(newimg);
            }
            
        }
      },
      error: function(jqXHR, textStatus, errorThrown) {
      }
    });
    
    $(".attachlink").click(function (e) {
      e.preventDefault();
      var form = $(this).closest(".imageuploadform");
      form.find("input.fileinput").click();
      currentFileuploadForm = form;
    });
  }



  return {
    init: init
  }
}();



app.register(app.compoundSpEdit);
