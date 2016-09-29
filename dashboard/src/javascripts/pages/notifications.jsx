import React from "react";
import Link from "react-router/Link";

import I18n from "i18n-js";

import { getNotifications } from "../api";
import YesNo from "../components/yes_no";

class Notifications extends React.Component {
  constructor() { super(); this.state = {
    notificationMessage: {
      messageKeys: [],
      arguments: []
    }
  };
  }

  componentWillMount() {
    const { currentUser } = this.context;

    getNotifications(currentUser.getCurrentIdpId())
      .then(data => this.setState({ notificationMessage: data.payload }));
  }

  render() {
    const notificationMessage = this.state.notificationMessage;
    return (
      <div className="l-mini">

        <div className="mod-notifications">
          <h1>{I18n.t("notifications.title")}</h1>

          <div>
            {notificationMessage.messageKeys.map(this.renderNotificationMessage)}
          </div>
          <br />
          <table>
            <thead>
              <tr>
                <th className="percent_60">{I18n.t("notifications.name")}</th>
                <th className="percent_20">{I18n.t("notifications.license")}</th>
                <th className="percent_20">{I18n.t("notifications.connection")}</th>
              </tr>
            </thead>
            <tbody>
              {notificationMessage.arguments.sort((l, r) => {
                return l.name.localeCompare(r.name);
              }).map(this.renderNotification)}
            </tbody>
          </table>

        </div>
      </div>
      );
  }

  renderNotificationMessage(messageKey) {
    return <p key={messageKey}>{I18n.t(messageKey)}</p>;
  }

  renderNotification(notificationArgument) {
    return (
      <tr key={notificationArgument.id}>
        <td>
          <Link to={"/apps/" + notificationArgument.id}>
            {notificationArgument.name}
          </Link>
        </td>
        <YesNo value={notificationArgument.license} />
        <YesNo value={notificationArgument.connected} />
      </tr>
    );
  }
}

Notifications.contextTypes = {
  currentUser: React.PropTypes.object
};

export default Notifications;
