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
    var html = $(jsp);
    var data = groupsWithMembers[0];
    
    var emailSelect2 = html.find('#email-select2');
    emailSelect2 .select2({
      placeholder : app.message.i18n('stats.select_idp'),
      createSearchChoice:function(term, data) { 
        if ($(data).filter(function() { 
            return this.text.localeCompare(term)===0; 
        }).length===0) {
            return {id:term, text:term};
        } 
      },
      tokenSeparators: [" ",", " ],
      multiple: true,
      placeholder: "Emails...",
      maximumSelectionSize: 2,
      formatResult: format,
      formatSelection: format,                
      minimumInputLength: 2,
      formatInputTooShort: function(term, minLength) {
        // style <li class="select2-no-results">
        return 'Start typing for mail suggestions...';
        },
      data: data
    });
    
    html.modal();
    
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
