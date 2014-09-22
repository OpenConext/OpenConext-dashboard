/** @jsx React.DOM */

App.Pages.Notifications = React.createClass({
  render: function() {
    var notificationMessage = this.props.notificationMessage;
    return (
      <div className="l-mini">

        <div className="mod-notifications">
          <h1>{I18n.t("notifications.title")}</h1>

          <p>
            {notificationMessage.messageKeys.map(this.renderNotificationMessage)}
          </p>
          <table>
            <thead>
              <tr>
                <th className="percent_10">{I18n.t("notifications.icon")}</th>
                <th className="percent_35">{I18n.t("notifications.name")}</th>
                <th className="percent_20">{I18n.t("notifications.license")}</th>
                <th className="percent_20">{I18n.t("notifications.connection")}</th>
              </tr>
            </thead>
            <tbody>
              {notificationMessage.arguments.sort(function(l, r) {
                return l.name.localeCompare(r.name)
              }).map(this.renderNotification)}
            </tbody>
          </table>

        </div>
      </div>
      );
  },
  renderNotificationMessage: function(messageKey) {
    return <p key={messageKey}>{I18n.t(messageKey)}</p>
  },
  renderNotification: function(notificationArgument) {
    return (
      <tr key={notificationArgument.id}>
        <td>
          {this.renderServiceLogo(notificationArgument)}
        </td>
        <td>
        {notificationArgument.name}
        </td>
        {App.renderYesNo(notificationArgument.license)}
        {App.renderYesNo(notificationArgument.connection)}
      </tr>
      );
  },
  renderServiceLogo: function(service) {
    if (service.logoUrl) {
      return <img src={service.logoUrl} width="30" height="30" alt=""/>
    }
  }

});
