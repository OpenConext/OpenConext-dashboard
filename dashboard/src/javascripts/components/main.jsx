import React from "react";

class Main extends React.Component {
  render() {
    return (
    );
  }

  renderNavigation() {
    if (!App.superUserNotSwitched()) {
      return <App.Components.Navigation active={this.props.page.props.key} loading={this.props.loading} />;
    }
  }
}

export default Main;
