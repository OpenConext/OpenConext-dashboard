import React from "react";

export const AppShape = React.PropTypes.shape({
  name: React.PropTypes.string.isRequired,
  supportMail: React.PropTypes.string,
  supportUrl: React.PropTypes.string,
  appUrl: React.PropTypes.string,
  websiteUrl: React.PropTypes.string,
  eulaUrl: React.PropTypes.string,
  detailLogoUrl: React.PropTypes.string,
  spEntityId: React.PropTypes.string
});
