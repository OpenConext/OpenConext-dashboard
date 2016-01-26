App.Controllers.Policies = {

  initialize: function () {
    page("/policies",
      this.loadPolicies.bind(this),
      this.overview.bind(this)
    );

    page("/policies/new",
      this.detail.bind(this)
    );

    page("/policies/:id",
      this.loadPolicy.bind(this),
      this.detail.bind(this)
    );

    page("policies/revisions",
      this.revision.bind(this)
    );
  },

  overview: function (ctx) {
    App.render(App.Pages.PolicyOverview({key: "policies", policies: ctx.policies}));
  },

  loadPolicies: function (ctx, next) {
    $.get(App.apiUrl("/policies"), function (data) {
      ctx.policies = data.payload;
      next();
    });
  },

  loadPolicy: function (ctx, next) {
    var url = App.apiUrl("/policies/:id", {id: ctx.params.id});
    $.get(url, function (data) {
      ctx.policy = data.payload;
      next();
    });
  },

  detail: function (ctx) {
    App.render(App.Pages.PolicyDetail({
      key: "policies",
      policy: ctx.policy,
      identityProviders: ctx.identityProviders,
      erviceProviders: ctx.serviceProviders,
      allowedAttributes: ctx.allowedAttributes
    }));
  },

  revision: function (ctx) {
  },
};
