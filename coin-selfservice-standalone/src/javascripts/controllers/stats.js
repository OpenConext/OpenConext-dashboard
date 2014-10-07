App.Controllers.Stats = {
  initialize: function() {
    page("/statistics", this.showStats.bind(this));
  },

  showStats: function() {
    App.render(App.Pages.Stats({key: "stats"}));
  }
}
