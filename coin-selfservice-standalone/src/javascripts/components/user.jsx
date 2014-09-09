/** @jsx React.DOM */
var user = {displayName: "Lars Vonk", institutionIdps: [
  {id: "1", name: "foo"},
  {id: "2", name: "bar"}
]};


App.Components.User = React.createClass({
  getInitialState: function () {
    return { user: {} }
  },
  componentDidMount: function() {
    $.ajax({
      url: App.BaseUrl + "/users/me",
      method: "GET",
      success: function(data) {
        this.setState({user: data});
        console.log(data);
      }.bind(this),
      error: function(xhr, status, err) {
        console.log(status);
      }.bind(this)
    });
  },
  render: function () {
    return (
      <div className="user">
        { this.state.user.displayName  }
      </div>
      );
  }
});
