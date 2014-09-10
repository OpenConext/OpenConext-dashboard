/** @jsx React.DOM */

App.Components.Main = React.createClass({
  render: function () {
    return (
      <div>
        <div className="l-header">
          <App.Components.Header />
          <App.Components.Navigation />
        </div>

        {this.props.page}

        <App.Components.Footer />
      </div>
    );
  }
});
