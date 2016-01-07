var app = app || {};

app.compoundSpEdit = function() {

  var init = function() {

  };

  var alertDiv = function(msg, error) {
    var div = $("<div />").addClass("alert").html(msg)
       .append("<button type='button'>&times;</button>").attr("data-dismiss", "alert").addClass("close");
    if (error) {
      div.addClass("error");
    }
    return div;
  };

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
                $(parent).prepend(alertDiv("Failure saving data. Details: " + msg), true);
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
              $(form).prepend(alertDiv(app.message.i18n('failed.save') + ' : ' + jqxhr.responseText, true));
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
  };

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
      // Custom handler because fileupload plugin does not honor maxfileSize and acceptedFileTypes options, although they are described in the API docs.
      // http://stackoverflow.com/questions/17451629/maxfilesize-and-acceptfiletypes-in-blueimp-file-upload-plugin-do-not-work-why
      add: function(e, data) {
        var maxFileSize = 1000000;
        var uploadErrors = [];
        var acceptFileTypes = /^image\/(gif|jpe?g|png)$/i;
        if(!acceptFileTypes.test(data.originalFiles[0]['type'])) {
          uploadErrors.push('Not an accepted file type');
        }
        var actualFileSize = data.originalFiles[0]['size'];
        if(actualFileSize > maxFileSize) {
          uploadErrors.push('File is too large (' + actualFileSize + " > " + maxFileSize + ")");
        }
        if(uploadErrors.length > 0) {
          $(currentFileuploadForm).prepend(alertDiv("Error uploading file: " + uploadErrors.join(". "), true));
        } else {
          data.submit();
        }
      },

      success: function (imageUrl) {

        var form = $(currentFileuploadForm);

        if (form.attr("id") === "form-screenshots-distributionchannel") {
          // IE9 does not inject the ajax data as first argument, but injects jQuery instead.
          if (typeof(imageUrl.fileUrl) != "string") {
            window.location.href = window.location.href;
            return;
          }

          $("div.screenshot-contents").
          append("<div class='screenshot-content'><img src='"+ contextPath + imageUrl.fileUrl +  "?" + new Date().getTime() + "'/>"
              + "<a id='screenshot-remove-" + imageUrl.id + "' href='#'>&times;</a></div>" );
        } else {

          // IE9 does not inject the ajax data as first argument, but injects jQuery instead.
          if (typeof(imageUrl) != "string") {
            window.location.href = window.location.href;
            return;
          }

          var img = $(currentFileuploadForm).find("img");
            var newimg = "<img src='" + contextPath +  imageUrl +  "?" + new Date().getTime() + "'/>";
            if (img.length) {
              img.replaceWith(newimg);
            } else {
              form.prepend(newimg);
            }
        }
      }, fail: function(event, data) {
        $(currentFileuploadForm).prepend(alertDiv("Error uploading file: " + data.errorThrown, true));
      }
    });

    $("input.fileinput").click(function () {
      currentFileuploadForm = $(this).closest(".imageuploadform");
    });
  }

  return {
    init: init
  };
}();

app.register(app.compoundSpEdit);
