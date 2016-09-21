import React from "react";
import I18n from "../lib/i18n";

class BrowserNotSupported extends React.Component {
  render() {
    return (
      <div>
        <div className="l-header">
          <Header />
        </div>
        <div className="mod-not-found">
          <h1>{I18n.t("browser_not_supported.title")}</h1>
          <p dangerouslySetInnerHTML={{ __html: I18n.t("browser_not_supported.description_html") }} />
        </div>
        <Footer />
      </div>
    );
  }
}

export default BrowserNotSupported;
