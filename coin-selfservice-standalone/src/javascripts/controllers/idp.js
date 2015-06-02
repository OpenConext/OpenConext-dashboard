App.Controllers.MyIdp = {
  initialize: function() {
    page("/my-idp", this.loadIdpRolesWithUsers.bind(this), this.myIdp.bind(this));
  },

  myIdp: function(ctx) {
    App.render(App.Pages.MyIdp({key: "my_idp", roles: ctx.roles, services: ctx.services}));
  },

  loadIdpRolesWithUsers: function(ctx, next) {
    $.get(App.apiUrl("/idp/current/roles"), function(data) {
      ctx.roles = data.payload;
      next();
    });
  },

};
