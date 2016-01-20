App.Controllers.Policies = {

  initialize: function () {
    page("/policies",
      this.loadPolicies.bind(this),
      this.overview.bind(this)
    );
  },

  overview: function (ctx) {
    App.render(App.Pages.Policies({key: "policies", policies: ctx.policies}));
  },

  loadPolicies: function (ctx, next) {
    $.get(App.apiUrl("/policies"), function (data) {
      ctx.policies = data.payload;
      next();
    })
  }
}
