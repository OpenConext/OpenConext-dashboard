/** @jsx React.DOM */

var App = {
  Components: {},
  Pages: {},

  initialize: function() {
    this.mainComponent = App.Components.Main({page: App.Pages.AppOverview()});

    React.renderComponent(this.mainComponent, document.getElementById("app"));
  }
};
