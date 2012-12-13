var app = app || {};

app.appDetail = function() {

  var init = function() {
    var recommendApp = $('#recommend-app');

    if (recommendApp.length === 0) {
      return;
    }

    recommendApp.click(function(e) {
      e.preventDefault();

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
      // $.post($(this).data('post-url'), $('#recommend-form').serialize());

      $.ajax({
        type : "POST",
        url : $(this).data('post-url'),
        data : $('#recommend-form').serialize(),
        error : function(msg) {
          $('.select2-input').addClass('error');
        },
        success : function(result) {
          html.modal('hide');
          //have to completely remove the modal otherwise we get session token problems
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
      placeholder : app.message.i18n('stats.select_idp'),
      createSearchChoice : function(term, data) {
        if ($(data).filter(function() {
          return this.text.localeCompare(term) === 0;
        }).length === 0) {
          return {
            id : term,
            text : term
          };
        }
      },
      tokenSeparators : [ " ", ", " ],
      multiple : true,
      placeholder : "Emails...",
      maximumSelectionSize : emailSelect2.data('max-selection-size'),
      formatResult : format,
      minimumInputLength : 1,
      formatInputTooShort : function(term, minLength) {
        // style <li class="select2-no-results">
        return emailSelect2.data('format-input-too-short');
      },
      data : data
    });

    html.modal({
      backdrop : "static",
      keyboard : "false"
    });
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
