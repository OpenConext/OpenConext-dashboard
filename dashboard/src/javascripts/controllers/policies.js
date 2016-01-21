App.Controllers.Policies = {

  initialize: function () {
    page("/policies",
      this.loadPolicies.bind(this),
      this.overview.bind(this)
    );

    page("/policies/new",
      this.detail.bind(this));
  },

  overview: function (ctx) {
    App.render(App.Pages.Policies({key: "policies", policies: ctx.policies}));
  },

  loadPolicies: function (ctx, next) {
    $.get(App.apiUrl("/policies"), function (data) {
      ctx.policies = data.payload;
      next();
    })
  },

  detail: function (ctx) {
      App.render(App.Pages.PolicyDetail({
            key: "policies",
            policy: ctx.policy,
            identityProviders: ctx.identityProviders,
            serviceProviders: ctx.serviceProviders,
            allowedAttributes: ctx.allowedAttributes
          }
      ));
  },
}
