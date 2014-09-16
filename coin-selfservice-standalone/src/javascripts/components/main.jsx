/** @jsx React.DOM */

App.Components.Main = React.createClass({
  render: function () {
    return (
      <div>
        <div className="l-header">
          <App.Components.Header />
          <App.Components.Navigation active={this.props.page.props.key} />
        </div>

        {this.props.page}

        <App.Components.Footer />
      </div>
    );
  }
});
