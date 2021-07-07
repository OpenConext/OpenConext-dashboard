import React from 'react'
import I18n from 'i18n-js'
import stopEvent from '../utils/stop'
import PropTypes from 'prop-types'

class LanguageSelector extends React.Component {
  render() {
    const languageCodes = this.props.supportedLanguageCodes.split(',').map((s) => s.trim())
    return <ul className="language-selector">{languageCodes.map((code) => this.renderLocaleChooser(code))}</ul>
  }

  renderLocaleChooser(locale) {
    return (
      <li key={locale} className={I18n.currentLocale() === locale ? 'selected' : ''}>
        <a href="/locale" title={I18n.t('select_locale', { locale: locale })} onClick={this.handleChooseLocale(locale)}>
          {I18n.t('code', { locale: locale })}
        </a>
      </li>
    )
  }

  handleChooseLocale(locale) {
    return function (e) {
      stopEvent(e)
      if (I18n.currentLocale() !== locale) {
        window.location.search = 'language=' + locale
      }
    }
  }
}

LanguageSelector.propTypes = {
  supportedLanguageCodes: PropTypes.string.isRequired,
}
export default LanguageSelector
