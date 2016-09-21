import I18n from "./lib/i18n";
import $ from "./lib/jquery-2.1.1";
import page from "./lib/page";
import _ from "./lib/underscore";

import "./pages/not_found";

window.App = {
  Components: {},
  Pages: {},
  Controllers: {},
  Mixins: {},
  Help: {},
  Utils: {},

  store: {}, // in memory key/value store, to save temporary settings

  initialize: function () {
    I18n.locale = "en";
    $(document).ajaxError(this.ajaxError.bind(this));
    $(document).ajaxStart(this.showSpinner.bind(this));
    $(document).ajaxStop(this.hideSpinner.bind(this));
    $(document).ajaxComplete(this.checkSessionExpired.bind(this));

    this.fetchUserData(function (user) {
      this.currentUser = user;

      var ieVersion = this.detectIeVersion();
      if (ieVersion !== false && ieVersion <= 9) {
        this.browserNotSupported(ieVersion);
        return;
      }

      this.currentUser.statsToken = this.fetchStatsToken();
      if (!this.currentUser.statsToken) {
        return this.authorizeStats();
      }

      $(document).ajaxSend(this.addDefaultHeaders.bind(this));

      this.checkPoliciesAvailable(function (available) {
        this.policiesAvailable = available;
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

  actionNotFound: function () {
    this.render(App.Pages.NotFound());
  },

  browserNotSupported: function (ieVersion) {
    this.render(App.Pages.BrowserNotSupported({ieVersion: ieVersion}));
  },

  superUserNotSwitched: function () {
    return this.currentUser && this.currentUser.superUser && !this.currentUser.switchedToIdp;
  },

  pageRequested: function () {
    //the redirect_uri goes to /, but we have stored the requested href in the state parameter
    var locationHash = window.location.hash.substr(1);
    var url = locationHash.substr(locationHash.indexOf("state=")).split("&")[0].split("=")[1];
    if (!url) {
      return url;
    }
    var parser = document.createElement('a');
    parser.href = decodeURIComponent(url);
    var pathname = parser.pathname;
    return pathname[0] === "/" ? pathname : "/" + pathname;
  },

  rootPath: function () {
    if (this.superUserNotSwitched()) {
      page.redirect("/users/search");
    } else {
      var requestedPage = this.pageRequested();
      if (requestedPage) {
        page.redirect(requestedPage);
      } else {
        page.redirect("/apps");
      }
    }
  },

  render: function (page) {
    if (this.mainComponent) {
      this.mainComponent.setProps({page: page});
    } else {
      this.mainComponent = React.renderComponent(App.Components.Main({page: page}), document.getElementById("app"));
    }
  },

  stop: function () {
    this.mainComponent = null;
    var node = document.getElementById("app");
    React.unmountComponentAtNode(node);
    React.renderComponent(App.Pages.Logout(), node);
  },

  apiUrl: function (value, params) {
    return page.uri(window.BASE_URL + value, params);
  },

  renderYesNo: function (value) {
    var word = value ? "yes" : "no";
    return <td className={word}>{I18n.t("boolean." + word)}</td>;
  },

  authorizeStats: function () {
    window.location = this.currentUser.statsUrl + "&state=" + window.location;
  },

  fetchStatsToken: function () {
    var locationHash = window.location.hash.substr(1);
    return locationHash.substr(locationHash.indexOf("access_token=")).split("&")[0].split("=")[1];
  },

  redirectTo403Server:  function () {
    window.location = window.location.protocol + "//" + window.location.host + "/dashboard/api/forbidden";
  },

  fetchUserData: function (callback) {
    $.get(App.apiUrl("/users/me" + window.location.search))
      .done(function (data) {
        if (!data.payload) {
          this.redirectTo403Server();
          return;
        }
        I18n.locale = data.language;
        callback(data.payload);
      }.bind(this));
  },

  checkPoliciesAvailable: function(callback) {
    $.ajax({
      url: App.apiUrl("/policies"),
      method: 'OPTIONS',
      async: false
    }).done(function (data, status, jqxhr) {
      var allowHeader = jqxhr.getResponseHeader('Allow');
      callback(_.isString(allowHeader) && allowHeader.indexOf('GET') > -1);
    }).fail(function (jqxhr, status, e) {
      callback(false);
    });
  },

  showSpinner: function () {
    if (this.mainComponent) {
      this.mainComponent.setProps({loading: true});
    }
  },

  hideSpinner: function () {
    if (this.mainComponent) {
      this.mainComponent.setProps({loading: false});
    }
  },

  ajaxError: function (event, jqxhr) {
    if (jqxhr.isConsumed || jqxhr.readyState == 0 || jqxhr.status == 0) {
      return;
    }

    switch (jqxhr.status) {
      case 404:
        App.actionNotFound();
        break;
      case 403:
        this.redirectTo403Server();
        break;
      default:
        console.error("Ajax request failed", event);
        this.render(App.Pages.ServerError());
    }
  },

  checkSessionExpired: function (event, xhr, settings) {
    //do not handle anything other then 200 and 302 as the others are handled by ajaxError
    if (!settings.crossDomain && xhr.getResponseHeader("sessionAlive") !== "success" && (xhr.status === 0 || xhr.status === 200 || xhr.status === 302)) {
      if (window.location.hostname === "localhost") {
        window.location.href = window.location.protocol + "//" + window.location.host + "/dashboard/api/home?redirectTo=" + window.location.pathname;
      } else {
        window.location.href = window.location.protocol + "//" + window.location.host + "/apps";
      }
    }
  },

  addDefaultHeaders: function (event, jqxhr, settings) {
    if (settings.url.indexOf(STATS_HOST) < 0) {
      jqxhr.setRequestHeader("X-IDP-ENTITY-ID", this.currentIdpId());
    }
  },

  currentIdpId: function () {
    return this.currentIdp().id;
  },

  currentIdp: function () {
    if (this.currentUser.superUser && this.currentUser.switchedToIdp) {
      return this.currentUser.switchedToIdp;
    } else {
      return (this.currentUser.switchedToIdp || this.currentUser.currentIdp);
    }
  },

  setFlash: function (message, type) {
    this.store.flash = {message: message, type: type || 'info'};
  },

  getFlash: function () {
    var flash = this.store.flash;
    this.store.flash = undefined;
    return flash;
  },


  PubSub: {
    _events: {},
    _lastUid: 0,

    publish: function (event, data) {
      if (!this._events[event]) return;

      var subscribers = this._events[event];
      for (s in subscribers) {
        subscribers[s](data);
      }
    },

    subscribe: function (event, callback) {
      if (typeof callback !== 'function') {
        return false;
      }
      if (!this._events[event]) this._events[event] = {};
      var token = 'uid_' + String(++this._lastUid);
      this._events[event][token] = callback;
      return token;
    },

    unsubscribe: function (token) {
      for (e in this._events) {
        var event = this._events[e];
        if (event[token]) {
          delete event[token];
          break;
        }
      }
    }
  }
};
