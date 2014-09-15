App.Controllers.User = {
  initialize: function() {
    page("/logout", this.logoutUser.bind(this));
  },

  logoutUser: function() {
    $.get(App.apiUrl("/logout"), function() {
      App.stop();
    });
  }
}
