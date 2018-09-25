import PropTypes from "prop-types";

export const AppShape = PropTypes.shape({
  name: PropTypes.string.isRequired,
  supportMail: PropTypes.string,
  supportUrl: PropTypes.string,
  appUrl: PropTypes.string,
  websiteUrl: PropTypes.string,
  eulaUrl: PropTypes.string,
  detailLogoUrl: PropTypes.string,
  spEntityId: PropTypes.string,
  exampleSingleTenant: PropTypes.bool,
  connected: PropTypes.bool
});
