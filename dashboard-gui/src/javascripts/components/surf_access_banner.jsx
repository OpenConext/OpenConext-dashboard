import React from 'react'
import PropTypes from 'prop-types'
import I18n from 'i18n-js'
import logo from '../../images/surf-access-logo.svg'

const SurfAccessBanner = (props, { currentUser }) => {
  if (!currentUser.surfAccessBannerEnabled) {
    return null
  }

  return (
    <div className="mod-surf-access-banner">
      <span className="logo">
        <img src={logo} alt="" />
      </span>
      <p>
        {I18n.t('surf_access_banner.message')}{' '}
        <a href={I18n.t('surf_access_banner.link_url')} target="_blank" rel="noreferrer noopener">
          {I18n.t('surf_access_banner.link')}
        </a>
      </p>
    </div>
  )
}

SurfAccessBanner.contextTypes = {
  currentUser: PropTypes.object,
}

export default SurfAccessBanner
