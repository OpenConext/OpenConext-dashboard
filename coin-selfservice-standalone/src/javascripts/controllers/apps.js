App.Controllers.Apps = {

  initialize: function() {
    page("/apps", this.loadApps.bind(this), this.overview.bind(this));
    page("/apps/:id", this.loadApp.bind(this), this.detail.bind(this));
  },

  loadApps: function(ctx, next) {
    $.get(App.apiUrl("/services"), function(data) {
      ctx.apps = data.payload;
      next();
    });
  },

  overview: function(ctx) {
    App.render(App.Pages.AppOverview({apps: ctx.apps}));
  },

  detail: function(ctx) {
    App.render(App.Pages.AppDetail({app: ctx.app}));
  },

  loadApp: function(ctx, next) {
    $.get(App.apiUrl("/services/id/" + ctx.params.id), function(data) {
      console.log(data.payload);
      ctx.app = data.payload;
      next();
    });
  }
}
