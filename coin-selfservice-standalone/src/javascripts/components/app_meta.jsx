/** @jsx React.DOM */

App.Components.AppMeta = React.createClass({
  render: function() {
    return (
      <div className="l-right">
        <div className="mod-app-meta">
          {this.renderLogo()}
          <App.Components.Contact email={this.props.app.supportMail} />
        </div>
      </div>
    );
  },

  renderLogo: function() {
    if (this.props.app.detailLogoUrl) {
      return (
        <div className='logo'>
          <img src={this.props.app.detailLogoUrl} alt={this.props.app.name} />
        </div>
      );
    }
  }
});
