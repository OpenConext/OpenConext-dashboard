App.Controllers.Notifications = {

  initialize: function() {
    page("/notifications", this.index.bind(this));
  },

  index: function() {
    App.render(App.Pages.Notifications({key: "notifications"}));
  }
}
