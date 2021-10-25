import React from 'react'
import PropTypes from 'prop-types'
import I18n from 'i18n-js'

class YesNo extends React.Component {
  render() {
    const word = this.props.value ? 'yes' : 'no'
    return <td className={`${word} yesno`}>{I18n.t('boolean.' + word)}</td>
  }
}

YesNo.propTypes = {
  value: PropTypes.bool,
}

export default YesNo
