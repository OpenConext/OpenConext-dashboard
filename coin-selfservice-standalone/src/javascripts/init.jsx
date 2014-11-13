/** @jsx React.DOM */

var App = {
  Components: {},
  Pages: {},
  Controllers: {},
  Mixins: {},

  store: {}, // in memory key/value store, to save temporary settings

  initialize: function() {
    I18n.locale = "en";
    $(document).ajaxError(this.ajaxError.bind(this));
    $(document).ajaxStart(this.showSpinner.bind(this));
    $(document).ajaxStop(this.hideSpinner.bind(this));

    this.fetchUserData(function(user) {
      this.currentUser = user;

      $(document).ajaxSend(function(event, jqxhr, settings) {
        if (settings.url.indexOf(STATS_HOST) < 0) {
          jqxhr.setRequestHeader("X-IDP-ENTITY-ID", this.currentIdpId());
        } else {
          settings.xhrFields = { withCredentials: true };
        }
      }.bind(this));

      for (controller in App.Controllers) {
        App.Controllers[controller].initialize();
      }

      page("/", this.rootPath.bind(this));
      page("*", this.actionNotFound.bind(this));

      if (this.superUserNotSwitched()) {
        page.start({dispatch: false});
        page("/users/search");
      } else {
        page.start();
      }
    }.bind(this));
  },

  actionNotFound: function() {
    this.render(App.Pages.NotFound());
  },

  superUserNotSwitched: function() {
    return this.currentUser.superUser && !this.currentUser.switchedToIdp;
  },

  rootPath: function() {
    if (this.superUserNotSwitched()) {
      page.redirect("/users/search");
    } else {
      page.redirect("/apps");
    }
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

  apiUrl: function(value, params) {
    return page.uri(BASE_URL + value, params);
  },

  renderYesNo: function(value) {
    var word = value ? "yes" : "no";
    return <td className={word}>{I18n.t("boolean." + word)}</td>;
  },

  fetchUserData: function(callback) {
    $.get(App.apiUrl("/users/me" + window.location.search), function (data) {
      // can't check the response status because it always returns a 200
      if (!data.payload) {
        window.location =
          window.location.protocol +
          "//" +
          window.location.host +
          "/dashboard/dashboard.jsp";
        return;
      }
      I18n.locale = data.language;
      callback(data.payload);
    });
  },

  showSpinner: function() {
    if (this.mainComponent) {
      this.mainComponent.setProps({loading: true});
    }
  },

  hideSpinner: function() {
    if (this.mainComponent) {
      this.mainComponent.setProps({loading: false});
    }
  },

  ajaxError: function(event, xhr) {
    switch (xhr.status) {
      case 404:
        App.actionNotFound();
        break;
      default:
        this.render(App.Pages.ServerError());
        console.error("Ajax request failed");
    }
  },

  currentIdpId: function() {
    if (this.currentUser.superUser && this.currentUser.switchedToIdp) {
      return this.currentUser.switchedToIdp.id;
    } else {
      return (this.currentUser.switchedToIdp || this.currentUser.currentIdp).id;
    }
  }
};
