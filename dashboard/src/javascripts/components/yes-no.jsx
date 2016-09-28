import React from "react";
import I18n from "i18n-js";

class YesNo extends React.Component {
  render() {
    const word = this.props.value ? "yes": "no";
    return <td className={word}>{ I18n.t("boolean." + word)}</td>;
  }
}

export default YesNo;
