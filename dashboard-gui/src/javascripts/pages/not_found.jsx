import React from "react";
import I18n from "i18n-js";

const NotFound = () => (
      <div className="mod-not-found">
        <h1>{I18n.t("not_found.title")}</h1>
        <p dangerouslySetInnerHTML={{ __html: I18n.t("not_found.description_html") }} />
      </div>
    );

export default NotFound;
