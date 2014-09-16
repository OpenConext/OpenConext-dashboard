App.Controllers.Apps = {

  initialize: function() {
    page("/apps",
      this.loadFacets.bind(this),
      this.loadApps.bind(this),
      this.overview.bind(this)
    );

    page("/apps/:id",
      this.loadApp.bind(this),
      this.detail.bind(this)
    );
  },

  loadApps: function(ctx, next) {
    $.get(App.apiUrl("/services"), function(data) {
      ctx.apps = data.payload;
      next();
    });
  },

  loadApp: function(ctx, next) {
    $.get(App.apiUrl("/services/id/" + ctx.params.id), function(data) {
      ctx.app = data.payload;
      next();
    });
  },

  loadFacets: function(ctx, next) {
    $.get(App.apiUrl("/facets"), function(data) {
      ctx.facets = data.payload;
      next();
    });
  },

  overview: function(ctx) {
    App.render(App.Pages.AppOverview({key: "apps", apps: ctx.apps, facets: ctx.facets}));
  },

  detail: function(ctx) {
    App.render(App.Pages.AppDetail({key: "apps", app: ctx.app}));
  }
}
