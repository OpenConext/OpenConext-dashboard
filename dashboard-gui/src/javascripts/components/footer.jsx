import React from 'react'
import I18n from 'i18n-js'
import LanguageSelector from './language_selector'

const Footer = ({ currentUser }) => {
  const supportedLanguageCodes = currentUser ? currentUser.supportedLanguages : []

  return (
    <div className="mod-footer">
      <div className="container">
        <div className="help">
          {I18n.t('footer.tips_or_info')}
          <div dangerouslySetInnerHTML={{ __html: I18n.t('footer.help_html') }} />
          <div dangerouslySetInnerHTML={{ __html: I18n.t('footer.terms_html') }} />
          <div dangerouslySetInnerHTML={{ __html: I18n.t('footer.contact_html') }} />
        </div>
        <LanguageSelector supportedLanguageCodes={supportedLanguageCodes} />

        <div className="powered-by-surf">
          Proudly powered by
          <div dangerouslySetInnerHTML={{ __html: I18n.t('footer.surf_html') }} />
        </div>
      </div>
    </div>
  )
}

export default Footer
