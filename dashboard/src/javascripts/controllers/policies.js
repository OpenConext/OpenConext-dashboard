App.Controllers.Policies = {

  initialize: function () {
    if (!App.policiesAvailable) {
      return;
    }

    page("/policies",
      this.loadPolicies.bind(this),
      this.overview.bind(this)
    );

    page("/policies/new",
      this.loadPolicy.bind(this),
      this.loadInstitutionServiceProviders.bind(this),
      this.loadConnectedServiceProviders.bind(this),
      this.loadAllowedAttributes.bind(this),
      this.detail.bind(this)
    );

    page("/policies/:id",
      this.loadPolicy.bind(this),
      this.loadInstitutionServiceProviders.bind(this),
      this.loadConnectedServiceProviders.bind(this),
      this.loadAllowedAttributes.bind(this),
      this.detail.bind(this)
    );

    page("/policies/:id/revisions",
      this.loadRevisions.bind(this),
      this.revisions.bind(this)
    );
  },

  overview: function (ctx) {
    var flash = App.getFlash();
    var shownBeta = $.cookie('shown_beta');
    if (!shownBeta) {
      $.cookie('shown_beta', 'true', { expires: 365, path: '/' });
      flash = { message: I18n.t("policies.beta"), type: 'warning' };
    }
    App.render(App.Pages.PolicyOverview({key: "policies", policies: ctx.policies, flash: flash}));
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

  loadInstitutionServiceProviders: function (ctx, next) {
    $.get(App.apiUrl("/users/me/serviceproviders"), function (data) {
      ctx.institutionServiceProviders = data.payload;
      next();
    });
  },

  loadConnectedServiceProviders: function (ctx, next) {
    $.get(App.apiUrl("/services/connected"), function (data) {
      ctx.connectedServiceProviders = data.payload;
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
      institutionServiceProviders: ctx.institutionServiceProviders,
      connectedServiceProviders: ctx.connectedServiceProviders,
      allowedAttributes: ctx.allowedAttributes
    }));
  },

  saveOrUpdatePolicy: function (policy, failureCallback) {
    var type = policy.id ? "PUT" : "POST";
    var action = policy.id ? I18n.t("policies.flash_updated") : I18n.t("policies.flash_created");
    var jqxhr = $.ajax({
      url: App.apiUrl("/policies"),
      type: type,
      data: JSON.stringify(policy),
      dataType: 'json',
      contentType: 'application/json',
    }).done(function () {
      App.setFlash(I18n.t("policies.flash", {policyName: policy.name, action: action}));
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
      App.setFlash(I18n.t("policies.flash", {policyName: policy.name, action: I18n.t("policies.flash_deleted")}));
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
