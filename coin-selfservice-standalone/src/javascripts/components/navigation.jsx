/** @jsx React.DOM */

App.Components.Navigation = React.createClass({
  render: function () {
    return (
      <div className="mod-navigation">
        <ul>
          {this.renderItem("/apps", "apps")}
          {this.renderItem("/notifications", "notifications")}
          {this.renderItem("/history", "history")}
          {this.renderItem("/statistics", "stats")}
        </ul>
      </div>
    );
  },

  renderItem: function(href, value) {
    var className = (this.props.active == value ? "active" : "");

    return (
      <li className={className}><a href={href}>{I18n.t("navigation." + value)}</a></li>
    );
  }
});
