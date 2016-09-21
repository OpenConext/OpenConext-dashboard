import React from "react";

class Main extends React.Component {
  render() {
    return (
      <div>
        <div className="l-header">
          <App.Components.Header />
          {this.renderNavigation()}
        </div>

        {this.props.page}

        <App.Components.Footer />
      </div>
    );
  }

  renderNavigation() {
    if (!App.superUserNotSwitched()) {
      return <App.Components.Navigation active={this.props.page.props.key} loading={this.props.loading} />;
    }
  }
}

export default Main;
