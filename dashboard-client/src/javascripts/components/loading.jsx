import React from "react";
import I18n from "i18n-js";

class Loading extends React.Component {
  render() {
    return <div>
      <div className="loader">

      </div>
      <p className="loader-info">{I18n.t("loader.loading")}</p>
    </div>;
  }
}

export default Loading;
