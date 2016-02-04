App.Controllers.Policies = {

  initialize: function () {
    page("/policies",
      this.loadPolicies.bind(this),
      this.overview.bind(this)
    );

    page("/policies/new",
      this.loadPolicy.bind(this),
      this.loadServiceProviders.bind(this),
      this.loadAllowedAttributes.bind(this),
      this.detail.bind(this)
    );

    page("/policies/:id",
      this.loadPolicy.bind(this),
      this.loadServiceProviders.bind(this),
      this.loadAllowedAttributes.bind(this),
      this.detail.bind(this)
    );

    page("/policies/:id/revisions",
      this.loadRevisions.bind(this),
      this.revisions.bind(this)
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
    var url = ctx.params.id ?
      App.apiUrl("/policies/:id", {id: ctx.params.id}) : App.apiUrl("/policies/new");
    $.get(url, function (data) {
      ctx.policy = data.payload;
      next();
    });
  },

  loadServiceProviders: function (ctx, next) {
    $.get(App.apiUrl("/services"), function (data) {
      ctx.serviceProviders = data.payload;
      next();
    });
  },

  loadIdentityProviders: function (ctx, next) {
    $.get(App.apiUrl(""), function (data) {
      ctx.identityProviders = data.payload;
      next();
    });
  },

  loadAllowedAttributes: function (ctx, next) {
    $.get(App.apiUrl("/policies/attributes"), function (data) {
      ctx.allowedAttributes = data.payload;
      next();
    });
  },

  detail: function (ctx) {
    App.render(App.Pages.PolicyDetail({
      key: "policies",
      policy: ctx.policy,
      identityProviders: App.currentUser.institutionIdps,
      serviceProviders: ctx.serviceProviders,
      allowedAttributes: ctx.allowedAttributes
    }));
  },

  saveOrUpdatePolicy: function (policy, failureCallback) {
    var type = policy.id ? "PUT" : "POST";
    var json = JSON.stringify(policy);
    var action = policy.id ? I18n.t("policies.flash_updated") : I18n.t("policies.flash_created");
    var jqxhr = $.ajax({
      url: App.apiUrl("/policies"),
      type: type,
      data: json
    }).done(function () {
      //App.setFlash(I18n.t("policies.flash", {policyName: policy.name, action:action}));
      page("/policies");
    }).fail(function () {
      failureCallback(jqxhr);
    });
  },

  deletePolicy: function (policy) {
    $.ajax({
      url: App.apiUrl("/policies/:id", {id: policy.id}),
      type: 'DELETE'
    }).done(function () {
      //App.setFlash(I18n.t("policies.flash", {policyName: policy.name, action: I18n.t("policies.flash_deleted")}));
      page("/policies");
    });
  },

  loadRevisions: function (ctx, next) {
    var url = App.apiUrl("/policies/:id/revisions", {id: ctx.params.id});
    $.get(url, function (data) {
      ctx.revisions = data.payload;
      next();
    });
  },

  revisions: function (ctx) {
    App.render(App.Pages.PolicyRevisions({
      key: "policies",
      revisions: ctx.revisions
    }));
  }

};
