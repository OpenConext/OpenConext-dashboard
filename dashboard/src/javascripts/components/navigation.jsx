import React from "react";
import I18n from "../lib/i18n";

class Navigation extends React.Component {

  componentDidUpdate() {
    if (this.props.loading) {
      if (!this.spinner) {
        this.spinner = new Spinner({
          lines: 25, // The number of lines to draw
          length: 25, // The length of each line
          width: 4, // The line thickness
          radius: 20, // The radius of the inner circle
          color: '#4DB3CF', // #rgb or #rrggbb or array of colors
        }).spin(this.refs.spinner.getDOMNode());
      }
    } else {
      this.spinner = null;
    }
  }

  render() {
    return (
      <div className="mod-navigation">
        <ul>
          {this.renderItem("/apps", "apps")}
          {this.renderItem("/policies", "policies")}
          {this.renderItem("/notifications", "notifications")}
          {this.renderItem("/history", "history")}
          {this.renderItem("/statistics", "stats")}
          {this.renderItem("/my-idp", "my_idp")}
        </ul>

        {this.renderSpinner()}
      </div>
    );
  }

  renderItem(href, value) {
    var className = (this.props.active == value ? "active" : "");
    return (
      <li className={className}><a href={href}>{I18n.t("navigation." + value)}</a></li>
    );
  }

  renderSpinner() {
    if (this.props.loading) {
      return <div className="spinner" ref="spinner" />;
    }
  }
}

export default Navigation;
