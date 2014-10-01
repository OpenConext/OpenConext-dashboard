/** @jsx React.DOM */

App.Components.AttributePolicyPanel = React.createClass({
  render: function() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>{I18n.t("attributes_policy_panel.title")}</h1>
          <p>{I18n.t("attributes_policy_panel.subtitle", { name: this.props.app.name })}</p>
        </div>

        <div className="mod-attributes">
          <table>
            <thead>
              <tr>
                <th>{I18n.t("attributes_policy_panel.attribute")}</th>
                <th>{I18n.t("attributes_policy_panel.your_value")}
                  <span className="star">*</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {this.props.app.filteredUserAttributes.map(this.renderAttribute)}
            </tbody>
          </table>
          <p>
            <span className="star">*</span> {I18n.t("attributes_policy_panel.hint")}</p>
        </div>
      </div>
      );
  },

  renderAttribute: function(attribute) {
    return (
      <tr key={attribute.name}>
        <td>{attribute.name}</td>
        <td>
          <ul>
          {attribute.userValues.map(this.renderAttributeValue)}
          </ul>
        </td>
      </tr>
      );
  },

  renderAttributeValue: function(attributeValue) {
    return (
      <li key={attributeValue}>{attributeValue}</li>
      );
  }
});
