import React from "react";
import I18n from "i18n-js";
import Header from "../components/header";
import Footer from "../components/footer";

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
