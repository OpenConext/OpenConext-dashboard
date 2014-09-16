/** @jsx React.DOM */

var App = {
  Components: {},
  Pages: {},
  Controllers: {},

  initialize: function() {
    this.fetchUserData(function(user) {
      this.currentUser = user;

      for (controller in App.Controllers) {
        App.Controllers[controller].initialize();
      }

      page("/", this.rootPath.bind(this));
      page("*", this.actionNotFound.bind(this));
      page.start();
    }.bind(this));
  },

  actionNotFound: function() {
    console.error("Page not found");
  },

  rootPath: function() {
    page.redirect('/apps');
  },

  render: function(page) {
    if (this.mainComponent) {
      this.mainComponent.setProps({
        page: page
      });
    } else {
      this.mainComponent = React.renderComponent(App.Components.Main({page: page}), document.getElementById("app"));
    }
  },

  stop: function() {
    var node = document.getElementById("app");
    React.unmountComponentAtNode(node);
    React.renderComponent(App.Pages.Logout(), node);
  },

  apiUrl: function(value) {
    return BASE_URL + value;
  },

  fetchUserData: function(callback) {
    $.get(App.apiUrl("/users/me" + window.location.search), function (data) {
      // can't check the response status because it always returns a 200
      if (!data.payload) {
        window.location =
          window.location.protocol +
          "//" +
          window.location.host +
          "/selfservice/dashboard.jsp";
        return;
      }
      I18n.locale = data.language;
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
  }
};
