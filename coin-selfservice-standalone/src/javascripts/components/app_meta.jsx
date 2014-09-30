/** @jsx React.DOM */

App.Components.AppMeta = React.createClass({
  render: function() {
    return (
      <div className="l-right">
        <div className="mod-app-meta">
          {this.renderLogo()}

          <div className="contact">
            <h2>{I18n.t("app_meta.question")}</h2>
            <address>
              <a href={"mailto:support@surfconext.nl?subject=Question about " + this.props.app.name}>support@surfconext.nl</a>
            </address>
          </div>

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
