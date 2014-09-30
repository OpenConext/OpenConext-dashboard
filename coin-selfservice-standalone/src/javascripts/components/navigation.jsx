/** @jsx React.DOM */

App.Components.Navigation = React.createClass({

  componentDidUpdate: function() {
    if (this.props.loading) {
      if (!this.spinner) {
        this.spinner = new Spinner({
          lines: 11, // The number of lines to draw
          length: 7, // The length of each line
          width: 3, // The line thickness
          radius: 8, // The radius of the inner circle
          color: '#FFF', // #rgb or #rrggbb or array of colors
        }).spin(this.refs.spinner.getDOMNode());
      }
    } else {
      this.spinner = null;
    }
  },

  render: function () {
    return (
      <div className="mod-navigation">
        <ul>
          {this.renderItem("/apps", "apps")}
          {this.renderItem("/notifications", "notifications")}
          {this.renderItem("/history", "history")}
          {this.renderItem("/statistics", "stats")}
          {this.renderItem("/my-idp", "my_idp")}
        </ul>

        {this.renderSpinner()}
      </div>
    );
  },

  renderItem: function(href, value) {
    var className = (this.props.active == value ? "active" : "");

    return (
      <li className={className}><a href={href}>{I18n.t("navigation." + value)}</a></li>
    );
  },

  renderSpinner: function() {
    if (this.props.loading) {
      return <div className="spinner" ref="spinner" />;
    }
  }
});
