App.Utils.AutoFormat = {

  addQuotes: function (str) {
    return (_.isEmpty(str)) ? str : "'" + str + "'";
  },

  attributes: function (attributes, allAttributesMustMatch) {
    const otherAttr = attributes.filter(function (attr) {
      return attr.name !== "urn:collab:group:surfteams.nl";
    });
    if (otherAttr.length === 0) {
      return ".";
    }
    var attributes = _.groupBy(otherAttr, function (attr) {
      return attr.name;
    });
    const attributeNames = Object.keys(attributes);
    const length = attributeNames.length;
    const lines = attributeNames.map(function (attributeName, index) {
      const values = attributes[attributeName].map(function (attribute) {
        return this.addQuotes(attribute.value);
      }.bind(this)).join(" or ");
      const logical = index === (length - 1) ? "" : allAttributesMustMatch ? " and " : " or ";
      const result = "he/ she has the value " + values + " for attribute '" + attributeName + "'" + logical;
      return result;
    }.bind(this));
    return lines.join("");

  },

  description: function (policy) {
    const idps = _.isEmpty(policy.identityProviderNames) ? "" : " from " + policy.identityProviderNames.map(this.addQuotes).join(" or ");
    const sp = this.addQuotes(policy.serviceProviderName) || "?";
    const attrs = policy.attributes || [];
    const teamMembershipAttr = attrs.filter(function (attr) {
      return attr.name === "urn:collab:group:surfteams.nl";
    });
    const teamMembership = teamMembershipAttr.length > 0 ? " he/ she is a member of the team " + teamMembershipAttr.map(function (attr) {
      return this.addQuotes(attr.value);
    }.bind(this)).join(" or ") : "";

    const and = teamMembershipAttr.length === 0 || teamMembershipAttr.length === attrs.length ? "" : policy.allAttributesMustMatch ? " and" : " or";
    const only = policy.denyRule ? "not" : "only";

    const attributes = this.attributes(attrs, policy.allAttributesMustMatch);
    //we can't use JS templates as the backtick breaks the uglification. Will be resolved when we upgrade the build tooling
    const description = "A user " + idps + "is " + only + " allowed to access " + sp + " when" + teamMembership + and + " " + attributes;

    return description;
  }

};
