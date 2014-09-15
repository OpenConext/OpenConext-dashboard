/** @jsx React.DOM */

App.Pages.AppDetail = React.createClass({
  render: function () {

    var logo = "";
    if (this.props.app.detailLogoUrl) {
      logo = "<div className='logo'><img src='" + this.props.app.detailLogoUrl +"'/></div>"
    }
    return (
      <div className="l-center">

        <div className="l-left">
          <div className="mod-app-nav">
            <ul>
              <li><a href="#">{I18n.t("apps.detail.overview")}</a></li>
              <li><a href="#">{I18n.t("apps.detail.license_info")}</a></li>
              <li><a href="#">{I18n.t("apps.detail.application_usage")}</a></li>
              <li><a href="#">{I18n.t("apps.detail.attribute_policy")}</a></li>
              <li><a href="#">{I18n.t("apps.detail.how_to_connect")}</a></li>
            </ul>
          </div>
        </div>

        <div className="l-right">
          <div className="mod-app-meta">
            {logo}
            <App.Components.Contact description={I18n.t("apps.detail.support_contact_description")} email={this.props.app.supportMail} />
          </div>
        </div>

        <div className="l-middle">
          <div className="mod-title">
            <h1>{this.props.app.name}</h1>
            <h2>Application by {this.props.app.spName}</h2>
          </div>

          <div className="mod-connection">
            <div className="technical yes">
              <h2>No technical connection</h2>
              <p>Read <a href="#">how to connect</a></p>
            </div>
            <div className="license no">
              <h2>License information unknown</h2>
              <p>Read <a href="#">how to connect</a></p>
            </div>
          </div>

          <div className="mod-description">
            <h2>Description</h2>
            <p>3TU.datacentrum offers the knowledge, experience and the tools to archive research data in a standardized, secure and well-documented manner. It provides the research community with:</p>
            <ul>
              <li>A long term archive for strogin scientificidata</li>
              <li>Permanent access to and tools for reuse of research data</li>
              <li>Advice and support on data management</li>
            </ul>
            <p>3TU.Datacentrum currently hosts about 5000 datasets. To see examples please visit: http://data.3tu.nl</p>
          </div>

          <App.Components.Screenshots screenshotUrls={this.props.app.screenshotUrls} />
        </div>

      </div>
    );
  }
});
