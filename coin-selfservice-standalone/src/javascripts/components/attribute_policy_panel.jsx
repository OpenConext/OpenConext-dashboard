/** @jsx React.DOM */

App.Components.AttributePolicyPanel = React.createClass({
  render: function() {
    return (
      <div className="l-middle">
        <div className="mod-title">
          <h1>Attributes</h1>
          <p>The following attributes will be exchanged with 3TU Datacentrum. Please note: If keys are missing, additional steps might be needed to ensure a working connection.</p>
        </div>

        <div className="mod-attributes">
          <table>
            <thead>
              <tr>
                <th>Attribute</th>
                <th>Explanation</th>
                <th>Your value <span className="star">*</span></th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>urn:mace:dir:attribute-def:eduPersonTargetedID</td>
                <td className="not-available">not available</td>
                <td className="not-available">not available</td>
              </tr>
              <tr>
                <td>urn:mace:dir:attribute-def:eduPersonTargetedID</td>
                <td className="not-available">Net-id</td>
                <td className="not-available">ward@surfnet.nl</td>
              </tr>
              <tr>
                <td>urn:mace:dir:attribute-def:eduPersonTargetedID</td>
                <td className="not-available">Net-id</td>
                <td className="not-available">ward@surfnet.nl</td>
              </tr>
              <tr>
                <td>urn:mace:dir:attribute-def:eduPersonTargetedID</td>
                <td className="not-available">Net-id</td>
                <td className="not-available">ward@surfnet.nl</td>
              </tr>
              <tr>
                <td>urn:mace:dir:attribute-def:eduPersonTargetedID</td>
                <td className="not-available">Net-id</td>
                <td className="not-available">ward@surfnet.nl</td>
              </tr>
            </tbody>
          </table>
          <p><span className="star">*</span> We show you an example of this key for your own person account so you get an idea of that this actually is. This might not be representative for other accounts within your organization.</p>
        </div>
      </div>
    );
  }
});
