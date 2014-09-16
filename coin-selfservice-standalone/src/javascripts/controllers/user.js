App.Controllers.User = {
  initialize: function() {
    page("/logout", this.logoutUser.bind(this));
  },

  logoutUser: function() {
    $.get(App.apiUrl("/logout"), function() {
      App.stop();
    });
  },

  switchToIdp: function(idp) {
    $.get(App.apiUrl("/users/me/switch-to-idp/" + encodeURIComponent(idp.id)), function() {
      window.location = window.location;
    });
  }
}
