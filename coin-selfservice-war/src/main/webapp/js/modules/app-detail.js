var app = app || {};

app.appDetail = function() {

  var loadingModal;
  
  var init = function() {
    var recommendApp = $('#recommend-app');

    if (recommendApp.length === 0) {
      return;
    }

    recommendApp.click(function(e) {
      e.preventDefault();

      loadingModal = $('<div class="modal fade"><div class="modal-body"><p>'+
          app.message.i18n('jsp.recommendations.loading_contacts') +'</p></div></div>');
      loadingModal.modal();
      
      var groupsWithMembers = $.ajax({
        url : contextPath + '/groupsWithMembers.json',
        cache : true,
        dataType : 'json'
      });

      var jsp = $.get($(this).attr('href'));

      $.when(groupsWithMembers, jsp).done(renderModal);

    });
  };

  var renderModal = function(groupsWithMembers, jsp) {
    // done returns the entire jqXHR object
    var html = $(jsp[0]);
    var data = groupsWithMembers[0];

    html.find('#recommend-link').click(function() {
      var sel = $("#email-select2").select2("val");
      if (!sel || sel.length === 0) {
        var msg = $("<p>"+app.message.i18n('jsp.recommendations.email_required')+"</p>");
        $(".error").html(msg);
        $(".error").show();
        msg.animate({
          opacity : 0
        }, 5000, 'linear', function() {
          $(".error").hide();
        });
        return;
      }

      
      $.ajax({
        type : "POST",
        url : $(this).data('post-url'),
        data : $('#recommend-form').serialize(),
        error : function(msg) {
          $('.error').html(msg);
        },
        success : function(result) {
          html.modal('hide');
          // have to completely remove the modal otherwise we get session token
          // problems
          html.remove()
        }
      });
    });

    $('#recommend-form').submit(function(e) {
      // fiddling with the enter key in the select2 box can trigger the submit
      e.preventDefault();
      return false;
    });

    var emailSelect2 = html.find('#email-select2');
    emailSelect2.select2({
      createSearchChoice : searchChoice,
      tokenSeparators : [ " ", ", " ],
      multiple : true,
      placeholder : "Emails...",
      maximumSelectionSize : emailSelect2.data('max-selection-size'),
      formatSelectionTooBig : function(maxSize) {
        return emailSelect2.data('format-selection-too-big') + ' (' + maxSize + ')';
      },
      formatResult : format,
      minimumInputLength : 1,
      formatInputTooShort : function(term, minLength) {
        // style <li class="select2-no-results">
        return emailSelect2.data('format-input-too-short');
      },
      data : data
    });

    loadingModal.modal('hide');
    loadingModal.remove();
    
    html.modal({
      backdrop : "static",
      keyboard : "false"
    });
  }

  var searchChoice = function(term, data) {
    if ($(data).filter(function() {
      return this.text.localeCompare(term) === 0;
    }).length === 0) {
      if (/\S+@\S+/g.test(term)) {
        return {
          id : term,
          text : term
        };
      } else {
        return null;
      }
    }
  }

  var format = function(state) {
    if (state.group) {
      return '<i class="icon-group"></i>' + state.text;
    }
    return '<i class="icon-envelope"></i>' + state.text;
  }

  return {
    init : init
  };

}();

app.register(app.appDetail);
