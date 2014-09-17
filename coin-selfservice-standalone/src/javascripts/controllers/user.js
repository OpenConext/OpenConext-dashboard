App.Controllers.User = {
  initialize: function() {
    if (App.currentUser.superUser) {
      page("/users/switch", this.switchUser.bind(this));
    }

    page("/logout", this.logoutUser.bind(this));
  },

  logoutUser: function() {
    $.get(App.apiUrl("/logout"), function() {
      App.stop();
    });
  },

  switchToIdp: function(idp) {
    $.get(App.apiUrl("/users/me/switch-to-idp?idpId=" + encodeURIComponent(idp.id)), function() {
      window.location = window.location;
    });
  },

  switchUser: function() {
    App.render(App.Pages.SwitchUser());
  }
}
