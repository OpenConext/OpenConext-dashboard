import React from "react";
import I18n from "../lib/i18n";

class YesNo extends React.Component {
  render() {
    var word = this.props.value ? "yes": "no";
    return <td className={word}>{ I18n.t("boolean." + word)}</td>;
  }
}

export default YesNo;
