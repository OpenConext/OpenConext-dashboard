App.Controllers.Apps = {

  initialize: function() {
    page("/", this.overview.bind(this));
    page("/apps", this.overview.bind(this));
    page("/apps/:id", this.loadApp.bind(this), this.detail.bind(this));
  },

  overview: function() {
    App.render(App.Pages.AppOverview());
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
