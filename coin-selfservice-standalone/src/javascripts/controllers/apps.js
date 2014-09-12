App.Controllers.Apps = {

  initialize: function() {
    page("/", this.overview.bind(this));
    page("/apps", this.loadApps.bind(this), this.overview.bind(this));
    page("/apps/:id", this.loadApp.bind(this), this.detail.bind(this));
  },

  loadApps: function(ctx, next) {
    $.ajax({
      headers: {"X-IDP-ENTITY-ID": App.currentUser.currentIdp.id},
      method: "GET",
      url: BASE_URL + "/services",
      type: "json",
      success: function (data) {
        ctx.apps = data.payload;
        next();
      }
    });
  },

  overview: function(ctx) {
    App.render(App.Pages.AppOverview({apps: ctx.apps}));
  },

  detail: function(ctx) {
    App.render(App.Pages.AppDetail({app: ctx.app}));
  },

  loadApp: function(ctx, next) {
    // fetch app with ajax request, id is in: ctx.params.id
    ctx.app = {
      name: "test"
    }
    next();
  }
}
