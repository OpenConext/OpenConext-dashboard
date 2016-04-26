/** @jsx React.DOM */

App.Pages.BrowserNotSupported = React.createClass({
  render: function () {
    return (
      <div className="mod-not-found">
        <h1>{I18n.t("browser_not_supported.title")}</h1>
        <p dangerouslySetInnerHTML={{ __html: I18n.t("browser_not_supported.description_html", {ieVersion: this.props.ieVersion} ) }} />
      </div>
    );
  }

});
