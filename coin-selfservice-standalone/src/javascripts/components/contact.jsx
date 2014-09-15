/** @jsx React.DOM */

App.Components.Contact = React.createClass({
  render: function() {
    return (
      <div className="contact">
        <h2>{this.props.description}</h2>
        <address>
        {this.props.name ? this.props.name + "<br />" : ""}
        {this.props.phone ? this.props.phone + "<br />" : ""}
          <a href={"mailto:" + this.props.email}>{this.props.email}</a>
        </address>
      </div>
      );
  }
});
