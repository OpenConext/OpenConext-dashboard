/** @jsx React.DOM */

var App = {
  Components: {},
  Pages: {},
  Controllers: {},

  initialize: function() {
    this.mainComponent = App.Components.Main();
    this.mainComponent = React.renderComponent(this.mainComponent, document.getElementById("app"));

    for (controller in App.Controllers) {
      App.Controllers[controller].initialize();
    }

    page("*", this.actionNotFound.bind(this));

    // this.fetchUserData(function(user) {
      // this.currentUser = user;
      page.start();
    // }.bind(this));
  },

  render: function(page) {
    this.mainComponent.setProps({
      page: page
    });
  },

  apiUrl: function(value) {
    console.log(BASE_URL + value);
    return BASE_URL + value;
  },

  fetchUserData: function(callback) {
    $.get(App.apiUrl("/users/me"), function (data) {
      callback(data.payload);
    });
  },

  ajaxError: function(event, xhr) {
    switch (xhr.status) {
      case 404:
        App.actionNotFound();
        break;
      default:
        console.error("Ajax request failed");
    }
  },

  actionNotFound: function() {
    console.error("Page not found");
  }
};
