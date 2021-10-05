import React from 'react'
import I18n from 'i18n-js'
import LanguageSelector from './language_selector'

const Footer = ({ currentUser }) => {
  const supportedLanguageCodes = currentUser ? currentUser.supportedLanguages : []

  return (
    <footer>
      <div className="container">
        <div className="help">
          <h3>{I18n.t('footer.tips_or_info')}</h3>
          <ul>
            <li dangerouslySetInnerHTML={{ __html: I18n.t('footer.help_html') }} />
            <li dangerouslySetInnerHTML={{ __html: I18n.t('footer.terms_html') }} />
            <li dangerouslySetInnerHTML={{ __html: I18n.t('footer.contact_html') }} />
          </ul>
        </div>

        <LanguageSelector supportedLanguageCodes={supportedLanguageCodes} />

        <div className="powered-by-surf">
          <h3>Powered by</h3>
          <ul>
            <li dangerouslySetInnerHTML={{ __html: I18n.t('footer.surf_html') }} />
          </ul>
        </div>
      </div>
    </footer>
  )
}

export default Footer
